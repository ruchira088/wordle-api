package com.ruchij.services.game.models

sealed trait GuessResult

object GuessResult {
  case class Incorrect(
    correctPositions: Seq[(Int, Char)],
    correctLetters: Seq[Char],
  ) extends GuessResult

  case class Correct(word: String) extends GuessResult
}
