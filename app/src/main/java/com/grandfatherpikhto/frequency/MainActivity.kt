package com.grandfatherpikhto.frequency

import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.grandfatherpikhto.frequency.databinding.ActivityMainBinding
import kotlin.experimental.and
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var intentService:Intent ?= null
    private val modulationModel by viewModels<ModulationModel>()

    companion object {
        const val TAG:String = "MainActivity"
        private var instance:MainActivity ?= null
        fun getInstance():MainActivity? = instance
        // val generatedSnd:Array<Double> = Array<Double>(size = numSamples, init = { _ -> 0.0 })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate()")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        Intent(this, PlayService::class.java).also { intent ->
            Log.e(TAG, "start Service model: $modulationModel")
            intentService = intent
            applicationContext.startForegroundService(intent)
        }

        instance = this
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    /**
     * Если воспроизведение тона не включено, сервис PlayService
     * будет разрушен
     */
    override fun onDestroy() {
        Log.e(TAG, "onDestroy()")
        if(!modulationModel.enable.value) {
            Intent(this, PlayService::class.java).also { intent ->
                Log.e(TAG, "stopService(${modulationModel.enable.value})")
                stopService(intent)
            }
        }
        super.onDestroy()
    }
}