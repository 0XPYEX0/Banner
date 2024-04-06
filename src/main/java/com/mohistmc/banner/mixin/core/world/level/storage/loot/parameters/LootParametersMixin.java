package com.mohistmc.banner.mixin.core.world.level.storage.loot.parameters;

import com.mohistmc.banner.asm.annotation.TransformAccess;
import com.mohistmc.banner.bukkit.BukkitExtraConstants;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LootContextParams.class)
public class LootParametersMixin {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL)
    private static final LootContextParam<Integer> LOOTING_MOD = BukkitExtraConstants.LOOTING_MOD;
}