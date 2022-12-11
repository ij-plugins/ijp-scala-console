/*
 * Scala (https://www.scala-lang.org)
 *
 * Copyright EPFL and Lightbend, Inc.
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package net.sf.ij_plugins.scala.console.editor.extra

// Simplified version of `scala.collection.mutable.Subscriber` that was removed in Scala 2.13

/** `Subscriber[A, B]` objects may subscribe to events of type `A`
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
