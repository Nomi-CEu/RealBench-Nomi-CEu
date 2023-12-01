package com.nomiceu.realbench.core;

import com.google.common.collect.ImmutableList;
import com.nomiceu.realbench.RealBenchValues;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class RealBenchLateMixin implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return ImmutableList.of("mixins.realbench.fastbench.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        String[] parts = mixinConfig.split("\\.");

        if (parts.length != 4)
            return true;

        if (!Objects.equals(parts[1], RealBenchValues.MODID)) {
            LogManager.getLogger(RealBenchValues.MODID).error("Non RealBench Mixin Found in Mixin Queue. This is probably an error. Skipping...");
            LogManager.getLogger(RealBenchValues.MODID).error("Mixin Config: " + mixinConfig);
            return true;
        }

        LogManager.getLogger().info(parts[2]);
        if (!Loader.isModLoaded(parts[2])) {
            LogManager.getLogger(RealBenchValues.MODID).info("Mod '" + parts[2] + "' is not loaded. Disabling Mixins...");
            return false;
        }

        return true;
    }
}
