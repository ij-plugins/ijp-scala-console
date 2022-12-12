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

import net.sf.ij_plugins.scala.console.outputarea.OutputArea
import org.scalafx.extras.mvcfx.MVCfx

import java.io.File

/**
 * Loader for the Scala Console main pane.
 */
class ScalaConsolePane extends MVCfx("ScalaConsolePane.fxml") {

  val outputArea: OutputArea = new OutputArea()

  override lazy val model: ScalaConsolePaneModel = new ScalaConsolePaneModel()

  def loadScriptFile(file: File): Unit = {
    model.editor.read(file)
  }
}
