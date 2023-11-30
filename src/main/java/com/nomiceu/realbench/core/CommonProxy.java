package com.nomiceu.realbench.core;

import com.nomiceu.realbench.RealBenchValues;
import com.nomiceu.realbench.logic.TileEntityWorkbench;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = RealBenchValues.MODID)
@SuppressWarnings("unused")
public class CommonProxy {
    public static void preInit() {
        GameRegistry.registerTileEntity(TileEntityWorkbench.class, new ResourceLocation(RealBenchValues.MODID, "workbench"));
    }
}
