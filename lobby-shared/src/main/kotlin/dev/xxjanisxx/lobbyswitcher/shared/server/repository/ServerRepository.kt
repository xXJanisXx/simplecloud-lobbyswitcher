package dev.xxjanisxx.lobbyswitcher.shared.server.repository

import app.simplecloud.api.CloudApi
import app.simplecloud.api.server.Server
import dev.xxjanisxx.lobbyswitcher.shared.server.cache.CachedServer
import dev.xxjanisxx.lobbyswitcher.shared.server.cache.ServerCache
import dev.xxjanisxx.lobbyswitcher.shared.server.handler.ServerStateHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.future.await

class ServerRepository(
    private val api: CloudApi,
    private val serverCache: ServerCache,
    private val stateHandler: ServerStateHandler
) {
    fun observeServersByGroups(groupNames: List<String>): Flow<List<CachedServer>> {
        return serverCache.servers.map { serversMap ->
            serversMap.values.filter { cached ->
                cached.server.group?.name in groupNames
            }
        }
    }

    suspend fun loadServersByGroups(groupNames: List<String>) {
        serverCache.clearGroups(groupNames)

        groupNames.forEach { groupName ->
            val servers = api.server().getServersByGroup(groupName).await()
            servers.forEach { server ->
                updateServerInCache(server)
            }
        }
    }

    private suspend fun getServerById(serverId: String): Server {
        return api.server().getServerById(serverId).await()
    }

    private fun updateServerInCache(server: Server) {
        val extendedState = stateHandler.calculateExtendedState(server)
        serverCache.updateServer(server, extendedState)
    }

    fun removeServerFromCache(serverId: String) {
        serverCache.removeServer(serverId)
    }

    fun getAllCachedServers(): List<CachedServer> {
        return serverCache.getAllServers()
    }

    suspend fun refreshServer(serverId: String) {
        try {
            val server = getServerById(serverId)
            updateServerInCache(server)
        } catch (e: Exception) {
            removeServerFromCache(serverId)
        }
    }
}
