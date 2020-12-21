import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}

object PropertyTests extends Properties("[Middle and GCD]") {

  val sizedInt = Gen.sized(n => {
    if (n < 90) {
      val k = 1 << ((n / 3) + 2)
      Gen.choose(-k, k - 1) // -2**m to 2**m-1 for m=2..31
    } else {
      Gen.oneOf(
        Gen.oneOf(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE),
        Gen.choose(Integer.MIN_VALUE, Integer.MAX_VALUE)
      )
    }
  })

  val gcdInt, middleInt = sizedInt

  property("middle computes the median") =
    forAll(middleInt, middleInt, middleInt) {
      case (x, y, z) =>
        val observedMiddle = Implementation.middle(x, y, z)
        val sorted = List(x, y, z).sorted
        val expectedMiddle = sorted(1)
        observedMiddle == expectedMiddle
    }

  property("gcd1 computes the gcd") = forAll(gcdInt, gcdInt) {
    check_gcd(_, _, Implementation.gcd1)
  }

  property("gcd2 computes the gcd") = forAll(gcdInt, gcdInt) {
    check_gcd(_, _, Implementation.gcd2)
  }

  property("gcd3 computes the gcd") = forAll(gcdInt, gcdInt) {
    check_gcd(_, _, Implementation.gcd3)
  }

  property("gcd4 computes the gcd") = forAll(gcdInt, gcdInt) {
    check_gcd(_, _, Implementation.gcd4)
  }

  def check_gcd(x: Int, y: Int, gcd: (Int, Int) => Int): Boolean = {
    val result = gcd(x, y)

    // 0 and Integer.MIN_VALUE are special cases
    if (x == 0 && y == 0) return result == 0
    if (result == 0) return false

    val zeroOrMinValue = Seq(0, Integer.MIN_VALUE)
    if (zeroOrMinValue.contains(x) && zeroOrMinValue.contains(y))
      return result == Integer.MIN_VALUE

    if (result < 0) return false

    // if 0 is in {x, y} then result = abs(z) where z is in {x, y} \ {0}
    if (x == 0) return result == y || result == -y
    if (y == 0) return result == x || result == -x

    // result is a common divisor of x and y
    if (x % result != 0) return false
    if (y % result != 0) return false

    // x and y have no common divisors outside of result
    if (gcd(x / result, y / result) != 1) return false

    return true
  }
}
