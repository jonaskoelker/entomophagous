import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

object SharedState { var ok = true }

class T1 extends AnyFlatSpec with Matchers {
  "ok" must "be true" in {
    SharedState.ok must be (true)
  }
}

class T2 extends AnyFlatSpec with Matchers {
  "ok" must "be false after negating it" in {
    SharedState.ok = !SharedState.ok
    SharedState.ok must be (false)
  }
}

class Tn extends AnyFlatSpec with Matchers {
  "Law of non-contradiction" must "hold" in {
    val example: Boolean = true
    (!(example && !example)) must be (true)
  }
}
