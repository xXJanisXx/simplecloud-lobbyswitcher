package dev.xxjanisxx.lobbyswitcher.shared.inventory

import com.noxcrew.interfaces.drawable.Drawable.Companion.drawable
import com.noxcrew.interfaces.element.StaticElement
import com.noxcrew.interfaces.interfaces.buildChestInterface
import com.noxcrew.interfaces.properties.interfaceProperty
import com.noxcrew.interfaces.utilities.forEachInGrid
import dev.xxjanisxx.lobbyswitcher.shared.config.ConfigHandler
import dev.xxjanisxx.lobbyswitcher.shared.inventory.item.ItemBuilder
import dev.xxjanisxx.lobbyswitcher.shared.server.cache.CachedServer
import dev.xxjanisxx.lobbyswitcher.shared.server.handler.ServerHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class LobbySwitcherInventory(
    private val config: ConfigHandler,
    private val serverHandler: ServerHandler,
    private val itemBuilder: ItemBuilder,
    private val clickHandler: ClickHandler,
    private val serversFlow: Flow<List<CachedServer>>
) {
    private val mm = MiniMessage.miniMessage()

    suspend fun open(player: Player) {
        val inventory = buildInterface(player)
        inventory.open(player)
    }

    private fun buildInterface(player: Player) = buildChestInterface {
        rows = config.lobbySwitcherConfig.gui.rows
        titleSupplier = { _ ->
            mm.deserialize(config.lobbySwitcherConfig.gui.title)
        }

        val serversProperty = interfaceProperty<List<CachedServer>>(emptyList())
        var serversList by serversProperty

        withTransform(serversProperty) { pane, _ ->
            val filteredServers = serverHandler.filterAndSortServers(
                serversList,
                config.lobbySwitcherConfig.general
            )

            val columns = 9
            var row = 0
            var col = 0

            filteredServers.forEach { cachedServer ->
                if (row >= rows) return@forEach

                val itemStack = itemBuilder.buildItem(cachedServer)

                pane[row, col] = StaticElement(drawable(itemStack)) { _ ->
                    completingLater = true

                    GlobalScope.launch {
                        try {
                            clickHandler.handleClick(player, cachedServer)
                            complete()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            complete()
                        }
                    }
                }
                col++
                if (col >= columns) {
                    col = 0
                    row++
                }
            }

            forEachInGrid(rows, columns) { gridRow, gridCol ->
                if (pane.has(gridRow, gridCol)) return@forEachInGrid

                val backgroundItem = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
                    editMeta { meta ->
                        meta.displayName(Component.text(""))
                    }
                }

                pane[gridRow, gridCol] = StaticElement(drawable(backgroundItem))
            }
        }

        GlobalScope.launch {
            serversFlow.collect { servers ->
                serversList = servers
            }
        }
    }

}
