package com.myassistant.app

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class AssistantTileService : TileService() {

    override fun onStartListening() {
        refresh()
    }

    override fun onClick() {
        Prefs.setOn(this, !Prefs.isOn(this))
        refresh()
    }

    private fun refresh() {
        val tile = qsTile ?: return
        val on = Prefs.isOn(this)
        tile.state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = if (on) "এআই চালু" else "এআই বন্ধ"
        tile.updateTile()
    }
}
