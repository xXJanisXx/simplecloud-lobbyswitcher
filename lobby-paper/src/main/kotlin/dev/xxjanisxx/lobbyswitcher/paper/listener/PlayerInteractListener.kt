package dev.xxjanisxx.lobbyswitcher.paper.listener

import dev.xxjanisxx.lobbyswitcher.shared.inventory.InventoryHandler
import dev.xxjanisxx.lobbyswitcher.shared.item.LobbyItemHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractListener(
    private val itemHandler: LobbyItemHandler,
    private val inventoryHandler: InventoryHandler
) : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val action = event.action

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val item = event.item ?: return
        val configItem = itemHandler.build()

        if (!item.isSimilar(configItem)) {
            return
        }

        event.isCancelled = true

        inventoryHandler.open(player)
    }
}