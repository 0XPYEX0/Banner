package com.mohistmc.banner.entity;

import com.mohistmc.banner.api.EntityAPI;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftProjectile;
import org.bukkit.entity.EntityType;

public class MohistModsThrowableEntity extends CraftProjectile {

    public String entityName;

    public MohistModsThrowableEntity(CraftServer server, ThrowableProjectile entity) {
        super(server, entity);
        this.entityName = EntityAPI.entityName(entity);
    }

    @Override
    public ThrowableProjectile getHandle() {
        return (ThrowableProjectile) entity;
    }

    @Override
    public EntityType getType() {
        return EntityAPI.entityType(entityName);
    }

    @Override
    public String toString() {
        return "MohistModsThrowableEntity{" + entityName + '}';
    }
}
