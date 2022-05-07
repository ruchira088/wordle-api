package com.ruchij.dao.game

import com.ruchij.dao.game.models.Game

trait GameDao[F[_]] {
  def insert(game: Game): F[Int]

  def findById(gameId: String): F[Option[Game]]

  def deleteById(gameId: String): F[Option[Game]]

  def deductRemainingAttempts(gameId: String, count: Int): F[Option[Game]]
}
