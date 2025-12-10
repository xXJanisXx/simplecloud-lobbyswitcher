package dev.xxjanisxx.lobbyswitcher.shared.server.listener

import app.simplecloud.api.CloudApi
import app.simplecloud.api.event.Subscription
import dev.xxjanisxx.lobbyswitcher.shared.server.repository.ServerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Listen to simplecloud server events.
 */
class ServerEventsListener(
    private val api: CloudApi,
    private val serverRepository: ServerRepository,
    private val scope: CoroutineScope
) {
    private var subscriptions: MutableList<Subscription> = mutableListOf()

    fun start() {
        // server start event
        subscriptions.add(
            api.event().server().onStarted { event ->
                scope.launch {
                    handleServerStarted(event.serverId)
                }
            }
        )

        // server stopped event
        subscriptions.add(
            api.event().server().onStopped { event ->
                handleServerStopped(event.serverId)
            }
        )

        // server state changed event
        subscriptions.add(
            api.event().server().onStateChanged { event ->
                scope.launch {
                    handleServerStateChanged(event.serverId)
                }
            }
        )

        // server update event
        subscriptions.add(
            api.event().server().onUpdated { event ->
                scope.launch {
                    handleServerUpdated(event.serverId)
                }
            }
        )

        // server deleted event
        subscriptions.add(
            api.event().server().onDeleted { event ->
                handleServerDeleted(event.serverId)
            }
        )
    }

    fun stop() {
        subscriptions.forEach { it.close() }
        subscriptions.clear()
    }

    /**
     * Handles server started event.
     */
    private suspend fun handleServerStarted(serverId: String) {
        try {
            serverRepository.refreshServer(serverId)
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Handles server stopped event.
     */
    private fun handleServerStopped(serverId: String) {
        serverRepository.removeServerFromCache(serverId)
    }

    /**
     * Handles server state changed event.
     */
    private suspend fun handleServerStateChanged(serverId: String) {
        try {
            serverRepository.refreshServer(serverId)
        } catch (e: Exception) {
            serverRepository.removeServerFromCache(serverId)
        }
    }

    /**
     * Handles server updated event.
     */
    private suspend fun handleServerUpdated(serverId: String) {
        try {
            serverRepository.refreshServer(serverId)
        } catch (e: Exception) {
            serverRepository.removeServerFromCache(serverId)
        }
    }

    /**
     * Handles server deleted event.
     */
    private fun handleServerDeleted(serverId: String) {
        serverRepository.removeServerFromCache(serverId)
    }
}