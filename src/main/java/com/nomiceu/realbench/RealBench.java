package com.nomiceu.realbench;

import com.nomiceu.realbench.core.ClientProxy;
import com.nomiceu.realbench.core.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = RealBenchValues.MODID, version = Tags.VERSION, name = RealBenchValues.MODNAME, acceptedMinecraftVersions = "[1.12.2]")
@SuppressWarnings("unused")
public class RealBench {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(ClientProxy.class);
        MinecraftForge.EVENT_BUS.register(CommonProxy.class);
        CommonProxy.preInit();
    }
}
