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

package ij_plugins.scala.console.editor.extra

// Simplified version of `scala.collection.mutable.Subscriber` that was removed in Scala 2.13

/**
 * `Subscriber[A, B]` objects may subscribe to events of type `A`
 *  published by an object of type `B`. `B` is typically a subtype of
 *  [[scala.collection.mutable.Publisher]].
 *
 *  @author  Matthias Zenger
 *  @author  Martin Odersky
 *  @since   1
 */
trait Subscriber[-Evt, -Pub] {
  def notify(pub: Pub, event: Evt): Unit
}