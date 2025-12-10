package dev.xxjanisxx.lobbyswitcher.paper.sender

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.incendo.cloud.SenderMapper

class PaperSenderMapper : SenderMapper<CommandSourceStack, PaperSender> {
    override fun map(base: CommandSourceStack): PaperSender =
        PaperSender(base)

    override fun reverse(mapped: PaperSender): CommandSourceStack =
        mapped.commandSourceStack
}
