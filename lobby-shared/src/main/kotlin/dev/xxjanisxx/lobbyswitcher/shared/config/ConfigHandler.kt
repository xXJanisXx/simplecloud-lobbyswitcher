package dev.xxjanisxx.lobbyswitcher.shared.config

import dev.xxjanisxx.lobbyswitcher.shared.config.serializer.ItemStackSerializer
import org.bukkit.inventory.ItemStack
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File

class ConfigHandler(private val dir: File) {

    lateinit var lobbySwitcherConfig: LobbySwitcherConfig
        private set

    lateinit var messageConfig: MessageConfig
        private set

    private fun createLoader(file: File): YamlConfigurationLoader {
        return YamlConfigurationLoader.builder()
            .path(file.toPath())
            .nodeStyle(NodeStyle.BLOCK)
            .defaultOptions { opts ->
                opts.serializers {
                    it.registerAnnotatedObjects(objectMapperFactory())
                    it.register(ItemStack::class.java, ItemStackSerializer())
                }
            }
            .build()
    }

    private fun <T> load(fileName: String, clazz: Class<T>, default: T): T {
        val file = File(dir, fileName)

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val loader = createLoader(file)

        if (!file.exists()) {
            val node = loader.createNode()
            node.set(clazz, default)
            loader.save(node)
            return default
        }

        val node: CommentedConfigurationNode = loader.load()

        return node.get(clazz) ?: run {
            val newNode = loader.createNode()
            newNode.set(clazz, default)
            loader.save(newNode)
            default
        }
    }

    fun loadAll() {
        lobbySwitcherConfig = load("config.yml", LobbySwitcherConfig::class.java, LobbySwitcherConfig())
        messageConfig = load("messages.yml", MessageConfig::class.java, MessageConfig())
    }

    fun reload() {
        try {
            loadAll()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to reload configs", e)
        }
    }

    fun save() {
        saveConfig("config.yml", LobbySwitcherConfig::class.java, lobbySwitcherConfig)
        saveConfig("messages.yml", MessageConfig::class.java, messageConfig)
    }

    private fun <T> saveConfig(fileName: String, clazz: Class<T>, config: T) {
        val file = File(dir, fileName)
        val loader = createLoader(file)
        val node = loader.createNode()
        node.set(clazz, config)
        loader.save(node)
    }

}