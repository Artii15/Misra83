import akka.actor.{ActorSystem, Props}

object Main {
  def main(args: Array[String]): Unit = {
    StringConverter.toInt(args.headOption) match {
      case Some(numberOfActors) if numberOfActors > 0 => start(numberOfActors)
      case _ => println("Usage: PROGRAM_NAME NUMBER_OF_ACTORS")
    }
  }

  private def start(numberOfActors: Int): Unit = {
    val actorSystem = ActorSystem("Local")
    actorSystem.terminate()
  }
}
