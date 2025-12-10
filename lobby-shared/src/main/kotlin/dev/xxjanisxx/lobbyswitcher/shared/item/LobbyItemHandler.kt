package dev.xxjanisxx.lobbyswitcher.shared.item

import dev.xxjanisxx.lobbyswitcher.shared.config.ConfigHandler
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack

class LobbyItemHandler(
    private val config: ConfigHandler
) {
    private val mm = MiniMessage.miniMessage()

    fun build() : ItemStack {
        val item = config.lobbySwitcherConfig.lobbyItem.type
        val name = mm.deserialize(config.lobbySwitcherConfig.lobbyItem.name)
        val lore = config.lobbySwitcherConfig.lobbyItem.lore.map(mm::deserialize)

        val meta = item.itemMeta!!

        meta.displayName(name)
        meta.lore(lore)

        item.itemMeta = meta
        return item
    }
}