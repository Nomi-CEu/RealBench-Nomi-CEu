package com.nomiceu.realbench.mixin;

import com.nomiceu.realbench.logic.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
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
    public Slot result;
    @Unique
    public List<ItemStack> oldMatrix;

    @Inject(method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "RETURN"))
    public void init(InventoryPlayer playerInventory, World worldIn, BlockPos posIn, CallbackInfo ci) {
        TileEntity foundTile = worldIn.getTileEntity(posIn);
        if (foundTile instanceof TileEntityWorkbench workbench) {
            tile = workbench;
            tile.addContainer((ContainerWorkbench) (Object) this);
        }
        else {
            tile = new TileEntityWorkbench().addContainer((ContainerWorkbench) (Object) this);
            worldIn.setTileEntity(posIn, tile);
        }

        // Clear all buttons and restart
        oldMatrix = NonNullList.withSize(9, ItemStack.EMPTY);
        craftMatrix = new InventoryWorkbench((ContainerWorkbench) (Object) this,3, 3);
        inventorySlots = NonNullList.create();
        inventoryItemStacks = NonNullList.create();

        result = addSlotToContainer(new SlotCraftingResult(playerInventory.player, (ContainerWorkbench) (Object) this, craftMatrix, craftResult, 0, 124, 35));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                addSlotToContainer(new Slot(craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
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
}
