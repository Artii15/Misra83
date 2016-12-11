package misra.messages.tokens.misra

import misra.messages.tokens._

case class Ping(version: Int) extends PingPongAlgToken with CriticalSectionPermission {
  override def makeComplementaryToken(): PingPongAlgToken = Pong(version)

  override def getVersion: Int = version

  override def equals(token: Token): Boolean = token match {
    case Ping(pingVersion) => version == pingVersion
    case _ => false
  }

  override def withVersion(chosenVersion: Int): PingPongAlgToken = Ping(chosenVersion)
}
