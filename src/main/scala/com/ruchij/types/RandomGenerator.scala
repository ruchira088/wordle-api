package com.ruchij.types

import cats.ApplicativeError
import cats.effect.kernel.Sync
import cats.implicits._

import java.util.UUID

trait RandomGenerator[F[_], +A] {
  def generate[B >: A]: F[B]
}

object RandomGenerator {
  def create[F[_]: Sync, A](block: => A): RandomGenerator[F, A] =
    new RandomGenerator[F, A] {
      override def generate[B >: A]: F[B] = Sync[F].delay(block)
    }

  implicit def uuidGenerator[F[_]: Sync]: RandomGenerator[F, UUID] = create(UUID.randomUUID())

  def select[F[_]: Sync, A](selections: Seq[A]): F[A] = {
    val size = selections.size

    if (size == 0) ApplicativeError[F, Throwable].raiseError(new IllegalArgumentException("The selections cannot be empty"))
    else Sync[F].delay(Math.random() * selections.size).map(value => selections(value.toInt))
  }
}
