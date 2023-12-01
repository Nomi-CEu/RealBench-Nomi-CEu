package com.nomiceu.realbench.mixin.fastbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.fastbench.gui.ContainerFastBench;

/**
 * Everything is handled by ContainerWorkbenchMixin except for onContainerClosed
 */
@Mixin(value = ContainerFastBench.class, remap = false)
public class ContainerFastBenchMixin extends ContainerWorkbench {
    /**
     * Mandatory ignored constructor
     */
    public ContainerFastBenchMixin(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
        super(playerInventory, worldIn, posIn);
    }

    // Only function not handled by ContainerWorkbenchMixin
    // As this is cancelled
    // Must be method reference + remap as this is originally mc function
    @Inject(method = "onContainerClosed(Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At("HEAD"), remap = true, cancellable = true)
    public void onContainerClosed(EntityPlayer player, CallbackInfo ci) {
        super.onContainerClosed(player);
        ci.cancel();
    }
}
