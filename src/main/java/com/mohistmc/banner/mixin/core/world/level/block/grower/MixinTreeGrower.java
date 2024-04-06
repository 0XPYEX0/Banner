package com.mohistmc.banner.mixin.core.world.level.block.grower;

import com.mohistmc.banner.bukkit.BukkitExtraConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.bukkit.TreeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TreeGrower.class)
public abstract class MixinTreeGrower {

    @Inject(method = "growTree", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;",
            ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void banner$setTreeType0(ServerLevel serverLevel, ChunkGenerator chunkGenerator,
                                     BlockPos blockPos, BlockState blockState, RandomSource randomSource,
                                     CallbackInfoReturnable<Boolean> cir, ResourceKey resourceKey,
                                     Holder holder, int i, int j) {
        this.setTreeType(holder);
    }

    @Inject(method = "growTree", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;",
            ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void banner$setTreeType1(ServerLevel serverLevel, ChunkGenerator chunkGenerator,
                                    BlockPos blockPos, BlockState blockState, RandomSource randomSource,
                                    CallbackInfoReturnable<Boolean> cir, ResourceKey resourceKey,
                                    ResourceKey resourceKey2, Holder holder2) {
        this.setTreeType(holder2);
    }

    public void setTreeType(Holder<ConfiguredFeature<?, ?>> holder) {
        ResourceKey<ConfiguredFeature<?, ?>> worldgentreeabstract = holder.unwrapKey().get();
        if (worldgentreeabstract == TreeFeatures.OAK || worldgentreeabstract == TreeFeatures.OAK_BEES_005) {
            BukkitExtraConstants.treeType = TreeType.TREE;
        } else if (worldgentreeabstract == TreeFeatures.HUGE_RED_MUSHROOM) {
            BukkitExtraConstants.treeType = TreeType.RED_MUSHROOM;
        } else if (worldgentreeabstract == TreeFeatures.HUGE_BROWN_MUSHROOM) {
            BukkitExtraConstants.treeType = TreeType.BROWN_MUSHROOM;
        } else if (worldgentreeabstract == TreeFeatures.JUNGLE_TREE) {
            BukkitExtraConstants.treeType = TreeType.COCOA_TREE;
        } else if (worldgentreeabstract == TreeFeatures.JUNGLE_TREE_NO_VINE) {
            BukkitExtraConstants.treeType = TreeType.SMALL_JUNGLE;
        } else if (worldgentreeabstract == TreeFeatures.PINE) {
            BukkitExtraConstants.treeType = TreeType.TALL_REDWOOD;
        } else if (worldgentreeabstract == TreeFeatures.SPRUCE) {
            BukkitExtraConstants.treeType = TreeType.REDWOOD;
        } else if (worldgentreeabstract == TreeFeatures.ACACIA) {
            BukkitExtraConstants.treeType = TreeType.ACACIA;
        } else if (worldgentreeabstract == TreeFeatures.BIRCH || worldgentreeabstract == TreeFeatures.BIRCH_BEES_005) {
            BukkitExtraConstants.treeType = TreeType.BIRCH;
        } else if (worldgentreeabstract == TreeFeatures.SUPER_BIRCH_BEES_0002) {
            BukkitExtraConstants.treeType = TreeType.TALL_BIRCH;
        } else if (worldgentreeabstract == TreeFeatures.SWAMP_OAK) {
            BukkitExtraConstants.treeType = TreeType.SWAMP;
        } else if (worldgentreeabstract == TreeFeatures.FANCY_OAK || worldgentreeabstract == TreeFeatures.FANCY_OAK_BEES_005) {
            BukkitExtraConstants.treeType = TreeType.BIG_TREE;
        } else if (worldgentreeabstract == TreeFeatures.JUNGLE_BUSH) {
            BukkitExtraConstants.treeType = TreeType.JUNGLE_BUSH;
        } else if (worldgentreeabstract == TreeFeatures.DARK_OAK) {
            BukkitExtraConstants.treeType = TreeType.DARK_OAK;
        } else if (worldgentreeabstract == TreeFeatures.MEGA_SPRUCE) {
            BukkitExtraConstants.treeType = TreeType.MEGA_REDWOOD;
        } else if (worldgentreeabstract == TreeFeatures.MEGA_PINE) {
            BukkitExtraConstants.treeType = TreeType.MEGA_REDWOOD;
        } else if (worldgentreeabstract == TreeFeatures.MEGA_JUNGLE_TREE) {
            BukkitExtraConstants.treeType = TreeType.JUNGLE;
        } else if (worldgentreeabstract == TreeFeatures.AZALEA_TREE) {
            BukkitExtraConstants.treeType = TreeType.AZALEA;
        } else if (worldgentreeabstract == TreeFeatures.MANGROVE) {
            BukkitExtraConstants.treeType = TreeType.MANGROVE;
        } else if (worldgentreeabstract == TreeFeatures.TALL_MANGROVE) {
            BukkitExtraConstants.treeType = TreeType.TALL_MANGROVE;
        } else if (worldgentreeabstract == TreeFeatures.CHERRY || worldgentreeabstract == TreeFeatures.CHERRY_BEES_005) {
            BukkitExtraConstants.treeType = TreeType.CHERRY;
        } else {
            BukkitExtraConstants.treeType = TreeType.ACACIA;// Banner - add field to handle modded trees TODO fixme
        }
    }
    // CraftBukkit end
}