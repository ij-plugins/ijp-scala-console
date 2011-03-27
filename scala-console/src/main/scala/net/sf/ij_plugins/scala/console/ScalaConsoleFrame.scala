/*
 * Image/J Plugins
 * Copyright (C) 2002-2011 Jarek Sacha
 * Author's email: jsacha at users dot sourceforge dot net
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
 * Latest release available at http://sourceforge.net/projects/ij-plugins/
 */

package net.sf.ij_plugins.scala.console

import event.Changed
import swing._
import javax.swing._
import java.awt.event.ActionEvent
import java.awt.BorderLayout._
import java.awt.{Cursor, Color}
import java.net.URL
import java.util.logging.{Level, Logger}
import java.util.ArrayList


/*
* Implementation of a Scala Console as 'regular' Swing JFrame, Scala.swing is still too buggy.
*
*/
object ScalaConsoleFrame {


  def main(args: Array[String]) {
    Swing.onEDT {
      val frame = new ScalaConsoleFrame
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      frame.pack()
      frame.setVisible(true)
    }
  }
}


/**
 * Implements the view part of the Scala Console.
 */
class ScalaConsoleFrame extends JFrame with Reactor {

  private val logger: Logger = Logger.getLogger(this.getClass.getName)

  private val model = new ScalaConsoleModel

  private val editor = new JTextArea(25, 80) {
    setText("import ij._\n" +
            "val img = WindowManager.getCurrentImage()")
  }

  private val outputArea = new JTextArea(10, 80) {
    setText("")
    setEditable(false)
    setBackground(new Color(255, 255, 218))
  }

  private val statusLine = new JLabel("Welcome to Scala Console")

  private val runAction = new AbstractAction("Run") {
    def actionPerformed(e: ActionEvent) {
      run()
    }
  }

  private val runButton = new JButton(runAction)

  private val enablable = Array(this, runButton)


  setTitle("Scala Console")
  setIconImages(loadIcons())
  add(runButton, NORTH)
  add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(editor), new JScrollPane(outputArea)), CENTER)
  add(statusLine, SOUTH)




  listenTo(model)
  reactions += {
    case Changed(model) => onEDT({
      enablable.foreach(_.setEnabled(model.ready))
      setCursor(if (model.ready) Cursor.getDefaultCursor else Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))
      outputArea.setText(model.output)
      statusLine.setText(model.status)
    })
  }


  private def run() {
    statusLine.setText("Running...")
    model.run(editor.getText)
  }


  private def onEDT(op: => Unit) {
    if (SwingUtilities.isEventDispatchThread) {
      op
    } else {
      Swing.onEDTWait(op)
    }
  }


  private def loadIcons(): java.util.List[Image] = {
    val names = Array("scala16.png", "scala32.png", "scala48.png", "scala64.png")
    val icons = new ArrayList[Image]
    for (name <- names) icons.add(loadImage(this.getClass, "resources/" + name))
    icons
  }


  /**
   * Load image as a resource for given class without throwing exceptions.
   * Intended for use with {@link JFrame#setIconImage}
   *
   * @param aClass Class requesting resource.
   * @param path   Image file path.
   * @return Image or null if loading failed.
   */
  private def loadImage(aClass: Class[_], path: String): Image = {
    try {
      val url: URL = aClass.getResource(path)
      if (url == null) {
        logger.log(Level.WARNING, "Unable to find resource '" + path + "' for class '" + aClass.getName + "'.")
        return null
      }
      return new ImageIcon(url).getImage
    } catch {
      case t: Throwable => {
        logger.log(Level.WARNING, "Unable to find resource '" + path + "' for class '" + aClass.getName + "'.", t)
      }
    }
    return null
  }
}