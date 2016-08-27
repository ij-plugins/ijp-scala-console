/*
 *  ImageJ Plugins
 *  Copyright (C) 2002-2016 Jarek Sacha
 *  Author's email: jpsacha at gmail dot com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   Latest release available at https://github.com/ij-plugins
 */

import java.io.File

import ij.{IJ, ImagePlus}

//
// Batch process images applying a median filter.
//
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