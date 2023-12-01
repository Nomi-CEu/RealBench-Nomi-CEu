package com.nomiceu.realbench.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotCraftingResult extends SlotCrafting {
    private final ContainerWorkbench container;
    public SlotCraftingResult(EntityPlayer player, ContainerWorkbench container, InventoryCrafting craftingInventory, InventoryCraftResult craftResult, int slotIndex, int xPosition, int yPosition) {
        super(player, craftingInventory, craftResult, slotIndex, xPosition, yPosition);
        this.container = container;
    }

    @Override
    public @NotNull ItemStack onTake(@NotNull EntityPlayer player, @NotNull ItemStack stack) {
        var value = super.onTake(player, stack);
        var tile = ((TileContainerWorkbench) container).getTile();
        tile.craft();
        return value;
    }
}
