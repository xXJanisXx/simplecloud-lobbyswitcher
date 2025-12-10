package dev.xxjanisxx.lobbyswitcher.shared.server.handler

import app.simplecloud.api.server.Server
import app.simplecloud.api.server.ServerState
import dev.xxjanisxx.lobbyswitcher.shared.server.State
import org.bukkit.entity.Player

class ServerStateHandler(
    private val currentPlayerGetter: () -> Player? = { null }
) {
    fun calculateExtendedState(server: Server): State {
        val currentPlayer = currentPlayerGetter()
        if (currentPlayer != null && isPlayerOnServer(currentPlayer, server)) {
            return State.CONNECTED
        }
        return when (server.state) {
            ServerState.PREPARING -> State.PREPARING
            ServerState.STARTING -> State.STARTING
            ServerState.STOPPING -> State.STOPPING
            ServerState.UNKNOWN_STATE -> State.OFFLINE

            ServerState.AVAILABLE, ServerState.INGAME -> {
                when {
                    isFull(server) -> State.FULL
                    isEmpty(server) -> State.EMPTY
                    else -> State.ONLINE
                }
            }
        }
    }

    private fun isPlayerOnServer(player: Player, server: Server): Boolean {
        val currentServerName = getCurrentServerName()
        return currentServerName == server.serverBase.name + "-" + server.numericalId
    }

    private fun getCurrentServerName(): String {
        return System.getenv("SIMPLECLOUD_SERVER_NAME") ?: "unknown"
    }

    private fun isFull(server: Server): Boolean {
        val playerCount = server.playerCount ?: 0
        val maxPlayers = server.maxPlayers
        return playerCount >= maxPlayers
    }

    private fun isEmpty(server: Server): Boolean {
        val playerCount = server.playerCount ?: 0
        return playerCount == 0
    }

    fun shouldShowServer(
        server: Server,
        showPreparing: Boolean,
        showOffline: Boolean,
        showStarting: Boolean,
        showStopping: Boolean
    ): Boolean {
        return when (server.state) {
            ServerState.PREPARING -> showPreparing
            ServerState.STARTING -> showStarting
            ServerState.STOPPING -> showStopping
            ServerState.UNKNOWN_STATE -> showOffline
            ServerState.AVAILABLE, ServerState.INGAME -> true
        }
    }
}
