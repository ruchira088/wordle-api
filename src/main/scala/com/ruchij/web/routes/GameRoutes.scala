package com.ruchij.web.routes

import cats.effect.Async
import cats.implicits._
import com.ruchij.services.game.GameService
import com.ruchij.web.requests.{CreateGameRequest, RequestOps, ValidateGuessRequest}
import com.ruchij.web.responses.GameResponse
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object GameRoutes {

  def apply[F[_]: Async](gameService: GameService[F])(implicit dsl: Http4sDsl[F]): HttpRoutes[F] = {
    import dsl._

    HttpRoutes.of[F] {
      case request @ POST -> Root =>
        for {
          createGameRequest <- request.to[CreateGameRequest]
          game <- gameService.createGame(createGameRequest.wordLength, createGameRequest.guessCount)
          response <- Created(GameResponse.from(game))
        } yield response

      case GET -> Root / "id" / gameId =>
        for {
          game <- gameService.findById(gameId)
          response <- Ok(GameResponse.from(game))
        }
        yield response

      case request @ POST -> Root / "id" / gameId =>
        for {
          validateGuessRequest <- request.to[ValidateGuessRequest]
          guessResult <- gameService.validateGuess(gameId, validateGuessRequest.guess)
          response <- Ok(guessResult)
        }
        yield response
    }
  }

}
