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

package ij_plugins.scala.console.plugins

import ij.IJ
import ij.plugin.PlugIn
import ij_plugins.scala.console.ScalaConsolePane
import org.scalafx.extras.{initFX, onFX}
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.stage.Stage

import java.io.File

private object ScalaConsolePlugin {
  addPluginsJarsToClassPath()
}

/**
 * ImageJ plugin for starting Scala Console.
 */
class ScalaConsolePlugin extends PlugIn {

  def run(arg: String): Unit = {
    IJ.showStatus("Starting Scala Console...")

    initFX()

    val scripFile = if (arg != null && arg.nonEmpty) {
      val file = new File(arg.trim)
      if (file.exists) Some(file) else None
    } else {
      None
    }

    onFX {
      try {
        val iconImages = {
          val names = Array("scala16.png", "scala32.png", "scala48.png", "scala64.png")
          val path  = "/net/sf/ij_plugins/scala/console/resources/"
          names.map { n => new Image(s"$path$n").delegate }
        }

        val scalaConsole = new ScalaConsolePane()
        new Stage {
          scene = new Scene(640, 480) {
            title = "Scala Console"
            root = scalaConsole.view
          }
          icons ++= iconImages
        }.show()

        scripFile.foreach { file => scalaConsole.loadScriptFile(file) }
      } catch {
        case ex: Throwable =>
          ex.printStackTrace()
          IJ.error("Scala Console Plugin", "Failed to start Scala Console. " + ex.getMessage)
      } finally {
        IJ.showStatus("")
      }
    }
  }

}
