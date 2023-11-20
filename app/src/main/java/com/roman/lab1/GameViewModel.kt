package com.roman.lab1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roman.lab1.database.MyDatabase
import com.roman.lab1.database.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val database: MyDatabase
) : ViewModel() {

    var currentUser: User? = null
    var gameService: GameService? = null
    val gameState = MutableLiveData(List(9) { SquareState.EMPTY })
    val isUserWin: MutableLiveData<Boolean?> = MutableLiveData(null)

    suspend fun isUserExist(username: String): Boolean {
        currentUser = database.userDao().findByUsername(username).firstOrNull()
        return currentUser != null
    }

    suspend fun isPasswordCorrect(username: String, password: String): Boolean {
        return database.userDao().findByUsername(username).first().password == password
    }

    fun addUser(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentUser = User(0, username, password)
            database.userDao().insertAll(currentUser ?: User(0, username, password))
        }
    }

    fun changeSquareState(index: Int, squareState: SquareState) {
        gameState.value?.let {
            val newGameState = it.toMutableList()
            newGameState[index] = squareState
            gameState.value = newGameState
            checkForWinner()
        }
    }

    private fun checkForWinner() {
        if (gameState.value?.let { isWinner(it, SquareState.PLAYER) } == true) {
            isUserWin.value = true
        } else if (gameState.value?.let { isWinner(it, SquareState.ENEMY) } == true) {
            isUserWin.value = false
        }
    }

    private fun isWinner(state: List<SquareState>, player: SquareState): Boolean {
        val winConditions = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        return winConditions.any { condition ->
            condition.all { index -> state[index] == player }
        }
    }

    fun makeEnemyMove() {
        gameService?.GameBinder()?.makeEnemyMove(gameState.value) { enemyMove ->
            enemyMove?.let {
                changeSquareState(it, SquareState.ENEMY)
            } ?: isUserWin.postValue(false)
        }
    }

    fun resetGame() {
        gameState.value = List(9) { SquareState.EMPTY }
        isUserWin.value = null
    }
}