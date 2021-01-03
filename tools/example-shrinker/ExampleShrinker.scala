import org.scalacheck.{Gen, Properties, Shrink, Test}
import org.scalacheck.Prop.forAll

abstract class ExampleShrinker(name: String) extends Properties(name)
{
  type Input
  type Shrinker = Shrink[Input]

  protected def knownFailingExample: Input
  protected def runSut(input: Input): Boolean
  protected implicit def shrinker: Shrinker

  property("automatic-simplification") = {
    forAll(Gen.const(knownFailingExample)) { input => runSut(input) }
  }

  override def overrideParameters(p: Test.Parameters): Test.Parameters =
    p.withMinSuccessfulTests(1)
}
