package com.ruchij.test.mixins

import cats.effect.Async
import com.ruchij.services.game.GameService
import com.ruchij.services.health.HealthService
import com.ruchij.web.Routes
import org.http4s.HttpApp
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest

trait MockedRoutes[F[_]] extends MockFactory with OneInstancePerTest {

  val healthService: HealthService[F] = mock[HealthService[F]]
  val gameService: GameService[F] = mock[GameService[F]]

  val async: Async[F]

  def createRoutes(): HttpApp[F] =
    Routes[F](gameService, healthService)(async)

}
