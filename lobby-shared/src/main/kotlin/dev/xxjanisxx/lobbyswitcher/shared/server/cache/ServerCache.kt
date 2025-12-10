package dev.xxjanisxx.lobbyswitcher.shared.server.cache

import app.simplecloud.api.server.Server
import dev.xxjanisxx.lobbyswitcher.shared.server.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ServerCache {
    private val _servers = MutableStateFlow<Map<String, CachedServer>>(emptyMap())
    val servers: StateFlow<Map<String, CachedServer>> = _servers.asStateFlow()

    fun updateServer(server: Server, extendedState: State) {
        val cached = CachedServer(
            server = server,
            extendedState = extendedState,
            lastUpdated = System.currentTimeMillis()
        )
        _servers.value += (server.serverId to cached)
    }

    fun removeServer(serverId: String) {
        _servers.value -= serverId
    }

    fun getAllServers(): List<CachedServer> {
        return _servers.value.values.toList()
    }

    fun clear() {
        _servers.value = emptyMap()
    }

    fun clearGroups(groupNames: List<String>) {
        _servers.value = _servers.value.filterValues { cached ->
            cached.server.group?.name !in groupNames
        }
    }
}

data class CachedServer(
    val server: Server,
    val extendedState: State,
    val lastUpdated: Long
)
