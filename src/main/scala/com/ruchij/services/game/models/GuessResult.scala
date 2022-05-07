package com.ruchij.services.game.models

sealed trait GuessResult

object GuessResult {
  sealed trait GuessOutcome extends GuessResult {
    val success: Boolean
  }

  case class IncorrectGuess(
    correctPositions: Seq[(Int, Char)],
    correctLetters: Seq[Char],
    override val success: Boolean = false
  ) extends GuessOutcome

  case class CorrectGuess(word: String, override val success: Boolean = true) extends GuessOutcome

  case class GuessesExhausted(word: String, guessCount: Int) extends GuessResult
}
