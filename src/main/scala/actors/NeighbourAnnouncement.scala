package actors

import akka.actor.ActorRef

case class NeighbourAnnouncement(neighbour: ActorRef)
