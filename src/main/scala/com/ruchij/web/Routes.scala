package com.ruchij.web

import cats.effect.Async
import com.ruchij.services.game.GameService
import com.ruchij.services.health.HealthService
import com.ruchij.web.middleware.{ExceptionHandler, NotFoundHandler}
import com.ruchij.web.routes.{GameRoutes, HealthRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server.middleware.GZip
import org.http4s.{HttpApp, HttpRoutes}

object Routes {
  def apply[F[_]: Async](gameService: GameService[F], healthService: HealthService[F]): HttpApp[F] = {
    implicit val dsl: Http4sDsl[F] = new Http4sDsl[F] {}

    val routes: HttpRoutes[F] =
      Router(
        "/game" -> GameRoutes(gameService),
        "/service" -> HealthRoutes(healthService)
      )

    GZip {
      ExceptionHandler {
        NotFoundHandler(routes)
      }
    }
  }
}
