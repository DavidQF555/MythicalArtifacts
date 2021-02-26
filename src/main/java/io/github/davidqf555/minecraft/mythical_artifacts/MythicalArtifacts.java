package io.github.davidqf555.minecraft.mythical_artifacts;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("mythical_artifacts")
public class MythicalArtifacts {

    public static final String MOD_ID = "mythical_artifacts";

    public MythicalArtifacts() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
