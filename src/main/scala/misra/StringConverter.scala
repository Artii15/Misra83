package misra

object StringConverter {
  def toInt(stringOption: Option[String]): Option[Int] = stringOption.flatMap(string => {
    try {
      Some(string.toInt)
    }
    catch {
      case _: Throwable => None
    }
  })
}
