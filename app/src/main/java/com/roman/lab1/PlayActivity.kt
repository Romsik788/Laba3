package com.roman.lab1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.roman.lab1.databinding.ActivityPlayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayBinding
    private val viewModel: GameViewModel by viewModels()
    private lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.username.text = intent.getStringExtra("USERNAME")
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as GameService.GameBinder
                viewModel.gameService = binder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                viewModel.gameService = null
            }
        }
        val serviceIntent = Intent(this, GameService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        viewModel.gameState.observe(this) {
            binding.item1.text = it[0].text
            binding.item2.text = it[1].text
            binding.item3.text = it[2].text
            binding.item4.text = it[3].text
            binding.item5.text = it[4].text
            binding.item6.text = it[5].text
            binding.item7.text = it[6].text
            binding.item8.text = it[7].text
            binding.item9.text = it[8].text
        }

        viewModel.isUserWin.observe(this) {
            if (it != null) {
                AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage(if (it) "You Win!" else "You Lose!")
                    .setPositiveButton("Restart") { _, _ ->
                        viewModel.resetGame()
                    }
                    .setNegativeButton("OK") { _, _ ->
                        finishAffinity();
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        binding.exit.setOnClickListener {
            finishAffinity();
        }

        binding.restart.setOnClickListener {
            viewModel.resetGame()
        }

        binding.item1.setOnClickListener {
            tryToMakeMove(0)
        }

        binding.item2.setOnClickListener {
            tryToMakeMove(1)
        }

        binding.item3.setOnClickListener {
            tryToMakeMove(2)
        }

        binding.item4.setOnClickListener {
            tryToMakeMove(3)
        }

        binding.item5.setOnClickListener {
            tryToMakeMove(4)
        }

        binding.item6.setOnClickListener {
            tryToMakeMove(5)
        }

        binding.item7.setOnClickListener {
            tryToMakeMove(6)
        }

        binding.item8.setOnClickListener {
            tryToMakeMove(7)
        }

        binding.item9.setOnClickListener {
            tryToMakeMove(8)
        }
    }
    private fun tryToMakeMove(index: Int) {
        viewModel.gameState.value?.let {
            if (it[index] == SquareState.EMPTY) {
                viewModel.changeSquareState(index, SquareState.PLAYER)
                if (viewModel.isUserWin.value == null)
                    viewModel.makeEnemyMove()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}