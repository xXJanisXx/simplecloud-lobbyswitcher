package dev.xxjanisxx.lobbyswitcher.paper.sender

import dev.xxjanisxx.lobbyswitcher.shared.command.Sender
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component

class PaperSender(val commandSourceStack: CommandSourceStack) : Sender {
    override fun sendMessage(message: Component) {
        commandSourceStack.sender.sendMessage(message)
    }
}