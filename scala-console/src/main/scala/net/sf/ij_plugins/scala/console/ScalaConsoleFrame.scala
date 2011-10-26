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

import net.sf.ij_plugins.scala.console.event.Changed
import swing._
import java.awt.event.ActionEvent
import java.awt.BorderLayout._
import java.awt.{Cursor, Color}
import java.util.ArrayList
import java.net.URL
import java.util.logging.{Level, Logger}
import javax.swing._
import filechooser.FileFilter
import java.io.File

/*
* Implementation of a Scala Console as 'regular' Swing JFrame, Scala.swing is still too buggy.
*
*/
object ScalaConsoleFrame {


    def main(args: Array[String]) {
        Swing.onEDT {
            try {
                val laf = UIManager.getSystemLookAndFeelClassName
                UIManager.setLookAndFeel(laf);
            } catch {
                case _ =>
            }

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

    private val defaultTitle = "Scala Console"

    private val logger: Logger = Logger.getLogger(this.getClass.getName)

    private val editor = new Editor

    private val model = new ScalaInterpreterModel

    private lazy val fileChooser = new JFileChooser() {
        setFileFilter(new FileFilter() {
            def accept(f: File) = f != null && !f.isDirectory && f.getName.endsWith(".scala")

            def getDescription = "*.scala"
        })

        this.setMultiSelectionEnabled(false)
    }


    private val outputArea = new JTextArea(10, 80) {
        setText("")
        setEditable(false)
        setBackground(new Color(255, 255, 218))
    }

    private val statusLine = new JLabel("Welcome to Scala Console")

    private val fileNewAction = new AbstractAction("New...") {
        def actionPerformed(e: ActionEvent) {

            if (editor.needsSave) {
                // Check if current document needs to be saved
                val status = JOptionPane.showConfirmDialog(ScalaConsoleFrame.this, "Do you want to save current file?", "New", JOptionPane.YES_NO_OPTION)
                // If not cancelled, save
                val saveCurrent = status match {
                    case JOptionPane.YES_OPTION => {
                        // Save
                        save()
                    }
                    case JOptionPane.NO_OPTION => {
                        /* Do nothing*/
                    }
                    case _ => return
                }
            }

            // Reset editor
            editor.reset()
        }
    }

    private val fileOpenAction = new AbstractAction("Open...") {
        def actionPerformed(e: ActionEvent) {

            val status = fileChooser.showOpenDialog(ScalaConsoleFrame.this)
            if (status != JFileChooser.APPROVE_OPTION) {
                return
            }

            val file = fileChooser.getSelectedFile
            if (file == null) {
                return
            }

            editor.read(file)
            setTitle(defaultTitle + " - " + file.getCanonicalPath)
        }
    }

    private val fileSaveAction = new AbstractAction("Save") {
        def actionPerformed(e: ActionEvent) {
            save()
        }
    }

    private val fileSaveAsAction = new AbstractAction("Save As...") {
        def actionPerformed(e: ActionEvent) {
            saveAs()
        }
    }

    private val fileExitAction = new AbstractAction("Exit") {
        def actionPerformed(e: ActionEvent) {
            ScalaConsoleFrame.this.setVisible(false)
        }
    }


    private val runAction = new AbstractAction("Run") {
        def actionPerformed(e: ActionEvent) {
            run()
        }
    }

    val topMenu = new JMenuBar

    val fileMenu = new JMenu("File")
    topMenu.add(fileMenu)

    val fileNewMenuItem = new JMenuItem(fileNewAction)
    fileMenu.add(fileNewMenuItem)

    val fileOpenMenuItem = new JMenuItem(fileOpenAction)
    fileMenu.add(fileOpenMenuItem)

    fileMenu.addSeparator()

    val fileSaveMenuItem = new JMenuItem(fileSaveAction)
    fileMenu.add(fileSaveMenuItem)

    val fileSaveAsMenuItem = new JMenuItem(fileSaveAsAction)
    fileMenu.add(fileSaveAsMenuItem)

    fileMenu.addSeparator()

    val fileExitMenuItem = new JMenuItem(fileExitAction)
    fileMenu.add(fileExitMenuItem)

    val scriptMenu = new JMenu("Script")
    topMenu.add(scriptMenu)

    val scriptRunMenuItem = new JMenuItem(runAction)
    scriptMenu.add(scriptRunMenuItem)


    private val runButton = new JButton(runAction)

    private val enablable = Array(this, runButton)


    setTitle(defaultTitle)
    setIconImages(loadIcons())

    setJMenuBar(topMenu)

    val toolBar = new JToolBar
    toolBar.add(runButton)

    add(toolBar, NORTH)
    add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, editor.view, new JScrollPane(outputArea)), CENTER)
    add(statusLine, SOUTH)




    listenTo(model)
    reactions += {
        case Changed(m) => onEDT({
            enablable.foreach(_.setEnabled(m.ready))
            setCursor(if (m.ready) Cursor.getDefaultCursor else Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))
            outputArea.setText(m.output)
            statusLine.setText(m.status)
        })
    }


    private def run() {
        statusLine.setText("Running...")
        val selection = editor.selection
        val code = if (selection != null && !selection.isEmpty) selection else editor.text
        model.run(code)
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
            new ImageIcon(url).getImage
        } catch {
            case t: Throwable => {
                logger.log(Level.WARNING, "Unable to find resource '" + path + "' for class '" + aClass.getName + "'.", t)
            }
            null
        }
    }

    private def save() {
        editor.sourceFile match {
            case Some(file) => {
                editor.save(file)
                setTitle(defaultTitle + " - " + file.getCanonicalPath)
            }
            case None => saveAs()
        }

    }

    private def saveAs(): Boolean = {
        val status = fileChooser.showSaveDialog(this)
        if (status != JFileChooser.APPROVE_OPTION) {
            return false
        }

        val file = fileChooser.getSelectedFile
        if (file == null) {
            return false;
        }

        editor.save(file)

        true
    }

}