//
// Apply some processing to the current image in ImageJ
//

import ij.IJ

process()

/**
  * Get a reference to currently selected image in ImageJ then apply median filter to it.
  * If no image is opened show "No image" error message.
  */
def process() {
  // Get currently selected image
  val imp = IJ.getImage
  if (imp == null) {
    // Show error message
    IJ.noImage()
    return
  }

  // Do some processing
  IJ.run(imp, "Median...", "radius=4")
  // ...
}
