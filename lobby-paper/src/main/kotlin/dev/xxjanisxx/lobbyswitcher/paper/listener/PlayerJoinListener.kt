package dev.xxjanisxx.lobbyswitcher.paper.listener

import dev.xxjanisxx.lobbyswitcher.shared.config.ConfigHandler
import dev.xxjanisxx.lobbyswitcher.shared.item.LobbyItemHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(
    private val config: ConfigHandler,
) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val item = LobbyItemHandler(config).build()
        val slot = config.lobbySwitcherConfig.lobbyItem.slot

        if (!config.lobbySwitcherConfig.lobbyItem.enabled) {
            return
        }

        player.inventory.setItem(slot, item)
    }
}