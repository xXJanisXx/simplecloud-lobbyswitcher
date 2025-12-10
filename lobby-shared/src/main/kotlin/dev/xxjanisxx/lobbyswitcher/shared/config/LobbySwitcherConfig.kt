package dev.xxjanisxx.lobbyswitcher.shared.config

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class LobbySwitcherConfig(
    val general: GeneralConfig = GeneralConfig(),
    val command: CommandConfig = CommandConfig(),
    val lobbyItem: LobbyItemConfig = LobbyItemConfig(),
    val gui: GuiConfig = GuiConfig()
)

@ConfigSerializable
data class GeneralConfig(
    val lobbyServerGroups: List<String> = listOf("lobby"),
    val showPreparingServers: Boolean = true,
    val showOfflineServers: Boolean = true,
    val showStartingServers: Boolean = true,
    val showStoppingServers: Boolean = true
)

@ConfigSerializable
data class CommandConfig(
    val permission: String = "lobbyswitcher.use",
)

@ConfigSerializable
data class LobbyItemConfig(
    val enabled: Boolean = true,
    val type: ItemStack = ItemStack(Material.COMPASS),
    val name: String = "<gradient:#00d4ff:#0099ff><bold>Lobby Switcher</bold></gradient>",
    val slot: Int = 6,
    val lore: List<String> = listOf(
        "",
        "<gray>Right-click to open the lobby selector",
        ""
    )
)

@ConfigSerializable
data class GuiConfig(
    val rows: Int = 4,
    val title: String = "<gradient:#00d4ff:#0099ff><bold>Select a Lobby</bold></gradient>",
    val layout: ServersLayoutConfig = ServersLayoutConfig()
)

@ConfigSerializable
data class ServersLayoutConfig(
    val online: ServerStateConfig = ServerStateConfig(
        item = Material.GREEN_WOOL,
        lore = listOf(
            "",
            "<dark_gray>| <gray>Status: <green>Online",
            "<dark_gray>| <gray>Players: <white><online_players><dark_gray>/<white><max_players>",
            "",
            "<dark_gray>» <green>Click to connect"
        )
    ),
    val offline: ServerStateConfig = ServerStateConfig(
        item = Material.RED_WOOL,
        lore = listOf(
            "",
            "<dark_gray>| <gray>Status: <red>Offline",
            "",
            "<dark_gray>» <red>Server is not available"
        )
    ),
    val preparing: ServerStateConfig = ServerStateConfig(
        item = Material.BLUE_WOOL,
        lore = listOf(
            "",
            "<dark_gray>| <gray>Status: <blue>Preparing",
            "",
            "<dark_gray>» <yellow>Server is being prepared…"
        )
    ),
    val starting: ServerStateConfig = ServerStateConfig(
        item = Material.YELLOW_WOOL,
        lore = listOf(
            "",
            "<dark_gray>| <gray>Status: <yellow>Starting",
            "",
            "<dark_gray>» <yellow>Server is starting up..."
        )
    ),
    val stopping: ServerStateConfig = ServerStateConfig(
        item = Material.ORANGE_WOOL,
        lore = listOf(
            "",
            "<dark_gray>| <gray>Status: <gold>Stopping",
            "",
            "<dark_gray>» <red>Server is shutting down"
        )
    ),
    val full: ServerStateConfig = ServerStateConfig(
        item = Material.GOLD_BLOCK,
        lore = listOf(
            "",
            "<dark_gray>| <gray>Status: <gold>Full",
            "<dark_gray>| <gray>Players: <white><online_players><dark_gray>/<white><max_players>",
            "",
            "<dark_gray>» <red>Server is full"
        )
    ),
    val connected: ServerStateConfig = ServerStateConfig(
        item = Material.CYAN_WOOL,
        lore = listOf(
            "",
            "<dark_gray>| <gray>Status: <aqua>Connected",
            "<dark_gray>| <gray>Players: <white><online_players><dark_gray>/<white><max_players>",
            "",
            "<dark_gray>» <aqua>You are here!"
        )
    ),
    val empty: ServerStateConfig = ServerStateConfig(
        item = Material.LIGHT_GRAY_WOOL,
        lore = listOf(
            "",
            "<dark_gray>| <gray>Status: <gray>Empty",
            "<dark_gray>| <gray>Players: <white>0<dark_gray>/<white><max_players>",
            "",
            "<dark_gray>» <green>Click to connect"
        )
    )
)

@ConfigSerializable
data class ServerStateConfig(
    val item: Material,
    val lore: List<String>
)