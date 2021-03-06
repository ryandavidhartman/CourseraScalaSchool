/**
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */
package actorbintree

import akka.actor._
import scala.collection.immutable.Queue

object BinaryTreeSet {

  trait Operation {
    def requester: ActorRef
    def id: Int
    def elem: Int
  }

  trait OperationReply {
    def id: Int
  }

  /** Request with identifier `id` to insert an element `elem` into the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Insert(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to check whether an element `elem` is present
    * in the tree. The actor at reference `requester` should be notified when
    * this operation is completed.
    */
  case class Contains(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to remove the element `elem` from the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Remove(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request to perform garbage collection*/
  case object GC

  /** Holds the answer to the Contains request with identifier `id`.
    * `result` is true if and only if the element is present in the tree.
    */
  case class ContainsResult(id: Int, result: Boolean) extends OperationReply

  /** Message to signal successful completion of an insert or remove operation. */
  case class OperationFinished(id: Int) extends OperationReply

}


class BinaryTreeSet extends Actor with ActorLogging {
  import BinaryTreeSet._
  import BinaryTreeNode._

  def createRoot: ActorRef = context.actorOf(BinaryTreeNode.props(0, initiallyRemoved = true))

  var root: ActorRef = createRoot

  // optional
  var pendingQueue: Queue[Operation] = Queue.empty[Operation]

  // optional
  def receive: Receive = normal

  // optional
  /** Accepts `Operation` and `GC` messages. */
  val normal: Receive = {
    case operation: Operation => root forward operation
    case GC =>
      val newRoot = createRoot
      context.become(garbageCollecting(newRoot))
      root ! CopyTo(newRoot)
  }

  // optional
  /** Handles messages while garbage collection is performed.
    * `newRoot` is the root of the new binary tree where we want to copy
    * all non-removed elements into.
    */
  def garbageCollecting(newRoot: ActorRef): Receive = {
  case operation: Operation =>
    log.debug("GC is happening, queuing operation request {}", operation)
    pendingQueue = pendingQueue.enqueue(operation)
  case CopyFinished =>
    root = newRoot
    context.become(receive)
    pendingQueue foreach (root ! _)
    pendingQueue = Queue.empty[Operation]

  }

}

object BinaryTreeNode {
  trait Position

  case object Left extends Position
  case object Right extends Position

  case class CopyTo(treeNode: ActorRef)
  case object CopyFinished

  def props(elem: Int, initiallyRemoved: Boolean) = Props(classOf[BinaryTreeNode],  elem, initiallyRemoved)
}

class BinaryTreeNode(val elem: Int, initiallyRemoved: Boolean) extends Actor with ActorLogging {
  import BinaryTreeNode._
  import BinaryTreeSet._

  var subtrees: Map[Position, ActorRef] = Map[Position, ActorRef]()
  var removed: Boolean = initiallyRemoved

  def getNextPosition(e: Int):Position = if (e < elem) Left else Right

  // optional
  def receive: Receive = normal

  // optional
  /** Handles `Operation` messages and `CopyTo` requests. */
  val normal: Receive =
    insert orElse
    contains orElse
    remove orElse
    copyTo orElse {
      case op => throw new RuntimeException(s"unknown operation $op")
    }

  def insert: Receive = {
    case Insert(requester, id, e) =>
      log.debug("Insert request from requester: {} id: {} elem: {}", requester, id, e)
      if (e == elem) {
        if (removed) removed = false
        requester ! OperationFinished(id)
      } else {
        val next = getNextPosition(e)
        if (subtrees.isDefinedAt(next)) subtrees(next) ! Insert(requester, id, e)
        else {
          subtrees += (next -> context.actorOf(props(e, initiallyRemoved = false)))
          requester ! OperationFinished(id)
        }
      }
  }

  def contains: Receive = {
    case Contains(requester, id, e) =>
      log.debug("Contains request from requester: {} id: {} elem: {}", requester, id, e)
      if (e == elem) {
        requester ! ContainsResult(id = id, result = !removed)
      } else {
        val next = getNextPosition(e)
        if (subtrees.isDefinedAt(next))
          subtrees(next) ! Contains(requester, id, e)
        else
          requester ! ContainsResult(id = id, result = false)
      }
  }

  def remove: Receive = {
    case Remove(requester, id, e) =>
      log.debug("Remove request from requester: {} id: {} elem: {}", requester, id, e)
      if (e == elem) {
        removed = true
        requester ! OperationFinished(id = id)
      } else {
        val next = getNextPosition(e)
        if (subtrees.isDefinedAt(next))
          subtrees(next) ! Remove(requester, id, e)
        else
          requester ! OperationFinished(id = id)
      }
  }

  def copyTo: Receive = {
    case CopyTo(newRoot) =>
      log.debug("CopyTo request to {} from requester {} ", newRoot, sender)

      val children: Set[ActorRef] = subtrees.values.toSet

      if (!removed) newRoot ! Insert(self, 0, elem)

      if (removed && subtrees.isEmpty){
        sender ! CopyFinished
        self ! PoisonPill
      }
      else{
        context.become(copying(children, insertConfirmed = removed, sender))
      }
      children foreach (_ ! CopyTo(newRoot))
    }


  // optional
  /** `expected` is the set of ActorRefs whose replies we are waiting for,
    * `insertConfirmed` tracks whether the copy of this node to the new tree has been confirmed.
    */
   def copying(expected: Set[ActorRef], insertConfirmed: Boolean, sentBy: ActorRef): Receive = {
     case OperationFinished(_) =>
       context.become(copying(expected, insertConfirmed = true, sentBy))
       if(expected.isEmpty) {
         sentBy ! CopyFinished
         self ! PoisonPill
       }
     case CopyFinished =>
       val newSet = expected - sender
       if(newSet.isEmpty && insertConfirmed){
         sentBy ! CopyFinished
         self ! PoisonPill
       }else {
         context.become(copying(newSet,insertConfirmed, sentBy))
       }
   }



}
