package com.ruchij.services.game.models

sealed trait Outcome {
  val success: Boolean
  val remainingGuesses: Int
}

object Outcome {
  case class IncorrectGuess(
    correctPositions: Seq[(Int, Char)],
    correctLetters: Seq[Char],
    override val remainingGuesses: Int,
    override val success: Boolean = false
  ) extends Outcome

  case class CorrectGuess(word: String, override val remainingGuesses: Int, override val success: Boolean = true)
      extends Outcome

  case class GuessesExhausted(
    word: String,
    guessCount: Int,
    override val remainingGuesses: Int = 0,
    override val success: Boolean = false
  ) extends Outcome
}
