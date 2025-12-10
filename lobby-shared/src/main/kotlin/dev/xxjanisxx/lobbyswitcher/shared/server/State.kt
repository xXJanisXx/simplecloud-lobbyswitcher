package dev.xxjanisxx.lobbyswitcher.shared.server

enum class State {

    /**
     * Server is Online.
     */
    ONLINE,

    /**
     * Server is offline.
     */
    OFFLINE,

    /**
     * Server is being prepared.
     */
    PREPARING,

    /**
     * Server is in starting up.
     */
    STARTING,

    /**
     * Server is in Stopping.
     */
    STOPPING,

    /**
     * Server has no free slots.
     */
    FULL,

    /**
     * Player is already connected to the server.
     */
    CONNECTED,

    /**
     * Server has no online players.
     */
    EMPTY
}