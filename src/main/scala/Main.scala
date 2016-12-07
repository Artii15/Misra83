import actors.{DummyConsumer, TokensBroker}
import akka.actor.{ActorSystem, Props}

object Main {
  def main(args: Array[String]): Unit = {
    StringConverter.toInt(args.headOption) match {
      case Some(numberOfActors) if numberOfActors > 0 => start(numberOfActors)
      case _ => println("Usage: PROGRAM_NAME NUMBER_OF_ACTORS")
    }
  }

  private def start(numberOfActors: Int): Unit = {
    val actorSystem = ActorSystem()
    val consumer = actorSystem.actorOf(Props[DummyConsumer])
    val broker = actorSystem.actorOf(Props(new TokensBroker(consumer, 1)))
    actorSystem.terminate()
  }
}
