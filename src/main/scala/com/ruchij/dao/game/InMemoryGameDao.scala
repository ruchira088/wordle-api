package com.ruchij.dao.game

import cats.data.OptionT
import cats.effect.kernel.Sync
import cats.implicits._
import com.ruchij.dao.game.models.Game

import java.util.concurrent.ConcurrentHashMap

class InMemoryGameDao[F[_]: Sync] extends GameDao[F] {
  val data = new ConcurrentHashMap[String, Game]()

  override def insert(game: Game): F[Int] =
    Sync[F].delay(data.put(game.id, game)).as(1)

  override def findById(gameId: String): F[Option[Game]] =
    Sync[F].delay(Option(data.get(gameId)))

  override def deleteById(gameId: String): F[Option[Game]] =
    Sync[F].delay(Option(data.remove(gameId)))

  override def deductRemainingAttempts(gameId: String, count: Int): F[Option[Game]] =
    OptionT(findById(gameId))
      .semiflatMap { game =>
        Sync[F].delay {
          data.put(gameId, game.copy(remainingGuesses = game.remainingGuesses - count))
        }
      }
      .value
}
