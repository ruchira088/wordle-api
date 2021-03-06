package com.ruchij.services.game

import cats.data.OptionT
import cats.effect.kernel.Sync
import cats.implicits._
import cats.{Applicative, ApplicativeError}
import com.ruchij.dao.game.GameDao
import com.ruchij.dao.game.models.Game
import com.ruchij.exceptions.ResourceNotFoundException
import com.ruchij.services.game.models.Outcome.{CorrectGuess, GuessesExhausted, IncorrectGuess}
import com.ruchij.services.game.models.{GuessResult, Outcome}
import com.ruchij.types.FunctionKTypes.{FunctionK2TypeOps, eitherToF}
import com.ruchij.types.{JodaClock, RandomGenerator}

import java.util.UUID

class GameServiceImpl[F[_]: Sync: JodaClock](words: Vector[String], gameDao: GameDao[F])(
  implicit randomGenerator: RandomGenerator[F, UUID]
) extends GameService[F] {

  private val wordsSet = words.toSet

  override def createGame(wordLength: Int, guessCount: Int): F[Game] =
    for {
      gameId <- randomGenerator.generate
      timestamp <- JodaClock[F].timestamp

      selectedWord <- RandomGenerator.select(words.filter(_.length == wordLength))

      game = Game(gameId.toString, timestamp, selectedWord, guessCount, guessCount)

      _ <- gameDao.insert(game)
    } yield game


  override def findById(gameId: String): F[Game] =
    OptionT(gameDao.findById(gameId))
      .getOrElseF {
        ApplicativeError[F, Throwable].raiseError(ResourceNotFoundException(s"Unable to find game with id=$gameId"))
      }

  override def validateGuess(gameId: String, guess: String): F[Outcome] =
    for {
      _ <-
        if (wordsSet.contains(guess)) Applicative[F].unit
        else ApplicativeError[F, Throwable].raiseError {
          new IllegalArgumentException(s"$guess is NOT a valid word")
        }

      game <- findById(gameId)

      guessResult <- GameService.compareGuess(guess, game.word).left
        .map(error => new IllegalArgumentException(error))
        .toType[F, Throwable]

      outcome <-
        guessResult match {
          case GuessResult.Incorrect(correctPositions, correctLetters) =>
            if (game.remainingGuesses == 0) gameDao.deleteById(gameId).as(GuessesExhausted(game.word, game.guessCount))
            else {
              gameDao.deductRemainingAttempts(gameId, 1)
                .as(IncorrectGuess(correctPositions, correctLetters, game.remainingGuesses - 1))
            }

          case GuessResult.Correct(word) => Applicative[F].pure(CorrectGuess(word, game.remainingGuesses))
        }

    } yield outcome

}
