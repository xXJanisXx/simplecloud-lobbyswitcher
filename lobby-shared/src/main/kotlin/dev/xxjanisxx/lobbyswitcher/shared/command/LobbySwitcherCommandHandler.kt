package dev.xxjanisxx.lobbyswitcher.shared.command

import dev.xxjanisxx.lobbyswitcher.shared.config.ConfigHandler
import net.kyori.adventure.text.minimessage.MiniMessage
import org.incendo.cloud.CommandManager
import org.incendo.cloud.context.CommandContext

class LobbySwitcherCommandHandler<C : Sender>(
    private val manager: CommandManager<C>,
    private val config: ConfigHandler
) {
    private val mm = MiniMessage.miniMessage()

    fun register() {
        manager.command(
            manager.commandBuilder("lobbyswitcher")
                .literal("reload")
                .handler { ctx: CommandContext<C> ->
                    ctx.sender().sendMessage(mm.deserialize(config.messageConfig.commandReload))
                    try {
                        config.reload()
                        ctx.sender().sendMessage(mm.deserialize(config.messageConfig.commandReloadSuccesfully))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ctx.sender().sendMessage(mm.deserialize(config.messageConfig.commandReloadFailed))
                    }
                }
                .permission("lobbyswitcher.command.reload")
                .build()
        )
    }

}