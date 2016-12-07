import actors.{DummyConsumer, TokensBroker}
import akka.actor.{ActorSystem, Props}
import messages.NeighbourAnnouncement

object Main {
  def main(args: Array[String]): Unit = {
    StringConverter.toInt(args.headOption) match {
      case Some(numberOfActors) if numberOfActors > 0 => start(numberOfActors)
      case _ => println("Usage: PROGRAM_NAME NUMBER_OF_ACTORS")
    }
  }

  private def start(numberOfActors: Int): Unit = {
    val actorSystem = ActorSystem()

    val systemSize = 2

    val consumer1 = actorSystem.actorOf(Props[DummyConsumer])
    val broker1 = actorSystem.actorOf(Props(new TokensBroker(consumer1, systemSize)))

    val consumer2 = actorSystem.actorOf(Props[DummyConsumer])
    val broker2 = actorSystem.actorOf(Props(new TokensBroker(consumer2, systemSize)))

    broker1 ! NeighbourAnnouncement(broker2)

    actorSystem.terminate()
  }
}
