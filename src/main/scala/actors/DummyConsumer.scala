package actors

import akka.actor.Actor
import tokens.CriticalSectionPermission

class DummyConsumer extends Actor {
  override def receive: Receive = {
    case token: CriticalSectionPermission => println("permission to enter received"); sender() ! token;
  }
}
