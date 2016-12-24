package misra.demo.actors

import akka.actor.Actor
import misra.demo.Writer
import misra.demo.messages.LoseToken
import misra.messages.tokens.TokenReturn
import misra.messages.tokens.misra.{Ping, PingPongAlgToken, Pong}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

class TokensConsumer(id: Int, consumersCount: Int) extends Actor {
  private var loseNextToken = false

  override def receive: Receive = {
    case token: PingPongAlgToken => receiveToken(token)
    case LoseToken => loseNextToken = true
  }

  private def receiveToken(token: PingPongAlgToken): Unit = if(loseNextToken) loseNextToken = false else token match {
    case ping: Ping => receivePing(ping)
    case pong: Pong => receivePong(pong)
  }

  private def receivePing(ping: Ping): Unit = {
    printStatus("PING", ping, Console.RED, 5)
    scheduleToSendLater(ping, 2)
  }

  private def receivePong(pong: Pong): Unit = {
    printStatus("PONG", pong, Console.BLUE, 6)
    scheduleToSendLater(pong, 1)
  }

  private def scheduleToSendLater(token: PingPongAlgToken, delayInSeconds: Int): Unit = {
    val broker = sender()
    context.system.scheduler.scheduleOnce(FiniteDuration(delayInSeconds, "seconds"), broker, TokenReturn(token))
  }

  private def printStatus(label: String, token: PingPongAlgToken, color: String, row: Int): Unit = {
    Writer.setCursorPosition(row, 1)
    Writer.clearLine()
    print(s"$color$label:${Console.RESET} ")
    Range.inclusive(1, consumersCount).foreach(consumerId => {
      if(consumerId > 1) print(" ")
      if(consumerId == id) print(s"$color${token.getVersion}${Console.RESET}") else print(consumerId)
    })
    Console.flush()
  }
}
