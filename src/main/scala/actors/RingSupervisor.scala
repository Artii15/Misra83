package actors

import akka.actor.Actor
import com.sun.javaws.exceptions.InvalidArgumentException
import messages.{NeighbourAnnouncement, NeighbourRegistrationAck}

class RingSupervisor(supervisedPairs: Traversable[SupervisedPair]) extends Actor {
  private var uninitializedPairs = supervisedPairs.size

  if(supervisedPairs.isEmpty) throw new InvalidArgumentException(Array("Empty nodes list provided"))
  else initialize()


  private def initialize(): Unit = {
    val lastPair = supervisedPairs.tail.foldLeft(supervisedPairs.head){ case (neighbourPair, pair) =>
        pair.supervisor ! NeighbourAnnouncement(neighbourPair.supervisor)
        pair
    }
    supervisedPairs.head.supervisor ! NeighbourAnnouncement(lastPair.supervisor)
    lastPair.supervisor ! new
  }

  override def receive: Receive = {
    case NeighbourRegistrationAck => uninitializedPairs -= 1
  }
}
