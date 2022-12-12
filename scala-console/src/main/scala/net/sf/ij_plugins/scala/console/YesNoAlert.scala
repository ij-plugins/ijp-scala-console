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

package net.sf.ij_plugins.scala.console

import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.stage.Window

/**
 * Show a confirmation dialog with "Yes", "No", and "Cancel" buttons.
 */
object YesNoAlert {
  val ButtonTypeYes    = new ButtonType("Yes")
  val ButtonTypeNo     = new ButtonType("No")
  val ButtonTypeCancel = new ButtonType("Cancel", ButtonData.CancelClose)

  /**
   * {{{
   *   val alert = YesNoAlert(
   *     parent = stage
   *     title = "Confirmation Dialog with Custom Actions",
   *     header = "Look, a Confirmation Dialog with Custom Actions.",
   *     content = "Choose your option."
   *   )
   *
   *   val result = alert.showAndWait()
   *
   *   result match {
   *     case Some(ButtonTypeYes) => println("... user chose \"Yes\"")
   *     case Some(ButtonTypeNo) => println("... user chose \"No\"")
   *     case _ => println("... user chose CANCEL or closed the dialog")
   *   }
   * }}}
   *
   * @param parent  parent window.
   * @param title   title of the dialog.
   * @param header  header text.
   * @param content main content.
   * @return selected button.
   */
  def apply(parent: Window, title: String, header: String, content: String): Alert = {
    val _t = title

    new Alert(AlertType.Confirmation) {
      initOwner(parent)
      title = _t
      headerText = header
      contentText = content
      // Note that we override here default dialog buttons, OK and Cancel,
      // with new ones.
      // We could also just add to existing button using `++=`.
      buttonTypes = Seq(
        ButtonTypeYes,
        ButtonTypeNo,
        ButtonType.Cancel
      )
    }
  }
}
