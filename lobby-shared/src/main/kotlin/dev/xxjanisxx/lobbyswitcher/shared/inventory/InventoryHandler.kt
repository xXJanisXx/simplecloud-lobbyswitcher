package dev.xxjanisxx.lobbyswitcher.shared.inventory

import dev.xxjanisxx.lobbyswitcher.shared.config.ConfigHandler
import dev.xxjanisxx.lobbyswitcher.shared.inventory.item.ItemBuilder
import dev.xxjanisxx.lobbyswitcher.shared.server.handler.ServerHandler
import dev.xxjanisxx.lobbyswitcher.shared.server.repository.ServerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class InventoryHandler(
    private val plugin: Plugin,
    private val config: ConfigHandler,
    private val serverRepository: ServerRepository,
    private val serverHandler: ServerHandler,
    private val scope: CoroutineScope
) {
    private lateinit var lobbySwitcherInventory: LobbySwitcherInventory
    private lateinit var itemBuilder: ItemBuilder
    private lateinit var clickHandler: ClickHandler

    fun initialize() {
        itemBuilder = ItemBuilder(
            layoutConfig = config.lobbySwitcherConfig.gui.layout
        )

        clickHandler = ClickHandler(
            plugin = plugin,
            config = config
        )

        val lobbyGroups = config.lobbySwitcherConfig.general.lobbyServerGroups
        val serversFlow = serverRepository.observeServersByGroups(lobbyGroups)

        lobbySwitcherInventory = LobbySwitcherInventory(
            config = config,
            serverHandler = serverHandler,
            itemBuilder = itemBuilder,
            clickHandler = clickHandler,
            serversFlow = serversFlow
        )
    }

    fun open(player: Player) {
        if (!::lobbySwitcherInventory.isInitialized) {
            initialize()
        }
        scope.launch(Dispatchers.IO) {
            try {
                lobbySwitcherInventory.open(player)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
