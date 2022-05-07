package com.ruchij

import cats.ApplicativeError
import cats.data.OptionT
import cats.effect.kernel.Sync
import cats.effect.{ExitCode, IO, IOApp}
import com.ruchij.config.ServiceConfiguration
import com.ruchij.dao.game.InMemoryGameDao
import com.ruchij.exceptions.ResourceNotFoundException
import com.ruchij.services.file.{Fs2ResourceStore, ResourceStore}
import com.ruchij.services.game.GameServiceImpl
import com.ruchij.services.health.HealthServiceImpl
import com.ruchij.web.Routes
import fs2.text
import org.http4s.blaze.server.BlazeServerBuilder
import pureconfig.ConfigSource

object App extends IOApp {
  private val DataSource = "words.txt"

  override def run(args: List[String]): IO[ExitCode] =
    for {
      configObjectSource <- IO.delay(ConfigSource.defaultApplication)
      serviceConfiguration <- ServiceConfiguration.parse[IO](configObjectSource)

      fileStore = new Fs2ResourceStore[IO]
      data <- loadData(fileStore, DataSource)

      gameDao = new InMemoryGameDao[IO]
      healthService = new HealthServiceImpl[IO](serviceConfiguration.buildInformation)

      gameService = new GameServiceImpl[IO](data, gameDao)

      exitCode <-
        BlazeServerBuilder[IO]
          .withHttpApp(Routes(gameService, healthService))
          .bindHttp(serviceConfiguration.httpConfiguration.port, serviceConfiguration.httpConfiguration.host)
          .serve.compile.lastOrError
    }
    yield exitCode

  def loadData[F[_]: Sync](fileStore: ResourceStore[F], dataSource: String): F[Vector[String]] =
    OptionT(fileStore.read(dataSource))
      .semiflatMap { byteStream =>
        byteStream
          .through(text.utf8.decode)
          .through(text.lines)
          .compile
          .toVector
      }
      .getOrElseF {
        ApplicativeError[F, Throwable].raiseError {
          ResourceNotFoundException(s"Unable to find data source at $dataSource")
        }
      }


}
