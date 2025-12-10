package dev.xxjanisxx.lobbyswitcher.shared.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class MessageConfig(
    val commandReload: String = "<color:#38bdf8><bold>⚡</bold></color> Reloading config...",
    val commandReloadSuccesfully: String = "<color:#38bdf8><bold>⚡</bold></color> <green>Succesfully reloaded config.",
    val commandReloadFailed: String = "<color:#38bdf8><bold>⚡</bold></color> <red>Failed to reload config.",

    val serverAlreadyConnected: String = "<yellow>You are already connected to this server!",
    val serverFull: String = "<red>This server is full!",
    val serverStarting: String = "<yellow>This server is still starting...",
    val serverPreparing: String = "<yellow>This server is being prepared...",
    val serverStopping: String = "<red>This server is stopping...",
    val serverOffline: String = "<red>This server is offline!",
    val serverCannotConnect: String = "<red>You cannot connect to this server.",

    val connectToServer: String = "<green>Connecting to <white>Server<green>...",
    val failedConnectToServer: String = "<red>Failed to connect to server"
)
