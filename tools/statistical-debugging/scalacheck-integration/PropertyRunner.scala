import org.scalacheck.Properties

object PropertyRunner {
  def main(argv: Array[String]): Unit = {
    if (argv.isEmpty) {
      println(
        "Usage: PropertyRunner <class name> [optional main() arguments]"
      )
      System.exit(1)
    }

    Class
      .forName(argv(0))
      .newInstance()
      .asInstanceOf[Properties]
      .main(argv.drop(1))
  }
}
