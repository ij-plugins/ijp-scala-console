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

import javafx.event.{ActionEvent, EventHandler}
import scalafx.scene.Node
import scalafx.scene.image.{Image, ImageView}

object Action {

  def apply(name: String, icon: Image, eventHandler: EventHandler[ActionEvent]): Action = {

    val _icon         = icon
    val _eventHandler = eventHandler

    new Action(name) {
      override def icon: Node = if (_icon != null) new ImageView(_icon) else null

      override def eventHandler: EventHandler[ActionEvent] = _eventHandler
    }
  }

}

/**
 * Simple UI action.
 */
abstract class Action(val name: String) {

  def icon: Node

  def eventHandler: EventHandler[ActionEvent]

}
