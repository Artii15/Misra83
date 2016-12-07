package messages.tokens.misra

import messages.tokens.Token

case class Pong(version: Int) extends PingPongAlgToken {
  override def makeComplementaryToken(): PingPongAlgToken = Ping(version)

  override def getVersion: Int = version

  override def equals(token: Token): Boolean = token match {
    case Pong(pongVersion) => pongVersion == version
  }

  override def withVersion(chosenVersion: Int): PingPongAlgToken = Pong(chosenVersion)
}
