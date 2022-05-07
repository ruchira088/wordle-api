package com.ruchij.services.game.models

import io.circe.Encoder

sealed trait Outcome {
  val success: Boolean
  val remainingGuessCount: Int
}

object Outcome {
  case class IncorrectGuess(
    correctPositions: Seq[(Int, Char)],
    correctLetters: Seq[Char],
    override val remainingGuessCount: Int,
    override val success: Boolean = false
  ) extends Outcome

  case class CorrectGuess(word: String, override val remainingGuessCount: Int, override val success: Boolean = true)
      extends Outcome

  case class GuessesExhausted(
    word: String,
    guessCount: Int,
    override val remainingGuessCount: Int = -1,
    override val success: Boolean = false
  ) extends Outcome

  implicit def outcomeEncoder(
    implicit incorrectGuessEncoder: Encoder[IncorrectGuess],
    correctGuessEncoder: Encoder[CorrectGuess],
    guessesExhaustedEncoder: Encoder[GuessesExhausted]
  ): Encoder[Outcome] = {
    case incorrectGuess: IncorrectGuess => incorrectGuessEncoder(incorrectGuess)
    case correctGuess: CorrectGuess => correctGuessEncoder(correctGuess)
    case guessesExhausted: GuessesExhausted => guessesExhaustedEncoder(guessesExhausted)
  }
}
