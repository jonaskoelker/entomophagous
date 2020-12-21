object Implementation extends App {

  println(s"middle(1, 0, 2) = ${middle(1, 0, 2)}")
  println(s"gcd1(-8, -6) = %2d".format(gcd1(-8, -6)))
  println(s"gcd2(-8, -6) = %2d".format(gcd2(-8, -6)))
  println(s"gcd3(-8, -6) = %2d".format(gcd3(-8, -6)))
  println(s"gcd4(-8, -6) = %2d".format(gcd4(-8, -6)))

  def middle(x: Int, y: Int, z: Int): Int = {
    var m = z
    if (y < z) {
      if (x < y)
        m = y
      else if (x < z)
        m = y
    } else {
      if (x > y)
        m = y
      else if (x > z)
        m = x
    }
    m
  }

  def gcd1(a: Int, b: Int): Int = {
    var d = b
    var r = a % b

    while (r > 0) {
      var n = d
      d = r
      r = n / d
    }

    d
  }

  def gcd2(x: Int, y: Int): Int = {
    var (a, b) = (x, y)
    while (b != 0) {
      val t = a % b
      b = t
      a = b
    }
    a
  }

  def gcd3(a: Int, b: Int): Int =
    if (b == 0)
      a
    else
      gcd3(b, a % b)

  def gcd4(a: Int, b: Int): Int =
    iabs(gcd3(a, b))

  def iabs(a: Int): Int = a max -a
}
