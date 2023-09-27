package com.mohistmc.banner.mixin.world.level.storage.loot;

import com.mohistmc.banner.injection.world.level.storage.loot.InjectionLootTable;
import io.izzel.arclight.mixin.Eject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.bukkit.craftbukkit.v1_20_R2.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.event.world.LootGenerateEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LootTable.class)
public abstract class MixinLootTable implements InjectionLootTable {

    @Shadow @Final
    static Logger LOGGER;

    @Shadow protected abstract List<Integer> getAvailableSlots(Container inventory, RandomSource random);

    @Shadow protected abstract void shuffleAndSplitItems(ObjectArrayList<ItemStack> stacks, int emptySlotsCount, RandomSource random);
    @Shadow @Final @Nullable Optional<ResourceLocation> randomSequence;

    @Shadow protected abstract ObjectArrayList<ItemStack> getRandomItems(LootContext context);

    @Eject(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private ObjectArrayList<ItemStack> banner$nonPluginEvent(LootTable lootTable, LootContext context, CallbackInfo ci, Container inv) {
        ObjectArrayList<ItemStack> list = this.getRandomItems(context);
        if (!context.hasParam(LootContextParams.ORIGIN) && !context.hasParam(LootContextParams.THIS_ENTITY)) {
            return list;
        }
        LootGenerateEvent event = CraftEventFactory.callLootGenerateEvent(inv, (LootTable) (Object) this, context, list, false);
        if (event.isCancelled()) {
            ci.cancel();
            return null;
        } else {
            return event.getLoot().stream().map(CraftItemStack::asNMSCopy).collect(ObjectArrayList.toList());
        }
    }

    @Override
    public void fillInventory(Container inv, LootParams lootparams, long seed, boolean plugin) {
        LootContext context = (new LootContext.Builder(lootparams)).withOptionalRandomSeed(seed).create(this.randomSequence);
        ObjectArrayList<ItemStack> objectArrayList = this.getRandomItems(context);
        RandomSource randomsource = context.getRandom();
        LootGenerateEvent event = CraftEventFactory.callLootGenerateEvent(inv, (LootTable) (Object) this, context, objectArrayList, plugin);
        if (event.isCancelled()) {
            return;
        }
        objectArrayList = event.getLoot().stream().map(CraftItemStack::asNMSCopy).collect(ObjectArrayList.toList());

        List<Integer> list = this.getAvailableSlots(inv, randomsource);
        this.shuffleAndSplitItems(objectArrayList, list.size(), randomsource);

        for (ItemStack itemstack : objectArrayList) {
            if (list.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }

            if (itemstack.isEmpty()) {
                inv.setItem(list.remove(list.size() - 1), ItemStack.EMPTY);
            } else {
                inv.setItem(list.remove(list.size() - 1), itemstack);
            }
        }
    }
}
