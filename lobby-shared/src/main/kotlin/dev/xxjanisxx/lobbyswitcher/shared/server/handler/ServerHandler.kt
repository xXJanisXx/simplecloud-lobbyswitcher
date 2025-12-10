package dev.xxjanisxx.lobbyswitcher.shared.server.handler

import app.simplecloud.api.server.Server
import dev.xxjanisxx.lobbyswitcher.shared.config.GeneralConfig
import dev.xxjanisxx.lobbyswitcher.shared.server.cache.CachedServer
import dev.xxjanisxx.lobbyswitcher.shared.server.State


class ServerHandler(
    private val stateHandler: ServerStateHandler
) {
    private fun filterServers(
        servers: List<CachedServer>,
        config: GeneralConfig
    ): List<CachedServer> {
        return servers.filter { cached ->
            shouldShowServer(cached.server, config)
        }
    }

    fun filterAndSortServers(
        servers: List<CachedServer>,
        config: GeneralConfig
    ): List<CachedServer> {
        return filterServers(servers, config)
            .sortedWith(compareBy(
                { getStatePriority(it.extendedState) },
                { it.server.numericalId }
            ))
    }

    private fun shouldShowServer(server: Server, config: GeneralConfig): Boolean {
        return stateHandler.shouldShowServer(
            server = server,
            showPreparing = config.showPreparingServers,
            showOffline = config.showOfflineServers,
            showStarting = config.showStartingServers,
            showStopping = config.showStoppingServers
        )
    }

    private fun getStatePriority(state: State): Int {
        return when (state) {
            State.CONNECTED -> 0
            State.ONLINE -> 1
            State.EMPTY -> 2
            State.FULL -> 3
            State.STARTING -> 4
            State.PREPARING -> 5
            State.STOPPING -> 6
            State.OFFLINE -> 7
        }
    }
}
