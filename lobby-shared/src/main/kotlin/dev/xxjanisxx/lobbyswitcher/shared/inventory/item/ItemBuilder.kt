package dev.xxjanisxx.lobbyswitcher.shared.inventory.item

import dev.xxjanisxx.lobbyswitcher.shared.config.ServersLayoutConfig
import dev.xxjanisxx.lobbyswitcher.shared.server.cache.CachedServer
import dev.xxjanisxx.lobbyswitcher.shared.server.State
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack

class ItemBuilder(
    private val layoutConfig: ServersLayoutConfig
) {
    private val mm = MiniMessage.miniMessage()

    fun buildItem(cachedServer: CachedServer): ItemStack {
        val server = cachedServer.server
        val state = cachedServer.extendedState
        val stateConfig = getStateConfig(state)
        val item = ItemStack(stateConfig.item)
        val displayName = buildDisplayName(server.serverBase.name, server.numericalId)
        val lore = buildLore(cachedServer, stateConfig.lore)

        item.editMeta { meta ->
            meta.displayName(displayName)
            meta.lore(lore)
        }
        return item
    }

    private fun getStateConfig(state: State) = when (state) {
        State.ONLINE -> layoutConfig.online
        State.OFFLINE -> layoutConfig.offline
        State.PREPARING -> layoutConfig.preparing
        State.STARTING -> layoutConfig.starting
        State.STOPPING -> layoutConfig.stopping
        State.FULL -> layoutConfig.full
        State.CONNECTED -> layoutConfig.connected
        State.EMPTY -> layoutConfig.empty
    }

    private fun buildDisplayName(groupName: String, numericalId: Int): Component {
        val serverName = "$groupName-$numericalId"
        return mm.deserialize("<bold>$serverName")
    }

    private fun buildLore(cachedServer: CachedServer, configuredLore: List<String>): List<Component> {
        val lore = mutableListOf<Component>()

        configuredLore.forEach { line ->
            val replaced = replacePlaceholders(line, cachedServer)
            lore.add(mm.deserialize(replaced))
        }

        return lore
    }

    private fun replacePlaceholders(line: String, cachedServer: CachedServer): String {
        val server = cachedServer.server
        val state = cachedServer.extendedState
        return line
            .replace("<server>", server.serverBase.name)
            .replace("<id>", server.numericalId.toString())
            .replace("<online_players>", (server.playerCount ?: 0).toString())
            .replace("<max_players>", server.maxPlayers.toString())
            .replace("<state>", getStateDisplayName(state))
    }

    private fun getStateDisplayName(state: State): String = when (state) {
        State.CONNECTED -> "Connected"
        State.ONLINE -> "Online"
        State.EMPTY -> "Empty"
        State.FULL -> "Full"
        State.STARTING -> "Starting"
        State.PREPARING -> "Preparing"
        State.STOPPING -> "Stopping"
        State.OFFLINE -> "Offline"
    }
}