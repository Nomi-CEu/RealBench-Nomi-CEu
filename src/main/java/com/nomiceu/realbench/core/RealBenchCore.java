package com.nomiceu.realbench.core;

import com.google.common.collect.ImmutableList;
import com.nomiceu.realbench.RealBenchValues;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.Name("RealBench-Core")
public class RealBenchCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
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

        if (!Loader.isModLoaded(parts[2])) {
            LogManager.getLogger(RealBenchValues.MODID).info("Mod '" + parts[2] + "' is not loaded. Disabling Mixins...");
            return false;
        }

        return true;
    }

    @Override
    public List<String> getMixinConfigs() {
        return ImmutableList.of("mixins.realbench.json", "mixins.realbench.fastbench.json");
    }
}