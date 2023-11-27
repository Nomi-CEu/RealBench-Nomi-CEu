package com.nomiceu.realbench.core;

import com.nomiceu.realbench.RealBenchValues;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RealBenchValues.MODID)
@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ClientProxy {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // TODO register renderer here
    }
}
