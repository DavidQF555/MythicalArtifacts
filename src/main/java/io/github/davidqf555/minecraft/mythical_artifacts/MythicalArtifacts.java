package io.github.davidqf555.minecraft.mythical_artifacts;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("mythical_artifacts")
public class MythicalArtifacts {

    public static final String MOD_ID = "mythical_artifacts";
    public static final ItemGroup GROUP = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.GOLDEN_SWORD);
        }
    };

    public MythicalArtifacts() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
