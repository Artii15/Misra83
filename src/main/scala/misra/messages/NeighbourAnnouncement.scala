package misra.messages

import akka.actor.ActorRef

case class NeighbourAnnouncement(neighbour: ActorRef)
