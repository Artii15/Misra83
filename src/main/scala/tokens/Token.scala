package tokens

trait Token {
  def equals(token: Token): Boolean
}
