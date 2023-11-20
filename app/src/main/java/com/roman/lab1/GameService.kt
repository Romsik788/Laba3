package com.roman.lab1

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class GameService : Service() {
    private val binder = GameBinder()
    inner class GameBinder : Binder() {
        private val handler = Handler(Looper.getMainLooper())

        fun getService(): GameService {
            return this@GameService
        }

        fun makeEnemyMove(board: List<SquareState>?, callback: (Int?) -> Unit) {
            Thread {
                val emptySquares = board?.mapIndexedNotNull { index, squareState ->
                    if (squareState == SquareState.EMPTY) index else null
                }

                val result = emptySquares?.takeIf { it.isNotEmpty() }?.random()

                handler.post {
                    callback(result)
                }
            }.start()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}