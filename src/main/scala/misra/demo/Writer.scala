package misra.demo

object Writer {
  private val escCode: Char = 0x1B

  def setCursorPosition(row: Int, col: Int): Unit = print(s"$escCode[$row;${col}f")

  def clearScreen(): Unit = print(s"$escCode[2J")

  def clearLine(): Unit = print(s"$escCode[2K")
}
