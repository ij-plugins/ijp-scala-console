import scala.math._

//
// Print a wave to the standard output
//
val scale = 2
for (x <- Range.Double(-Pi / 2, 3.5 * Pi, Pi / 5)) {
  // Prepare empty line
  val line = Array.fill(scale * 2 + 1)(" ")
  // Create marker at location `y`
  val y = round((sin(x) + 1) * scale).toInt
  line(y) = "*"
  // Print line as string
  println(line.mkString(" "))
}