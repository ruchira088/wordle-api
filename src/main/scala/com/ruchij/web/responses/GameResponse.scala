package com.ruchij.web.responses

import com.ruchij.dao.game.models.Game

case class GameResponse(id: String, wordLength: Int, remainingGuessCount: Int)

object GameResponse {
  def from(game: Game): GameResponse =
    GameResponse(game.id, game.word.length, game.remainingGuesses)
}