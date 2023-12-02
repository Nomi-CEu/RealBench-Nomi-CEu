package com.nomiceu.realbench.mixin;

import com.nomiceu.realbench.logic.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ContainerWorkbench.class)
public abstract class ContainerWorkbenchMixin extends Container implements TileContainerWorkbench {
    @Shadow
    public InventoryCrafting craftMatrix;
    @Shadow
    public InventoryCraftResult craftResult;
    @Shadow @Final
    private World world;
    @Shadow @Final
    private EntityPlayer player;
    @Unique
    public TileEntityWorkbench tile;
    @Unique
    public List<ItemStack> oldMatrix;

    @Inject(method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "RETURN"))
    public void init(InventoryPlayer playerInventory, World worldIn, BlockPos posIn, CallbackInfo ci) {
        tile = ContainerWorkbenchLogic.getTile((ContainerWorkbench) (Object) this, worldIn, posIn);

        oldMatrix = NonNullList.withSize(9, ItemStack.EMPTY);

        ContainerWorkbenchLogic.init((ContainerWorkbench) (Object) this, playerInventory.player, (container, playerIn) ->
                addSlotToContainer(new SlotCraftingResult(playerIn,
                        container, craftMatrix, craftResult, 0, 124, 35)));
    }

    @Inject(method = "onContainerClosed(Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At("HEAD"), cancellable = true)
    public void onContainerClosed(EntityPlayer player, CallbackInfo ci) {
        super.onContainerClosed(player);
        ci.cancel();
    }

    @Unique
    @Override
    @NotNull
    public TileEntityWorkbench getTile() {
        return tile;
    }

    @Unique
    @Override
    public void updateResult() {
        slotChangedCraftingGrid(world, player, craftMatrix, craftResult);
        detectAndSendChanges();
    }

    @Unique
    @Override
    public List<ItemStack> getOldMatrix() {
        return oldMatrix;
    }

    public void updateOldMatrix(List<ItemStack> matrix) {
        oldMatrix = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < matrix.size(); i++) {
            oldMatrix.set(i, matrix.get(i).copy());
        }
    }

    @Override
    @Unique
    public void clearResult() {
        craftResult.clear();
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(this.windowId, 0, ItemStack.EMPTY));
    }

    @Override
    @Unique
    public void addSlot(Slot slot) {
        addSlotToContainer(slot);
    }
}
