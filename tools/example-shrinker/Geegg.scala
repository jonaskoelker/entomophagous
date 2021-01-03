object Geegg {
  // From exercise 5.1 of Why Programs Fail (second edition, p. 124).
  def geegg(s: String): Boolean =
    s.count(_ == 'g') >= 3 || s.count(_ == 'e') >= 2
}

object GeeggExample extends ExampleShrinker("Geegg") {
  type Input = String
  val knownFailingExample: Input = "a-debugging-exam"
  // Deliberately fail just to exhibit the shrinking procedure.
  def runSut(s: Input): Boolean = { println(s); !Geegg.geegg(s) }
  val shrinker: Shrinker = implicitly
}

/*
 * The ddmin algorithm in Why Programs Fail omits elements from a list but
 * does not simplify individual elements.  ScalaCheck's shrinker does.
 *
 * If you want to only omit elements but not shrink them, use
 * OnlyOmitElements.shrinker instead of implicitly above.
 */
object OnlyOmitElements {
  import org.scalacheck.Shrink

  def shrinker: Shrink[String] = Shrink {
    s => implicitly[Shrink[Set[Int]]]
      .shrink(Set(0 until s.size: _*))
      .map(s.zipWithIndex.collect { case (c, i) if _(i) => c }.mkString)
  }
}
