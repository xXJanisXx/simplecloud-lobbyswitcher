package dev.xxjanisxx.lobbyswitcher.shared.inventory

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import dev.xxjanisxx.lobbyswitcher.shared.config.ConfigHandler
import dev.xxjanisxx.lobbyswitcher.shared.server.cache.CachedServer
import dev.xxjanisxx.lobbyswitcher.shared.server.State
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ClickHandler(
    private val plugin: Plugin,
    private val config: ConfigHandler
) {
    private val mm = MiniMessage.miniMessage()

    fun handleClick(player: Player, cachedServer: CachedServer): Boolean {
        val state = cachedServer.extendedState
        if (!canJoinServer(state)) {
            sendCannotJoinMessage(player, state)
            return false
        }

        player.closeInventory()

        try {
            connectToServer(player, cachedServer)
            return true
        } catch (e: Exception) {
            player.sendMessage(mm.deserialize(config.messageConfig.failedConnectToServer))
            return true
        }
    }

    private fun canJoinServer(state: State): Boolean {
        return state == State.ONLINE || state == State.EMPTY
    }

    private fun sendCannotJoinMessage(player: Player, state: State) {
        val message = when (state) {
            State.CONNECTED -> config.messageConfig.serverAlreadyConnected
            State.FULL -> config.messageConfig.serverFull
            State.STARTING -> config.messageConfig.serverStarting
            State.PREPARING -> config.messageConfig.serverPreparing
            State.STOPPING -> config.messageConfig.serverStopping
            State.OFFLINE -> config.messageConfig.serverOffline
            else -> config.messageConfig.serverCannotConnect
        }
        player.sendMessage(mm.deserialize(message))
    }


    private fun connectToServer(player: Player, cachedServer: CachedServer) {
        val server = cachedServer.server
        val serverName = "${server.serverBase.name}-${server.numericalId}"

        player.sendMessage(mm.deserialize(config.messageConfig.connectToServer))

        val out:ByteArrayDataOutput = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(serverName)

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
    }

}
