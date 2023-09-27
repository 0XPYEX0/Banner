package com.mohistmc.banner.injection.world.level.block.entity;

import org.bukkit.craftbukkit.v1_20_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.InventoryHolder;
import org.spigotmc.CustomTimingsHandler;

public interface InjectionBlockEntity {

    default CustomTimingsHandler bridge$tickTimer() {
        return null;
    }

    default CraftPersistentDataContainer bridge$persistentDataContainer() {
        return null;
    }

    default void banner$setPersistentDataContainer(CraftPersistentDataContainer persistentDataContainer) {
    }

    default InventoryHolder bridge$getOwner() {
        return null;
    }
}
