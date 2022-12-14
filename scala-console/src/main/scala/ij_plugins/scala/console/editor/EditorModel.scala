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

package ij_plugins.scala.console.editor

import ij_plugins.scala.console.editor.Editor.EditorEvent
import ij_plugins.scala.console.editor.extra.Publisher
import scalafx.beans.binding.{Bindings, BooleanBinding}
import scalafx.beans.property.{ObjectProperty, ReadOnlyObjectProperty, ReadOnlyObjectWrapper}
import scalafx.beans.value.ObservableValue

import java.io.{File, FileWriter}

/**
 * Model of the code editor, in the MVC sense. Publishes `SourceFileEvent` when new file is read or saved to.
 *
 * @author Jarek Sacha
 * @since 2/17/12 5:57 PM
 */
private class EditorModel(private val textArea: EditorCodeArea) extends Publisher[EditorEvent] {

  private val _sourceFile = ReadOnlyObjectWrapper[Option[File]](None)

  /**
   * Source file from editor content was saved to read from.
   */
  val sourceFile: ReadOnlyObjectProperty[Option[File]] = _sourceFile.readOnlyProperty

  private val lastSavedText = ObjectProperty[Option[String]](None)

  /**
   * Text content of this editor
   */
  val text: ObservableValue[String, String] = textArea.text

  def selection: String = {
    textArea.selectedText
  }

  val needsSaving: BooleanBinding = Bindings.createBooleanBinding(
    () => {
      lastSavedText.value match {
        case Some(lastText) => !lastText.equals(textArea.text.value)
        case None           => !textArea.text.value.isEmpty
      }
    },
    lastSavedText,
    textArea.text
  )

  def reset(): Unit = {
    textArea.replaceText("")
    _sourceFile.value = None
    lastSavedText.value = Some(textArea.text.value)
  }

  def read(file: File): Unit = {
    val source = scala.io.Source.fromFile(file)
    val lines  = source.mkString
    source.close()

    lastSavedText.value = Some(lines)
    textArea.replaceText(lines)
    _sourceFile.value = Some(file)
  }

  def save(file: File): Unit = {
    val writer = new FileWriter(file)
    try {
      val t = text.value
      writer.write(t)
      lastSavedText.value = Some(t)
    } finally {
      writer.close()
    }
    _sourceFile.value = Some(file)
  }

}
