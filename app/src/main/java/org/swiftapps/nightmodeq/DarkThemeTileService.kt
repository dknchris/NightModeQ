package org.swiftapps.nightmodeq

import android.app.Service
import android.content.Context
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

class DarkThemeTileService : TileService() {

    private val ntfHelper by lazy { NotificationHelper(applicationContext) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val useForegroundService = intent?.getBooleanExtra(EXTRA_USE_FOREGROUND, false) == true
        if (useForegroundService) {
            // Create notification channel
            ntfHelper.createChannel()
            startForeground(ntfHelper.ntfId, ntfHelper.createForegroundNotification())
            updateTile()
        }

        return Service.START_STICKY
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.d(logTag, "onStartListening")
        updateTile()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        Log.d(logTag, "onTileAdded")
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        Log.d(logTag, "onClick")

        val newMode: NightModeManager.NightMode = NightModeManager.toggleNightMode()
        updateTile(newMode)
    }

    private fun updateTile(newMode: NightModeManager.NightMode = NightModeManager.getMode()) {
        qsTile?.apply {
            state = when (newMode) {
                NightModeManager.NightMode.yes -> Tile.STATE_ACTIVE
                NightModeManager.NightMode.no -> Tile.STATE_INACTIVE
                NightModeManager.NightMode.auto -> Tile.STATE_INACTIVE
            }
            updateTile()
        }
    }

    companion object {

        private val logTag by lazy { "TonesTileService" }
        private val EXTRA_USE_FOREGROUND by lazy { "use_foreground" }

        fun startService(ctx: Context, useForegroundService: Boolean) {
            val serviceIntent = Intent(ctx.applicationContext, DarkThemeTileService::class.java)
            if (useForegroundService) {
                serviceIntent.putExtra(EXTRA_USE_FOREGROUND, true)
            }
            ctx.startForegroundService(serviceIntent)
        }
    }
}