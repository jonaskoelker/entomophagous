import com.sun.jdi.{
  AbsentInformationException,
  Bootstrap,
  ClassType,
  Location,
  Method,
  ObjectReference,
  VirtualMachineManager
}

import com.sun.jdi.connect.LaunchingConnector

import com.sun.jdi.event.{
  BreakpointEvent,
  ClassPrepareEvent,
  MethodExitEvent,
  StepEvent,
  VMDeathEvent,
  VMDisconnectEvent,
  VMStartEvent
}

import com.sun.jdi.request.{
  EventRequest,
  StepRequest
}

import java.io.{
  BufferedReader,
  IOException,
  InputStream,
  InputStreamReader,
  OutputStream,
  PrintStream
}

import scala.collection.JavaConverters._
import scala.collection.mutable.{Map => MutableMap}
import scala.util.Try

object StatisticalDebugger {
  def main(argv: Array[String]): Unit = {

    val vmManager: VirtualMachineManager = Bootstrap.virtualMachineManager()
    val connector: LaunchingConnector = vmManager.defaultConnector()
    val environment = connector.defaultArguments()

    val environmentVariables = System.getenv().asScala
    val optionsName = "ENTOMOPHAGOUS_STATISTICAL_DEBUGGER_OPTIONS"
    val mainName = "ENTOMOPHAGOUS_STATISTICAL_DEBUGGER_MAIN"
    val (options, main) = ((
      environmentVariables.get(optionsName),
      environmentVariables.get(mainName)
    ) match {
      case (Some(options), Some(main)) => (options, main)
      case _ =>
        println(s"""Required environment variables missing:
${optionsName}: jvm options (including, importantly, classpath)
${mainName}: specifies the main class, e.g. "scala.tools.nsc.MainGenericRunner [PropertyRunner] <some test object or class> [-f <regexp>]"
""")
        return System.exit(1)
    })

    environment.get("options").setValue(options)
    environment.get("main").setValue(main)

    val virtualMachine = connector.launch(environment)

    val redirectStdout = new Redirect(
      "Redirect stdout",
      virtualMachine.process.getInputStream(),
      System.out
    )

    val redirectStderr = new Redirect(
      "Redirect stderr",
      virtualMachine.process().getErrorStream(),
      System.err
    )

    val eventRequestManager = virtualMachine.eventRequestManager()

    val propClassName = "org.scalacheck.Prop$"
    val testClassName = "org.scalacheck.Test$"

    {
      val classPrepareRequest1 =
        eventRequestManager.createClassPrepareRequest()
      classPrepareRequest1.addClassFilter(propClassName)
      classPrepareRequest1.enable()

      val classPrepareRequest2 =
        eventRequestManager.createClassPrepareRequest()
      classPrepareRequest2.addClassFilter(testClassName)
      classPrepareRequest2.enable()
    }

    var steps: List[StepEvent] = Nil
    var bad_runs = 0
    var good_runs = 0
    var exitMethod: Method = null
    var stepping = false

    val good_locations: MutableMap[Location, Int] = MutableMap.empty
    val bad_locations: MutableMap[Location, Int] = MutableMap.empty

    def isGoodResult(name: String): Boolean = {
      val result = ("org.scalacheck.Prop$False$" != name) && ("org.scalacheck.Prop$Exception" != name)
      result
    }

    val queue = virtualMachine.eventQueue()
    while (true) {
      val eventSet = queue.remove()
      val eventIterator = eventSet.eventIterator()
      while (eventIterator.hasNext()) {
        val event = eventIterator.nextEvent()
        event match {
          case ev: ClassPrepareEvent =>
            val classType: ClassType = ev
              .referenceType().asInstanceOf[ClassType]
            val classTypeName = classType.name
            if (classTypeName == testClassName) {
              val methodExitRequest = eventRequestManager
                .createMethodExitRequest()
              methodExitRequest.addClassFilter(testClassName)
              methodExitRequest.enable()
            } else if (classTypeName == propClassName) {
              val entryPoint = classType.concreteMethodByName(
                "slideSeed",
                "(Lorg/scalacheck/Gen$Parameters;)Lorg/scalacheck/Gen$Parameters;"
              ).locationOfCodeIndex(0)

              eventRequestManager
                .createBreakpointRequest(entryPoint)
                .enable()

              exitMethod = classType.concreteMethodByName(
                "org$scalacheck$Prop$$provedToTrue",
                "(Lorg/scalacheck/Prop$Result;)Lorg/scalacheck/Prop$Result;"
              )

              val methodExitRequest = eventRequestManager
                .createMethodExitRequest()
              methodExitRequest.addClassFilter(propClassName)
              methodExitRequest.enable()
            }

          case ev: BreakpointEvent =>
            if (!stepping) {
              val stepRequest = eventRequestManager.createStepRequest(
                ev.thread(),
                StepRequest.STEP_MIN,
                StepRequest.STEP_INTO
              )

              List("java*", "jdk*", "sun*", "org.scalacheck.*", "scala.*")
                .foreach(stepRequest.addClassExclusionFilter)
              stepRequest.enable()

              eventRequestManager
                .breakpointRequests().asScala.toList.foreach {
                  case request =>
                    request.disable()
                    eventRequestManager.deleteEventRequest(request)
                }
            }

          case ev: StepEvent =>
            steps = ev :: steps

          case ev: MethodExitEvent =>
            val method = ev.method
            if (method == exitMethod && steps != Nil) {
	      val retval = ev.returnValue()
              retval match {
                case ref: ObjectReference =>
                  val statusField = ref.referenceType.fieldByName("status")
                  val statusValue = ref.getValue(statusField)
                  val statusTypeName = statusValue
                    .asInstanceOf[ObjectReference]
                    .referenceType
                    .name

                  val map = if (isGoodResult(statusTypeName)) {
                    print(".")
                    good_runs += 1
                    good_locations
                  } else {
                    print("x")
                    bad_runs += 1
                    bad_locations
                  }

                  for (step <- steps) {
                    val location = step.location()
                      map(location) = 1 + map.getOrElse(location, 0)
                  }

                  steps = Nil

                case _ => ()
              }
            } else if (method.name == "checkProperties") {
              return done()
            }

          case ev: VMDisconnectEvent =>
            return done()

          case ev: VMDeathEvent =>
            return done()

          case ev: VMStartEvent =>
            ()

          case _ =>
            println(s"Unknown event: ${event}")
        }
      }
      virtualMachine.resume()
    }

    def done(): Unit = return analyze(
      bad_locations,
      good_locations,
      bad_runs,
      good_runs
    )
  }

  def analyze(
    bad_locations: MutableMap[Location, Int],
    good_locations: MutableMap[Location, Int],
    bad_runs: Int,
    good_runs: Int
  ): Unit = {
    if (bad_locations.isEmpty) {
      println("NO FAILING RUNS")
      return
    }

    assert(bad_runs > 0)
    val bad_runs_f = bad_runs.asInstanceOf[Double]
    val bad_ratio = bad_runs_f / (bad_runs_f + good_runs)
    val total_runs = good_runs + bad_runs
    println(
      s"Bad runs: ${bad_runs} out of ${total_runs} (%.2f%%)"
        .format(bad_ratio * 100)
    )

    val weighted_bad_locations = bad_locations
      .toArray
      .filter {
        case (location, bad_count) =>
          val good_count = good_locations.getOrElse(location, 0)
          val ratio = bad_count / bad_runs
          (bad_count * ratio, good_count * ratio) != (bad_runs, good_runs)
      }
      .map {
        case (location, bad_count) =>
          require(bad_count > 0)
          val bad_count_f = bad_count.asInstanceOf[Double]
          val good_count = good_locations.getOrElse(location, 0)
          val good_count_f = good_count.asInstanceOf[Double]
          (location, bad_count_f / (bad_count_f + good_count_f))
      }
      .sortWith {
        case ((loc1, count1), (loc2, count2)) =>
          count1 > count2 || (
            count1 == count2 && (
              loc1.method.name < loc2.method.name || (
                loc1.method.name == loc2.method.name && (
                  loc1.codeIndex <= loc2.codeIndex
                )
              )
            )
          )
      }

    val ordered_bad_locations = weighted_bad_locations
    for ((loc, count) <- ordered_bad_locations) {
      val locationIndicator = computeLocationIndicator(loc)
      println(s"%6.2f%%: ${locationIndicator} ${loc.method} ${loc.codeIndex}".format(count * 100))
    }
  }

  def computeLocationIndicator(loc: Location): String =
    Try(s"${loc.sourcePath}:${loc.lineNumber}")
      .getOrElse(loc.toString)
}

class Redirect(
  threadName: String,
  source: InputStream,
  sink: PrintStream
) extends Thread(threadName) {

  val buffering = new BufferedReader(new InputStreamReader(source))

  override def run(): Unit = {
    while (!isInterrupted()) {
      val string = buffering.readLine()
      if (string == null) { return }
      sink.println(string)
    }
  }

  setDaemon(true)
  start()
}
