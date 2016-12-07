import akka.actor.Actor

class TokensConsumer extends Actor {
  override def receive: Receive = {
    case token: Token => receiveToken(token)
    case _ => println("Warning: unknown message received")
  }

  private def receiveToken(token: Token): Unit = {
    token match {
      case _: CriticalSectionPermission => println("Permission to enter section received")
      case _: Token => println("Some other token received")
    }
    sender() ! TokenReturn(token)
  }
}
