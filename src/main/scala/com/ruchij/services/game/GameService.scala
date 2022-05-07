package com.ruchij.services.game

import com.ruchij.dao.game.models.Game
import com.ruchij.services.game.models.{GuessResult, Outcome}
import com.ruchij.services.game.models.GuessResult.{Correct, Incorrect}

trait GameService[F[_]] {
  def createGame(wordLength: Int, guessCount: Int): F[Game]

  def findById(gameId: String): F[Game]

  def validateGuess(gameId: String, guess: String): F[Outcome]
}

object GameService {

  def compareGuess(guess: String, word: String): Either[String, GuessResult] =
    if (guess.length != word.length) Left("Guessed word length does NOT equal target word length")
    else {
      val correctPositions: Seq[(Int, Char)]= findCorrectPositions(guess, word).sortBy { case (index, _) => index }
      val correctLetters: Seq[Char] = findCorrectLetters(guess.toList, word.toList).sortBy(_ => Math.random())

      if (correctPositions.size == word.length) Right(Correct(word))
      else Right(Incorrect(correctPositions, correctLetters))
    }

  private def findCorrectPositions(guess: String, word: String): Seq[(Int, Char)] =
    guess
      .zip(word)
      .zipWithIndex
      .collect { case ((guessChar, wordChar), index) if guessChar == wordChar => index -> wordChar }

  private def findCorrectLetters(guess: List[Char], word: List[Char]): List[Char] =
    guess match {
      case Nil => Nil

      case head :: tail =>
        if (word.contains(head)) List(head) ++ findCorrectLetters(tail, removeFromList(word, head))
        else findCorrectLetters(tail, word)
    }

  private def removeFromList[A](list: List[A], value: A): List[A] =
    list match {
      case head :: tail if head == value => tail
      case head :: tail => head :: removeFromList(tail, value)
      case Nil => Nil
    }

}
