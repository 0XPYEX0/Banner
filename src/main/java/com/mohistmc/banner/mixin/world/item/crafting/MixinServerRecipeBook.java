package com.mohistmc.banner.mixin.world.item.crafting;

import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.craftbukkit.v1_20_R2.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(ServerRecipeBook.class)
public class MixinServerRecipeBook {

    @Redirect(method = "addRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;isSpecial()Z"))
    public boolean banner$recipeUpdate(RecipeHolder<?> recipe, Collection<Recipe<?>> collection, ServerPlayer playerEntity) {
        return recipe.value().isSpecial() || !CraftEventFactory.handlePlayerRecipeListUpdateEvent(playerEntity, recipe.id());
    }

    @Inject(method = "sendRecipes", cancellable = true, at = @At("HEAD"))
    public void banner$returnIfFail(ClientboundRecipePacket.State state, ServerPlayer player, List<ResourceLocation> recipesIn, CallbackInfo ci) {
        if (player.connection == null) {
            ci.cancel();
        }
    }
}
