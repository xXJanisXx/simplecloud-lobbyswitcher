package dev.xxjanisxx.lobbyswitcher.shared.server.listener

import app.simplecloud.api.CloudApi
import app.simplecloud.api.event.Subscription
import dev.xxjanisxx.lobbyswitcher.shared.server.repository.ServerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Listen to simplecloud group events.
 */
class GroupEventsListener(
    private val api: CloudApi,
    private val serverRepository: ServerRepository,
    private val scope: CoroutineScope,
    private val lobbyGroupNames: () -> List<String>
) {
    private var subscriptions: MutableList<Subscription> = mutableListOf()

    fun start() {
        // Group created event.
        subscriptions.add(
            api.event().group().onCreated { event ->
                scope.launch {
                    handleGroupCreated(event.serverGroupId)
                }
            }
        )

        // Group updated event
        subscriptions.add(
            api.event().group().onUpdated { event ->
                scope.launch {
                    handleGroupUpdated(event.serverGroupId)
                }
            }
        )

        // Group deleted event.
        subscriptions.add(
            api.event().group().onDeleted { event ->
                handleGroupDeleted(event.serverGroupId)
            }
        )
    }

    fun stop() {
        subscriptions.forEach { it.close() }
        subscriptions.clear()
    }

    /**
     * Handles group created event.
     */
    private suspend fun handleGroupCreated(groupId: String) {
        try {
            val group = withContext(Dispatchers.IO) {
                api.group().getGroupById(groupId).get()
            }
            val lobbyGroups = lobbyGroupNames()

            if (group.name in lobbyGroups) {
                serverRepository.loadServersByGroups(listOf(group.name))
            }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Handles group updated event
     */
    private suspend fun handleGroupUpdated(groupId: String) {
        try {
            val group = withContext(Dispatchers.IO) {
                api.group().getGroupById(groupId).get()
            }
            val lobbyGroups = lobbyGroupNames()

            if (group.name in lobbyGroups) {
                serverRepository.loadServersByGroups(listOf(group.name))
            }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Handles group deleted event.
     */
    private fun handleGroupDeleted(groupId: String) {
        val cachedServers = serverRepository.getAllCachedServers()
        cachedServers.forEach { cached ->
            if (cached.server.serverGroupId == groupId) {
                serverRepository.removeServerFromCache(cached.server.serverId)
            }
        }
    }
}
