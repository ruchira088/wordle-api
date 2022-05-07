package com.ruchij.services.file

import fs2.Stream

trait ResourceStore[F[_]] {
  def read(key: String): F[Option[Stream[F, Byte]]]
}
