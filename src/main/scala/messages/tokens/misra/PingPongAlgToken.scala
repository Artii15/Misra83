package messages.tokens.misra

import messages.tokens.Token

trait PingPongAlgToken extends Token {
  def makeComplementaryToken(): PingPongAlgToken
  def getVersion: Int
  def withVersion(version: Int): PingPongAlgToken
}
