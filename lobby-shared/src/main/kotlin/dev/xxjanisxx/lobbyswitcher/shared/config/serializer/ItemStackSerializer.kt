package dev.xxjanisxx.lobbyswitcher.shared.config.serializer

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

class ItemStackSerializer : TypeSerializer<ItemStack> {
    override fun serialize(type: Type, obj: ItemStack?, node: ConfigurationNode) {
        if (obj == null) {
            node.set(null)
            return
        }
        node.set(obj.type.name)
    }

    override fun deserialize(type: Type, node: ConfigurationNode): ItemStack {
        if (node.isNull) {
            throw SerializationException("Node is null, cannot deserialize ItemStack")
        }

        val materialName = node.getString()
            ?: throw SerializationException("Material name is null")

        val material = Material.matchMaterial(materialName)
            ?: throw SerializationException("Unknown material: $materialName")

        return ItemStack(material, 1)
    }
}