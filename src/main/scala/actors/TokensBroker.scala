package actors

import akka.actor.{Actor, ActorRef}
import messages.{NeighbourAnnouncement, NeighbourRegistrationAck}
import messages.tokens.TokenReturn
import messages.tokens.misra.PingPongAlgToken

class TokensBroker(tokensConsumer: ActorRef, numberOfProcesses: Int) extends Actor {
  var numberOfPossessedTokens = 0
  var lastProcessedToken: Option[PingPongAlgToken] = None
  var tokensVersion = 0
  var neighbour: Option[ActorRef] = None

  override def receive: Receive = {
    case token: PingPongAlgToken => receiveToken(token)
    case TokenReturn(token: PingPongAlgToken) => sendToNext(token)
    case NeighbourAnnouncement(neighbourRef) => receiveNeighbour(neighbourRef)
    case _ => println("Unknown message received")
  }

  private def receiveToken(token: PingPongAlgToken): Unit = {
    if(wasTokenLost(token))
      regenerate(token)
    else
      processToken(token)
  }

  private def wasTokenLost(token: PingPongAlgToken): Boolean = lastProcessedToken.exists(_.equals(token))

  private def regenerate(tokenDetectingLoss: PingPongAlgToken): Unit = {
    val regeneratedToken = tokenDetectingLoss.makeComplementaryToken()
    send(regeneratedToken, self)
    send(tokenDetectingLoss, self)
  }

  private def processToken(token: PingPongAlgToken): Unit = {
    val unpackedToken = unpack(token)
    tokensConsumer ! unpackedToken
    lastProcessedToken = Some(unpackedToken)
  }

  private def unpack(token: PingPongAlgToken): PingPongAlgToken = {
    numberOfPossessedTokens += 1
    assert(Set(1, 2) contains numberOfPossessedTokens)

    if(hasTokensMeetingOccurred)
      incarnate(token)
    else
      readExpectedTokenToken(token)
  }

  private def hasTokensMeetingOccurred = numberOfPossessedTokens == 2

  private def incarnate(newlyArrivedToken: PingPongAlgToken): PingPongAlgToken = {
    tokensVersion = (newlyArrivedToken.getVersion + 1) % (numberOfPossessedTokens + 1)
    newlyArrivedToken.withVersion(tokensVersion)
  }

  private def readExpectedTokenToken(token: PingPongAlgToken): PingPongAlgToken = {
    tokensVersion = token.getVersion
    token
  }

  private def sendToNext(token: PingPongAlgToken): Unit = send(token, neighbour)

  private def send(token: PingPongAlgToken, receiver: ActorRef): Unit = send(token, Some(receiver))

  private def send(token: PingPongAlgToken, receiver: Option[ActorRef]): Unit = {
    numberOfPossessedTokens -= 1
    receiver.foreach(_ ! token.withVersion(tokensVersion))
  }

  private def receiveNeighbour(neighbourRef: ActorRef): Unit = {
    neighbour = Some(neighbourRef)
    sender() ! NeighbourRegistrationAck()
  }
}
