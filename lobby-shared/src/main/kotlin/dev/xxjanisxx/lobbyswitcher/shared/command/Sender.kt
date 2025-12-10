package dev.xxjanisxx.lobbyswitcher.shared.command

import net.kyori.adventure.text.Component

interface Sender {
    fun sendMessage(message: Component)
}
