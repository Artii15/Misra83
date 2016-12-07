package actors

import akka.actor.Actor
import com.sun.javaws.exceptions.InvalidArgumentException
import messages.tokens.misra.{Ping, PingPongPair, Pong}
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
  }

  override def receive: Receive = {
    case NeighbourRegistrationAck() => receiveNeighbourRegistrationAck()
  }

  private def receiveNeighbourRegistrationAck(): Unit = {
    uninitializedPairs -= 1
    if(uninitializedPairs == 0) sendInitialTokens()
  }

  private def sendInitialTokens(): Unit = {
    val tokensVersion = 0
    val ping = Ping(tokensVersion)
    val pong = Pong(tokensVersion)
    supervisedPairs.head.supervisor ! PingPongPair(ping, pong)
  }
}
