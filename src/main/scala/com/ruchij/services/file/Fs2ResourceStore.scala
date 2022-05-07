package com.ruchij.services.file

import cats.data.OptionT
import cats.effect.kernel.Sync
import fs2.Stream

import java.io.InputStream

class Fs2ResourceStore[F[_]: Sync] extends ResourceStore[F] {
  private val ChunkSize = 4096

  override def read(key: String): F[Option[Stream[F, Byte]]] =
    OptionT(Sync[F].blocking(Option(getClass.getClassLoader.getResourceAsStream(key))))
      .map { inputStream => read(inputStream, 0) }
      .value

  private def read(inputStream: InputStream, offset: Long): Stream[F, Byte] =
    Stream.eval(Sync[F].blocking(inputStream.readNBytes(ChunkSize)))
      .flatMap {
        bytes =>
          if (bytes.length < ChunkSize) Stream.emits(bytes)
          else Stream.emits(bytes) ++ read(inputStream, offset + ChunkSize)
      }
      .onFinalize {
        Sync[F].blocking(inputStream.close())
      }

}
