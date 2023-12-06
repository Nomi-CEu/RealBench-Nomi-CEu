package com.nomiceu.realbench.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class ContainerWorkbenchLogic {
    public static void init(ContainerWorkbench container, EntityPlayer player, BiConsumer<ContainerWorkbench, EntityPlayer> addResultSlot) {
        // Clear all buttons and restart
        container.craftMatrix = new InventoryWorkbench(container, 3, 3);
        container.inventorySlots = NonNullList.create();
        container.inventoryItemStacks = NonNullList.create();

        var tileContainer = (TileContainerWorkbench) container;
        addResultSlot.accept(container, player);

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                tileContainer.addSlot(new Slot(container.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                tileContainer.addSlot(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            tileContainer.addSlot(new Slot(player.inventory, l, 8 + l * 18, 142));
        }
    }

    public static TileEntityWorkbench getTile(ContainerWorkbench container, World world, BlockPos pos) {
        TileEntity foundTile = world.getTileEntity(pos);
        if (foundTile instanceof TileEntityWorkbench workbench) {
            return workbench.addContainer(container);
        }
        return null;
    }
}
