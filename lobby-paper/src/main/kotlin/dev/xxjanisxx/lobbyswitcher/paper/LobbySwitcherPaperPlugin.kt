package dev.xxjanisxx.lobbyswitcher.paper

import app.simplecloud.api.CloudApi
import com.noxcrew.interfaces.InterfacesListeners
import dev.xxjanisxx.lobbyswitcher.paper.listener.PlayerInteractListener
import dev.xxjanisxx.lobbyswitcher.paper.listener.PlayerJoinListener
import dev.xxjanisxx.lobbyswitcher.paper.sender.PaperSender
import dev.xxjanisxx.lobbyswitcher.paper.sender.PaperSenderMapper
import dev.xxjanisxx.lobbyswitcher.shared.command.LobbySwitcherCommandHandler
import dev.xxjanisxx.lobbyswitcher.shared.command.LobbysCommandHandler
import dev.xxjanisxx.lobbyswitcher.shared.config.ConfigHandler
import dev.xxjanisxx.lobbyswitcher.shared.inventory.InventoryHandler
import dev.xxjanisxx.lobbyswitcher.shared.item.LobbyItemHandler
import dev.xxjanisxx.lobbyswitcher.shared.server.cache.ServerCache
import dev.xxjanisxx.lobbyswitcher.shared.server.handler.ServerHandler
import dev.xxjanisxx.lobbyswitcher.shared.server.handler.ServerStateHandler
import dev.xxjanisxx.lobbyswitcher.shared.server.listener.GroupEventsListener
import dev.xxjanisxx.lobbyswitcher.shared.server.listener.ServerEventsListener
import dev.xxjanisxx.lobbyswitcher.shared.server.repository.ServerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.slf4j.LoggerFactory

class LobbySwitcherPaperPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: LobbySwitcherPaperPlugin
            private set
    }

    private lateinit var api: CloudApi
    private lateinit var commandManager: PaperCommandManager<PaperSender>
    private lateinit var config: ConfigHandler
    private lateinit var serverCache: ServerCache
    private lateinit var serverRepository: ServerRepository
    private lateinit var stateHandler: ServerStateHandler
    private lateinit var serverHandler: ServerHandler
    private lateinit var serverEventsListener: ServerEventsListener
    private lateinit var groupEventsListener: GroupEventsListener
    private lateinit var inventoryHandler: InventoryHandler
    private lateinit var lobbyItemHandler: LobbyItemHandler

    private val logger = LoggerFactory.getLogger(LobbySwitcherPaperPlugin::class.java)
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        logger.info("Starting up lobbyswitcher...")
        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        InterfacesListeners.install(this)
        config = ConfigHandler(dataFolder)
        config.loadAll()
        api = CloudApi.create()
        lobbyItemHandler = LobbyItemHandler(config)
        serverCache = ServerCache()
        stateHandler = ServerStateHandler()
        serverHandler = ServerHandler(stateHandler)
        serverRepository = ServerRepository(
            api = api,
            serverCache = serverCache,
            stateHandler = stateHandler
        )

        serverEventsListener = ServerEventsListener(
            api = api,
            scope = scope,
            serverRepository = serverRepository
        )

        groupEventsListener = GroupEventsListener(
            api = api,
            scope = scope,
            serverRepository = serverRepository,
            lobbyGroupNames = {
                config.lobbySwitcherConfig.general.lobbyServerGroups
            }

        )

        inventoryHandler = InventoryHandler(
            plugin = this,
            config = config,
            scope = scope,
            serverRepository = serverRepository,
            serverHandler = serverHandler
        )

        initializeCommandManager()
        LobbysCommandHandler(
            manager = commandManager,
            config = config,
            inventoryHandler = inventoryHandler
        ).register()

        LobbySwitcherCommandHandler(
            manager = commandManager,
            config = config
        ).register()

        Bukkit.getPluginManager().registerEvents(PlayerJoinListener(config), this)
        Bukkit.getPluginManager().registerEvents(PlayerInteractListener(lobbyItemHandler, inventoryHandler), this)

        serverEventsListener.start()
        groupEventsListener.start()

        inventoryHandler.initialize()
        logger.info("Successfully started lobbyswitcher.")
    }

    override fun onDisable() {
        logger.info("Shutting down lobbyswitcher...")
        config.save()
        serverCache.clear()
        serverEventsListener.stop()
        groupEventsListener.stop()
        logger.info("Successfully stopped lobbyswitcher.")
    }

    private fun initializeCommandManager() {
        commandManager = PaperCommandManager.builder(PaperSenderMapper())
            .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
            .buildOnEnable(this)
    }

}
