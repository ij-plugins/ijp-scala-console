import ij.*
import ij.process.*

/** Creates an image of a sphere using discontinuity in "Red/Green" lookup table */
def sphere(): Unit = {
  val size = 1024
  val ip   = new FloatProcessor(size, size)
  val t0   = System.currentTimeMillis()
  for (y <- 0 until size) {
    val dy = y - size / 2
    for (x <- 0 until size) {
      val dx = x - size / 2
      val d  = Math.sqrt(dx * dx + dy * dy).toFloat
      ip.setf(x, y, -d)
    }
  }
  val time = (System.currentTimeMillis() - t0) / 1000
  val img  = new ImagePlus(s"$time seconds", ip)
  // Apply "Red/Green" lookup table
  IJ.run(img, "Red/Green", "")
  img.show()
}

sphere()
