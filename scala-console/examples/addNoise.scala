/////////////////////////////////////////////////////////////////////////////
//        .__     __                 .__               .__                 //
//       |__|   |__|         ______ |  |  __ __  ____ |__| ____   ______   //
//      |  |   |  |  ______ \____ \|  | |  |  \/ ___\|  |/    \ /  ___/    //
//     |  |   |  | /_____/ |  |_> >  |_|  |  / /_/  >  |   |  \\___ \      //
//    |__/\__|  |         |   __/|____/____/\___  /|__|___|  /____  >      //
//      \______|         |__|             /_____/         \/     \/        //
//                                                                         //
/////////////////////////////////////////////////////////////////////////////


import ij.IJ._
import ij.ImagePlus
import ij.WindowManager._


// get the current image
val imp = getCurrentImage

// check that it's a valid image and if so start processing it!
if (imp == null) {
  noImage()
} else {
  var ip = imp.getProcessor.crop()
  ip = ip.convertToByte(false)

  // add random noise to its pixels
  val width = ip.getWidth
  val height = ip.getHeight
  for (x <- 0 until width) {
    showProgress(x, width)
    for (y <- 0 until height) {
      val noise = math.round(math.random * 255 - 128).asInstanceOf[Int]
      ip.putPixel(x, y, ip.getPixel(x, y) + noise)
    }
  }
  showProgress(width, width)

  // show it in a new image
  ip.resetMinAndMax()
  new ImagePlus("Noisy", ip).show()
}

