package misra.demo

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import misra.demo.actors.RingSupervisor
import misra.demo.messages.{LoseToken, Start}

import scala.annotation.tailrec
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem()
    val supervisor = actorSystem.actorOf(Props(new RingSupervisor(10)))
    supervisor ! Start
    Writer.clearScreen()
    interact(supervisor, actorSystem)
  }

  @tailrec
  private def interact(supervisor: ActorRef, actorSystem: ActorSystem): Unit = {
    val command = StdIn.readLine()
    if(command == "q") actorSystem.terminate()
    else {
      supervisor ! LoseToken
      interact(supervisor, actorSystem)
    }
  }
}
