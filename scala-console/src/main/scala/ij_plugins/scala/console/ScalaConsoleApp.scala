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

package ij_plugins.scala.console

import org.scalafx.extras._
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.binding.Bindings
import scalafx.scene.image.Image
import scalafx.scene.{Node, Scene}
import scalafx.stage.WindowEvent

/**
 * Stand-alone Scala Console application.
 */
object ScalaConsoleApp extends JFXApp3 {

  val title = "Scala Console"

  override def start(): Unit = {

    setupUncaughtExceptionHandling(title)

    val scalaConsolePane = new ScalaConsolePane()

    stage = new PrimaryStage {
      scene = new Scene(640, 480) {
        root = scalaConsolePane.view
      }
      icons ++= loadIcons()

      // Intercept window close request
      onCloseRequest = (event: WindowEvent) => {
        if (scalaConsolePane.model.onExit()) {
          // Exiting, allow default FX close handler
        } else {
          // Do not exit, mark close request event as done
          event.consume()
        }
      }

      // Display open file name in the title
      title <== Bindings.createStringBinding(
        () => {
          val t = scalaConsolePane.model.editor.sourceFile.value match {
            case Some(f) => "Scala Console: " + f.getName
            case None    => "Scala Console"
          }

          // Add `*` when content is modified
          t + (if (scalaConsolePane.model.editor.needsSaving.value) "*" else "")
        },
        scalaConsolePane.model.editor.sourceFile,
        scalaConsolePane.model.editor.needsSaving
      )
    }
  }

  def loadIcons(): Array[javafx.scene.image.Image] = {
      val names = Array("scala16.png", "scala32.png", "scala48.png", "scala64.png")
      val path  = "/ij_plugins/scala/console/resources/"
      names.map { n => new Image(s"$path$n").delegate }
  }

  private def setupUncaughtExceptionHandling(title: String): Unit = {
    Thread.setDefaultUncaughtExceptionHandler((t: Thread, e: Throwable) => {
      e.printStackTrace()
      showException(s"$title: Unhandled exception, thread: ${t.getName}.", e)
    })

    onFXAndWait {
      Thread.currentThread().setUncaughtExceptionHandler((t: Thread, e: Throwable) => {
        e.printStackTrace()
        showException(s"$title: Unhandled FX exception, thread: ${t.getName}.", e)
      })
    }
  }

  private def showException(header: String, t: Throwable): Unit = {
    // Make sure that FX is initialized
    initFX()

    ShowMessage.exception(title, header, t, null.asInstanceOf[Node])
  }
}
