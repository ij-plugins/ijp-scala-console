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
import scalafx.beans.binding.BooleanBinding
import scalafx.beans.property.ReadOnlyObjectProperty
import scalafx.scene.Node

import java.io.File

object Editor {

  /**
   * Event marker trait.
   */
  trait EditorEvent

  /**
   * Notifies that file name associated with the editor changed.
   * Informs what file was saved or opened in the editor.
   */
  case class SourceFileEvent(file: Option[File]) extends EditorEvent

}

/**
 * Code input area of the console.  Creates MVC components and gives access to the view, and externally relevant
 * parts of the model (selection and text) and controller (actions).
 *
 * Publishes event [[net.sf.ij_plugins.scala.console.editor.Editor.SourceFileEvent]]
 */
class Editor extends Publisher[EditorEvent] {

  private val editorCodeArea = new EditorCodeArea()

  private lazy val _view       = editorCodeArea.view
  private lazy val _model      = new EditorModel(editorCodeArea)
  private lazy val _controller = new EditorController(null, _model)

  _model.subscribe((_: Publisher[EditorEvent], event: EditorEvent) => {
    // Just forward event from the model
    publish(event)
  })

  // Initialize editor
  _model.reset()

  /**
   * Component displaying content of this editor
   */
  def view: Node = _view

  /**
   * Currently selected text in the editor. May be an empty string.
   */
  def selection: String = _model.selection

  /**
   * Full editor content.
   */
  def text: String = _model.text.value

  /**
   * Source file from editor content was saved to read from.
   */
  val sourceFile: ReadOnlyObjectProperty[Option[File]] = _model.sourceFile

  val needsSaving: BooleanBinding = _model.needsSaving

  /**
   * Actions for file menu.
   */
  def fileActions: Seq[Action] = _controller.fileActions

  /**
   * Perform operations needed to safely close the editor, save files, etc.
   *
   * Return 'true' if can be closed.
   */
  def prepareToClose(): Boolean = {
    _controller.prepareToClose()
  }

  def read(file: File): Unit = {
    _controller.read(file)
  }

}
