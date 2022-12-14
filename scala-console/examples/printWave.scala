import scala.math._

//
// Print a wave to the standard output
//
val scale  = 5
val xRange = Range.BigDecimal(-Pi / 2, 5.5 * Pi, Pi / 20)
val waveVertical = for (x <- xRange) yield {
  // Prepare empty line
  val line = Array.fill(scale * 2 + 1) {
    " "
  }
  // Create marker in location `y`
  val y = round((sin(x.doubleValue) + 1) * scale).toInt
  line(y) = "*"
  line
}

// Transpose and print the wave
waveVertical.transpose.foreach(w => println(w.mkString("")))
