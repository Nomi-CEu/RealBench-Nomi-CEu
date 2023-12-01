package com.nomiceu.realbench.mixin.fastbench;

import com.nomiceu.realbench.logic.TileContainerWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadows.fastbench.gui.ContainerFastBench;
import shadows.fastbench.gui.SlotCraftingSucks;

@Mixin(value = SlotCraftingSucks.class, remap = false)
public class SlotCraftingSucksMixin {
    @Unique
    private ContainerFastBench container;
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(ContainerFastBench container, EntityPlayer player, InventoryCrafting inv, InventoryCraftResult holder, int slotIndex, int xPosition, int yPosition, CallbackInfo ci) {
        this.container = container;
    }

    // Must be method reference + remap as this is overriding mc function
    @Inject(method = "onTake(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"), remap = true)
    public void onTake(EntityPlayer player, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        var tile = ((TileContainerWorkbench) container).getTile();
        tile.craft();
    }
}
