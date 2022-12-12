/*
 *  ImageJ Plugins
 *  Copyright (C) 2002-2022 Jarek Sacha
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

package net.sf.ij_plugins.scala.console.editor

import scalafx.application.JFXApp3
import scalafx.scene.Scene

/**
 * Simple demo of using EditorCodeArea.
 */
object ScalaKeywordsDemo extends JFXApp3 {

  override def start(): Unit = {
    val editorCodeArea = new EditorCodeArea()

    val sampleCode: String = Seq(
      "import scala.math._",
      "",
      "/*",
      "* Print a wave to the standard output",
      "*/",
      "val scale = 2",
      "for (x <- Range.Double(-Pi / 2, 3.5 * Pi, Pi / 5)) {",
      "  // Prepare empty line",
      "  val line = Array.fill(scale * 2 + 1) {\" \"}",
      "  // Create marker at location `y`",
      "  val y = round((sin(x) + 1) * scale).toInt",
      "  line(y) = \"*\"",
      "  // Print line as string",
      "  println(line.mkString(\" \"))",
      "}"
    ).mkString("\n")

    editorCodeArea.replaceText(sampleCode)

    stage = new JFXApp3.PrimaryStage {
      title = "Scala Keywords Demo"
      scene = new Scene(640, 480) {
        root = editorCodeArea.view
      }
    }
  }
}
