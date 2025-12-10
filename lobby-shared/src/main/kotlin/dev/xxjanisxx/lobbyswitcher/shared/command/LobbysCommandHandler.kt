package dev.xxjanisxx.lobbyswitcher.shared.command

import dev.xxjanisxx.lobbyswitcher.shared.config.ConfigHandler
import dev.xxjanisxx.lobbyswitcher.shared.inventory.InventoryHandler
import org.bukkit.entity.Player
import org.incendo.cloud.CommandManager
import org.incendo.cloud.context.CommandContext

class LobbysCommandHandler<C : Sender>(
    private val manager: CommandManager<C>,
    private val config: ConfigHandler,
    private val inventoryHandler: InventoryHandler
) {
    fun register() {
        manager.command(
            manager.commandBuilder("lobbys")
                .handler { ctx: CommandContext<C> ->
                    val sender = ctx.sender()
                    val player = sender as Player
                    inventoryHandler.open(player)
                }
                .permission(config.lobbySwitcherConfig.command.permission)
                .build()
        )
    }
}