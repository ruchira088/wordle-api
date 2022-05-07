package com.ruchij.services.game

import com.ruchij.services.game.GameService.compareGuess
import com.ruchij.services.game.models.GuessResult.{Correct, Incorrect}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, Inside}

import java.util.UUID

class GameServiceSpec extends AnyFlatSpec with Matchers with EitherValues with Inside {

  "compareGuess" should "return a left result when the guessed word length and target word length does not match" in {
    compareGuess("one", "once") mustBe Left("Guessed word length does NOT equal target word length")
  }

  it should "return a Correct result when the guessed word and target word match" in {
    compareGuess("hello", "hello") mustBe Right(Correct("hello"))

    val randomUuid = UUID.randomUUID().toString
    compareGuess(randomUuid, randomUuid) mustBe Right(Correct(randomUuid))
  }

  it should "return a Incorrect result when the guesses word and target word does not match" in {
    val incorrectOne = compareGuess("hell", "hlil").value
    incorrectOne mustBe a [Incorrect]

    inside(incorrectOne) {
      case Incorrect(correctPositions, correctLetters) =>
        correctPositions mustBe Seq(0 -> 'h', 3 -> 'l')
        correctLetters must contain theSameElementsAs Seq('h', 'l', 'l')
    }

    val incorrectGuessTwo = compareGuess("hell", "heal").value
    incorrectGuessTwo mustBe a [Incorrect]

    inside(incorrectGuessTwo) {
      case Incorrect(correctPositions, correctLetters) =>
        correctPositions mustBe Seq(0 -> 'h', 1 -> 'e', 3 -> 'l')
        correctLetters must contain theSameElementsAs Seq('h', 'e', 'l')
    }
  }

}
