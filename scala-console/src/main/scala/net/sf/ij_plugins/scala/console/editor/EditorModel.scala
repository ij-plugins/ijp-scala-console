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

package net.sf.ij_plugins.scala.console.editor

import java.io.{File, FileWriter}

import net.sf.ij_plugins.scala.console.editor.Editor.{EditorEvent, SourceFileEvent}

import scala.collection.mutable


/**
  * Model of the code editor, in the MVC sense. Publishes `SourceFileEvent` when new file is read or saved to.
  *
  * @author Jarek Sacha
  * @since 2/17/12 5:57 PM
  */
private class EditorModel(private val textArea: EditorCodeArea) extends mutable.Publisher[EditorEvent] {

  private var _sourceFile: Option[File] = None
  private var lastSavedText: Option[String] = None

  /**
    * Associated file from which the file was loaded or saved last time.
    */
  def sourceFile: Option[File] = _sourceFile

  private def sourceFile_=(file: Option[File]): Unit = {
    _sourceFile = file
    publish(SourceFileEvent(_sourceFile))
  }


  /**
    * Return a text content of this editor
    */
  def text: String = {
    textArea.text
  }

  def selection: String = {
    textArea.selectedText
  }


  def needsSave: Boolean = {
    lastSavedText match {
      case Some(lastText) => !lastText.equals(textArea.text)
      case None => !textArea.text.isEmpty
    }
  }

  def reset(): Unit = {
    textArea.text = Seq(
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
    sourceFile = None
    lastSavedText = None
  }

  def read(file: File): Unit = {
    val source = scala.io.Source.fromFile(file)
    val lines = source.mkString
    source.close()

    lastSavedText = Some(lines)
    textArea.text = lines
    sourceFile = Some(file)
  }


  def save(file: File): Unit = {
    val writer = new FileWriter(file)
    try {
      writer.write(text)
      lastSavedText = Some(text)
    } finally {
      writer.close()
    }
    sourceFile = Some(file)
  }


}
