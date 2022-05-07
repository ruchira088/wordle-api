package com.ruchij.services.game

import com.ruchij.services.game.GameService.compareGuess
import com.ruchij.services.game.models.GuessResult.{IncorrectGuess, CorrectGuess}
import org.scalatest.{EitherValues, Inside}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.util.UUID

class GameServiceSpec extends AnyFlatSpec with Matchers with EitherValues with Inside {

  "compareGuess" should "return a left result when the guessed word length and target word length does not match" in {
    compareGuess("one", "once") mustBe Left("Guessed word length does NOT equal target word length")
  }

  it should "return a CorrectGuess result when the guessed word and target word match" in {
    compareGuess("hello", "hello") mustBe Right(CorrectGuess("hello"))

    val randomUuid = UUID.randomUUID().toString
    compareGuess(randomUuid, randomUuid) mustBe Right(CorrectGuess(randomUuid))
  }

  it should "return a IncorrectGuess result when the guesses word and target word does not match" in {
    val incorrectGuessOne = compareGuess("hell", "hlil").value
    incorrectGuessOne mustBe a [IncorrectGuess]

    inside(incorrectGuessOne) {
      case IncorrectGuess(correctPositions, correctLetters, success) =>
        success mustBe false
        correctPositions mustBe Seq(0 -> 'h', 3 -> 'l')
        correctLetters must contain theSameElementsAs Seq('h', 'l', 'l')
    }

    val incorrectGuessTwo = compareGuess("hell", "heal").value
    incorrectGuessTwo mustBe a [IncorrectGuess]

    inside(incorrectGuessTwo) {
      case IncorrectGuess(correctPositions, correctLetters, success) =>
        success mustBe false
        correctPositions mustBe Seq(0 -> 'h', 1 -> 'e', 3 -> 'l')
        correctLetters must contain theSameElementsAs Seq('h', 'e', 'l')
    }
  }

}
