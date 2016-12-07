package actors

import akka.actor.ActorRef

case class SupervisedPair(supervisor: ActorRef, supervised: ActorRef)
