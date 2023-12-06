package com.nomiceu.realbench.logic;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWorkbenchLogic {
    public static void breakBlock(World world, BlockPos pos) {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileEntityWorkbench workbench) {
            for (var stack : workbench.craftMatrix) {
                if (!stack.isEmpty())
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }
}
