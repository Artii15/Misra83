package misra.actors

import akka.actor.{Actor, ActorRef}
import misra.messages.{LoseToken, NeighbourAnnouncement, NeighbourRegistrationAck}
import misra.messages.tokens.TokenReturn
import misra.messages.tokens.misra.PingPongAlgToken

class TokensBroker(tokensConsumer: ActorRef, numberOfProcesses: Int) extends Actor {
  private var simulateFailure = false
  private var numberOfPossessedTokens = 0
  private var lastProcessedToken: Option[PingPongAlgToken] = None
  private var tokensVersion = 0
  private var neighbour: Option[ActorRef] = None


  override def receive: Receive = {
    case token: PingPongAlgToken => receiveToken(token)
    case TokenReturn(token: PingPongAlgToken) => sendToNext(token)
    case NeighbourAnnouncement(neighbourRef) => receiveNeighbour(neighbourRef)
    case LoseToken => simulateFailure = true
  }

  private def receiveToken(token: PingPongAlgToken): Unit = {
    val processedToken = processToken(token)
    tokensConsumer ! processedToken
    lastProcessedToken = Some(processedToken)
  }

  private def processToken(token: PingPongAlgToken): PingPongAlgToken = {
    numberOfPossessedTokens += 1
    assert(Set(1, 2) contains numberOfPossessedTokens)

    if(wasTokenLost(token))
      regenerate(token)
    else if(hasTokensMeetingOccurred)
      incarnate(token)
    else
      readExpectedTokenVersion(token)

    token.withVersion(tokensVersion)
  }

  private def wasTokenLost(token: PingPongAlgToken): Boolean = lastProcessedToken.exists(_.equals(token))

  private def regenerate(tokenDetectingLoss: PingPongAlgToken): Unit =
    receiveToken(tokenDetectingLoss.makeComplementaryToken())

  private def hasTokensMeetingOccurred = numberOfPossessedTokens == 2

  private def incarnate(newlyArrivedToken: PingPongAlgToken): Unit =
    tokensVersion = (newlyArrivedToken.getVersion + 1) % (numberOfProcesses + 1)

  private def readExpectedTokenVersion(token: PingPongAlgToken): Unit =
    tokensVersion = token.getVersion

  private def sendToNext(token: PingPongAlgToken): Unit = {
    numberOfPossessedTokens -= 1
    if(simulateFailure) simulateFailure = false
    else neighbour.foreach(_ ! token.withVersion(tokensVersion))
  }

  private def receiveNeighbour(neighbourRef: ActorRef): Unit = {
    neighbour = Some(neighbourRef)
    sender() ! NeighbourRegistrationAck
  }
}
