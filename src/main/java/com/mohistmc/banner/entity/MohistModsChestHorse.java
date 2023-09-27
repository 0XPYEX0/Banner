package com.mohistmc.banner.entity;

import com.mohistmc.banner.api.EntityAPI;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftChestedHorse;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

public class MohistModsChestHorse extends CraftChestedHorse {

    public String entityName;

    public MohistModsChestHorse(CraftServer server, AbstractChestedHorse entity) {
        super(server, entity);
        this.entityName = EntityAPI.entityName(entity);
    }

    @Override
    public String toString() {
        return "MohistModsChestHorse{" + entityName + '}';
    }

    @Override
    public AbstractChestedHorse getHandle() {
        return (AbstractChestedHorse) entity;
    }

    @Override
    public EntityType getType() {
        return EntityAPI.entityType(entityName);
    }

    @Override
    public Horse.Variant getVariant() {
        return Horse.Variant.HORSE;
    }

    @Override
    public EntityCategory getCategory() {
        return EntityCategory.NONE;
    }
}
