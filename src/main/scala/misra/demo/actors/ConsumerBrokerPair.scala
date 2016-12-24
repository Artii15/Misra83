package misra.demo.actors

import akka.actor.ActorRef

case class ConsumerBrokerPair(consumer: ActorRef, broker: ActorRef)
