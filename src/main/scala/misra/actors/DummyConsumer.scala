package misra.actors

import akka.actor.Actor
import misra.messages.tokens.CriticalSectionPermission

class DummyConsumer extends Actor {
  override def receive: Receive = {
    case token: CriticalSectionPermission => println("permission to enter received"); sender() ! token;
  }
}
