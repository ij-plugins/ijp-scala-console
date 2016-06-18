/*
 *  ImageJ Plugins
 *  Copyright (C) 2002-2016 Jarek Sacha
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

package net.sf.ij_plugins.scala

import java.awt.Font
import java.io.{File, FileFilter, FilenameFilter}
import java.net.URL
import java.util.logging.{Level, Logger}
import javax.swing.ImageIcon

import ij.Menus

import scala.collection.mutable.ArrayBuffer
import scala.swing._
import scala.tools.nsc.io.Path


/**
  * Helper methods used in package `net.sf.ij_plugins.scala.console`.
  *
  * @author Jarek Sacha
  * @since 2/17/12
  */
package object console {
  private lazy val logger: Logger = Logger.getLogger(this.getClass.getName)

  // Look for one of the preferred fonts, if cannot find use default mono-spaced font
  private lazy val _defaultEditorFont: Font =
    Option(Font.decode("consolas-plain")).getOrElse(
      Option(Font.decode("Lucida Sans Typewriter Regular")).getOrElse(
        Font.getFont(Font.MONOSPACED)
      )
    )


  def defaultEditorFont: Font = _defaultEditorFont

  def loadIcon(aClass: Class[_], path: String): ImageIcon = {
    try {
      val url: URL = aClass.getResource(path)
      if (url == null) {
        logger.log(Level.WARNING, "Unable to find resource '" + path + "' for class '" + aClass.getName + "'.")
        return null
      }
      new ImageIcon(url)
    } catch {
      case t: Throwable => {
        logger.log(Level.WARNING, "Unable to find resource '" + path + "' for class '" + aClass.getName + "'.", t)
      }
        null
    }
  }


  /**
    * Load image as a resource for given class without throwing exceptions.
    * Intended for use with `javax.swing.JFrame#setIconImage`.
    *
    * @param aClass Class requesting resource.
    * @param path   Image file path.
    * @return Image or null if loading failed.
    */
  def loadImage(aClass: Class[_], path: String): Image = {
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

  def addPluginsJarsToClassPath() {
    // TODO do not add existing entries to the classpath again
    var classpath = System.getProperty("java.class.path")

    val jars = listJarFiles(listDirectories(new File(Menus.getPlugInsPath)))
    for (jar <- jars) {
      classpath = jar.getAbsolutePath + File.pathSeparator + classpath
    }

    System.setProperty("java.class.path", classpath)
  }


  def listAllJarFiles(root: File): Array[File] = {
    val r = new ArrayBuffer[File]()
    Path(root).walk.filter(e => e.toString().contains(".jar")).foreach(p => r.append(p.jfile))
    r.toArray
  }


  def listPluginDirectories() {
    val jars = listJarFiles(listDirectories(new File(Menus.getPlugInsPath)))
    for (jar <- jars) {
      println("jar: " + jar.getAbsolutePath)
    }
  }

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def listDirectories(root: File): Array[File] = {
    val dirFilter = new FileFilter() {
      override def accept(file: File) = file.isDirectory
    }

    val r = ArrayBuffer(root)
    for (dir <- root.listFiles(dirFilter)) {
      r.appendAll(listDirectories(dir))
    }
    r.toArray
  }


  def listJarFiles(dirs: Array[File]): Array[File] = {
    val r = ArrayBuffer[File]()
    for (dir <- dirs) {
      r.appendAll(listJarFiles(dir))
    }
    r.toArray
  }


  def listJarFiles(dir: File): Array[File] = {
    val jarFilter = new FilenameFilter() {
      override def accept(dir: File, name: String) = name.toLowerCase.endsWith(".jar")
    }

    dir.listFiles(jarFilter)
  }

}
