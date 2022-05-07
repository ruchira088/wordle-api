package com.ruchij.dao.game.models

import org.joda.time.DateTime

case class Game(id: String, createdAt: DateTime, word: String, guessCount: Int, remainingGuesses: Int)
