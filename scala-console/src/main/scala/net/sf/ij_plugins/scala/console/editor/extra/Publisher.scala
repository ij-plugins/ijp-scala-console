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

import scala.collection.mutable

// Simplified version of `scala.collection.mutable.Publisher` that was removed in Scala 2.13

/**
 * `Publisher[A,This]` objects publish events of type `A`
 *  to all registered subscribers. When subscribing, a subscriber may specify
 *  a filter which can be used to constrain the number of events sent to the
 *  subscriber. Subscribers may suspend their subscription, or reactivate a
 *  suspended subscription. Class `Publisher` is typically used
 *  as a mixin. The abstract type `Pub` models the type of the publisher itself.
 *
 *  @tparam Evt      type of the published event.
 *
 *  @author  Matthias Zenger
 *  @author  Martin Odersky
 *  @since   1
 */
trait Publisher[Evt] {

  type Pub <: Publisher[Evt]
  type Sub    = Subscriber[Evt, Pub]
  type Filter = Evt => Boolean

  /**
   * The publisher itself of type `Pub`. Implemented by a cast from `this` here.
   * Needs to be overridden if the actual publisher is different from `this`.
   */
  protected val self: Pub = this.asInstanceOf[Pub]

  private val filters   = new mutable.HashMap[Sub, mutable.Set[Filter]] with mutable.MultiMap[Sub, Filter]
  private val suspended = new mutable.HashSet[Sub]

  def subscribe(sub: Sub): Unit                 = { subscribe(sub, _ => true) }
  def subscribe(sub: Sub, filter: Filter): Unit = { filters.addBinding(sub, filter) }
  def suspendSubscription(sub: Sub): Unit       = { suspended += sub }
  def activateSubscription(sub: Sub): Unit      = { suspended -= sub }
  def removeSubscription(sub: Sub): Unit        = { filters -= sub }
  def removeSubscriptions(): Unit               = { filters.clear() }

  protected def publish(event: Evt): Unit = {
    filters.keys.foreach(sub =>
      if (
        !suspended.contains(sub) &&
        filters.entryExists(sub, p => p(event))
      )
        sub.notify(self, event)
    )
  }

  /**
   * Checks if two publishers are structurally identical.
   *
   *  @return true, iff both publishers contain the same sequence of elements.
   */
  override def equals(obj: Any): Boolean = obj match {
    case that: Publisher[_] => filters == that.filters && suspended == that.suspended
    case _                  => false
  }
}
