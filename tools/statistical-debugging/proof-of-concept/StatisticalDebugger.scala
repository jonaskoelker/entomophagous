import com.sun.jdi.{
  BooleanValue,
  Bootstrap,
  Location,
  VirtualMachineManager
}
import com.sun.jdi.connect.LaunchingConnector
import com.sun.jdi.event.{
  MethodEntryEvent,
  MethodExitEvent,
  StepEvent,
  ThreadStartEvent,
  VMDeathEvent,
  VMStartEvent
}
import com.sun.jdi.request.StepRequest

import scala.collection.mutable.{Map => MutableMap}

object StatisticalDebugger {
  def main(argv: Array[String]): Unit = {
    val vmManager: VirtualMachineManager = Bootstrap.virtualMachineManager()
    val connector: LaunchingConnector = vmManager.defaultConnector()
    val environment = connector.defaultArguments()

    environment.get("main").setValue(
      "example." + argv.lift(0).getOrElse("Middle")
    )

    environment.get("options").setValue(
      "-ea -cp " + argv.lift(1).getOrElse("target/scala-2.12/classes")
    )

    val virtualMachine = connector.launch(environment)

    val eventRequestManager = virtualMachine.eventRequestManager()

    {
      eventRequestManager.createThreadStartRequest().enable()

      val methodExitRequest = eventRequestManager.createMethodExitRequest()
      methodExitRequest.addClassFilter("example.*")
      methodExitRequest.enable()

      val methodEntryRequest = eventRequestManager.createMethodEntryRequest()
      methodEntryRequest.addClassFilter("example.*")
      methodEntryRequest.enable()
    }

    var steps: List[StepEvent] = Nil
    var bad_runs = 0
    var good_runs = 0

    val good_locations: MutableMap[Location, Int] = MutableMap.empty
    val bad_locations: MutableMap[Location, Int] = MutableMap.empty

    val queue = virtualMachine.eventQueue()
    while (true) {
      val eventSet = queue.remove()
      val eventIterator = eventSet.eventIterator()
      while (eventIterator.hasNext()) {
        val event = eventIterator.nextEvent()
        event match {
          case ev: VMStartEvent => ()

          case ev: ThreadStartEvent =>
            val stepRequest = eventRequestManager.createStepRequest(
              ev.thread(),
              StepRequest.STEP_MIN,
              StepRequest.STEP_INTO
            )
            stepRequest.addClassFilter("example.*")
            stepRequest.enable()

          case ev: StepEvent =>
            steps = ev :: steps

          case ev: MethodEntryEvent =>
            if (ev.method().name == "runTest")
              steps = Nil

          case ev: MethodExitEvent =>
            if (ev.method().name == "runTest")
              ev.returnValue() match {
                case v: BooleanValue =>
                  print(if (v.value) '.' else 'x')
                  System.out.flush()
                  val map = if (v.value) {
                    good_runs += 1
                    good_locations
                  } else {
                    bad_runs += 1
                    bad_locations
                  }
                  for (step <- steps) {
                    val location = step.location()
                    map(location) = 1 + map.getOrElse(location, 0)
                  }
                  steps = Nil
                case _ =>
                  ()
              } else if (ev.method().name == "main") {
                println("")
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

                val filtered_bad_locations = bad_locations
                  .filter {
                    case (location, bad_count) =>
                      val good_count = good_locations.getOrElse(location, 0)
                      val ratio = bad_count / bad_runs
                      (bad_count, good_count) != (bad_runs * ratio, good_runs * ratio)
                  }

                val baseline_bad_locations =
                  if (filtered_bad_locations.size > 0)
                    filtered_bad_locations
                  else
                    bad_locations

                val sorted_bad_locations =
                  baseline_bad_locations
                  .toArray
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
                            loc1.method == loc2.method && (
                              loc1.codeIndex <= loc2.codeIndex
                            )
                          )
                        )
                      )
                  }

                for ((loc, count) <- sorted_bad_locations)
                  println(
                    s"%6.2f%%:  ${loc.sourceName}:%-3d  ${loc.method}  ${loc.codeIndex}"
                      .format(count * 100, loc.lineNumber)
                  )
              }

          case done: VMDeathEvent =>
            return

          case ev =>
            println(s"Unmatched event: ${ev}")
        }
      }
      virtualMachine.resume()
    }
  }
}
