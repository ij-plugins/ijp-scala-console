/*
 * ImageJ Plugins
 * Copyright (C) 2002-2016 Jarek Sacha
 * Author's email: jpsacha at gmail dot com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Latest release available at https://github.com/ij-plugins
 *
 */

package net.sf.ij_plugins.scala.console

import net.sf.ij_plugins.scala.console.ScalaInterpreter._
import net.sf.ij_plugins.scala.console.editor.Editor
import net.sf.ij_plugins.scala.console.outputarea.OutputArea
import org.scalafx.extras.modelview.Model
import org.scalafx.extras.onFX

import scala.collection.mutable
import scala.tools.nsc.interpreter.Results
import scalafx.application.Platform
import scalafx.beans.property.{ReadOnlyBooleanProperty, ReadOnlyBooleanWrapper, StringProperty}

/**
  * UI model for the Scala Console main pane.
  */
class ScalaConsolePaneModel extends Model {

  val statusText = new StringProperty("Welcome to Scala Console")
  val editor = new Editor()
  val outputArea = new OutputArea()

  private val _isReady = new ReadOnlyBooleanWrapper(this, "isReady", true)
  val isReady: ReadOnlyBooleanProperty = _isReady.readOnlyProperty

  private val scalaInterpreter = new ScalaInterpreter()

  val interpreterReactions =
    new mutable.Subscriber[InterpreterEvent, mutable.Publisher[InterpreterEvent]] {
      override def notify(pub: mutable.Publisher[InterpreterEvent], event: InterpreterEvent): Unit = {
        event match {
          case StateEvent(s) => onFX {
            _isReady.value = s == State.Ready
            statusText.value = s.entryName
          }
          //          enablable.foreach(_.enabled = isReady)
          //          cursor = if (isReady) Cursor.getDefaultCursor else Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)

          case ErrStreamEvent(data) => outputArea.model.appendErrStream(data)
          case OutStreamEvent(data) => outputArea.model.appendOutStream(data)
          case InterpreterLogEvent(data) => outputArea.model.appendInterpreterOut(data)
          case ResultEvent(result) =>
            result match {
              case Results.Error => outputArea.model.appendErrStream(result.toString)
              case Results.Success => outputArea.model.appendOutStream(result.toString)
              case Results.Incomplete => outputArea.model.appendErrStream(result.toString)
            }

          //        case SourceFileEvent(fileOption) => fileOption match {
          //          case Some(file) => title = defaultTitle + " - " + file.getCanonicalPath
          //          case None => title = defaultTitle
          //        }
          //
          //        case WindowClosing(_) => editor.prepareToClose()
          case x => throw new Exception("Unrecognised event: " + x)
        }
      }
    }

  scalaInterpreter.subscribe(interpreterReactions)

  def onRun(): Unit = {
    outputArea.model.clear()

    // Use selection if not empty
    val selection = editor.selection
    val code = if (selection != null && !selection.isEmpty) selection else editor.text

    // Show which code will be run
    outputArea.model.list(code)

    // Run the code
    scalaInterpreter.run(code)
  }

  /**
    * Exit application if no saving is needed or saving is done.
    * Do not exit if user cancelled saving request.
    *
    * @return `true` when exited (application may terminate before returning).
    *         `false` if exit was canceled.
    */
  def onExit(): Boolean = {
    // Check if content needs to be saved
    if (editor.prepareToClose()) {
      Platform.exit()
      true
    } else {
      false
    }
  }

}
