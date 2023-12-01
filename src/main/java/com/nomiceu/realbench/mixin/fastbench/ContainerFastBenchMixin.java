package com.nomiceu.realbench.mixin.fastbench;

import com.nomiceu.realbench.logic.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.fastbench.gui.ContainerFastBench;
import shadows.fastbench.gui.SlotCraftingSucks;

import java.util.List;

@Mixin(value = ContainerFastBench.class, remap = false)
public class ContainerFastBenchMixin extends ContainerWorkbench implements TileContainerWorkbench {
    /**
     * Mandatory ignored constructor
     */
    public ContainerFastBenchMixin(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
        super(playerInventory, worldIn, posIn);
    }

    @Unique
    public TileEntityWorkbench tile;
    @Unique
    public Slot result;
    @Unique
    public List<ItemStack> oldMatrix;
    @Unique
    private World world;
    @Unique
    private EntityPlayer player;

    @Inject(method = "<init>*", at = @At(value = "RETURN"))
    public void init(EntityPlayer player, World world, BlockPos pos, CallbackInfo ci) {
        tile = ContainerWorkbenchLogic.getTile(this, world, pos);
        this.world = world;
        this.player = player;

        oldMatrix = NonNullList.withSize(9, ItemStack.EMPTY);

        ContainerWorkbenchLogic.init(this, player, (container, playerIn) ->
                result = addSlot(new SlotCraftingSucks((ContainerFastBench) container,
                        playerIn, craftMatrix, craftResult, 0, 124, 35)));
    }

    @Inject(method = "onContainerClosed", at = @At("HEAD"), cancellable = true)
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
    public Slot addSlot(Slot slot) {
        return addSlotToContainer(slot);
    }
}
