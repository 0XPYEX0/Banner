package com.mohistmc.banner.mixin.core.world.entity.projectile;

import com.mohistmc.banner.injection.world.entity.projectile.InjectionArrow;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Arrow.class)
public abstract class MixinArrow extends AbstractArrow implements InjectionArrow {

    @Shadow @Final private static EntityDataAccessor<Integer> ID_EFFECT_COLOR;

    protected MixinArrow(EntityType<? extends AbstractArrow> entityType, Level level, ItemStack itemStack) {
        super(entityType, level, itemStack);
    }

    @Inject(method = "doPostHurtEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private void banner$pushArrowCause(LivingEntity target, CallbackInfo ci) {
        target.pushEffectCause(EntityPotionEffectEvent.Cause.ARROW);
    }
}
