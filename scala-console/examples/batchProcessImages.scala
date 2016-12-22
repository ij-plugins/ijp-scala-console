//
// Batch process images in ImageJ applying a median filter.
//

import java.io.File

import ij.{IJ, ImagePlus}


batchProcess(
  filter = {
    imp =>
      IJ.run(imp, "Median...", "radius=4")
      imp
  },
  inputDir = new File("my_input_dir"),
  inputExtension = ".png",
  outputDir = new File("my_output_dir")
)

//-------------------------------------------------------------------------------------------------


/**
  * Apply filter to files in input directory, save modified files in the output directory.
  *
  * @param filter         operation to be applied to processed images
  * @param inputDir       input directory
  * @param inputExtension input file name extension
  * @param outputDir      output directory
  */
def batchProcess(inputDir: File,
                 inputExtension: String,
                 outputDir: File,
                 filter: ImagePlus => ImagePlus) {
  val title = "Batch Process"

  // Input directory
  if (!inputDir.exists) {
    IJ.error(title, "Input directory does not exist: " + inputDir.getAbsolutePath)
    return
  }

  // Output directory
  if (!outputDir.mkdirs()) {
    IJ.error(title, "Failed to create output directory: " + outputDir.getAbsolutePath)
    return
  }

  // List all files with extension ".png"
  val inputFiles = inputDir.listFiles.filter(_.getName.endsWith(inputExtension))

  // batch process iterating through all input files
  for (inputFile <- inputFiles) {
    println("Processing: " + inputFile.getAbsolutePath)
    val src = IJ.openImage(inputFile.getPath)
    val dest = filter(src)
    val outputFile = new File(outputDir, inputFile.getName)
    IJ.saveAs(dest, "tif", outputFile.getAbsolutePath)
  }
}