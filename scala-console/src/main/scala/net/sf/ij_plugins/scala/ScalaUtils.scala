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

package net.sf.ij_plugins.scala

import collection.mutable.ArrayBuffer
import ij.Menus
import java.io._
import tools.nsc.io.Path
import scala.Array


/**
 */
object ScalaUtils {

    def addPluginsJarsToClassPath() {
        // TODO do not add existing entries to the classpath again
        var classpath = System.getProperty("java.class.path")

        val jars = listJarFiles(listDirectories(new File(Menus.getPlugInsPath)))
        for (jar <- jars) {
            classpath = jar.getAbsolutePath + File.pathSeparator + classpath;
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


    def listDirectories(root: File): Array[File] = {
        val dirFilter = new FileFilter() {
            override def accept(file: File) = file.isDirectory
        }

        val r = ArrayBuffer(root)
        for (dir <- root.listFiles(dirFilter)) {
            r.appendAll(listDirectories(dir))
        }
        r.toArray;
    }


    def listJarFiles(dirs: Array[File]): Array[File] = {
        val r = ArrayBuffer[File]()
        for (dir <- dirs) {
            r.appendAll(listJarFiles(dir))
        }
        r.toArray;
    }


    def listJarFiles(dir: File): Array[File] = {
        val jarFilter = new FilenameFilter() {
            override def accept(dir: File, name: String) = name.toLowerCase.endsWith(".jar")
        }

        dir.listFiles(jarFilter)
    }


    def redirectSystemOut(): BufferedPrintStream = {
        if (!System.out.isInstanceOf[BufferedPrintStream]) {
            System.setOut(new BufferedPrintStream())
        }
        System.out.asInstanceOf[BufferedPrintStream]
    }
}