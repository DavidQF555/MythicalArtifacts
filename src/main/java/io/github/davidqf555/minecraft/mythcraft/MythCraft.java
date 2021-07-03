package io.github.davidqf555.minecraft.mythcraft;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("mythcraft")
public class MythCraft {

    public static final String MOD_ID = "mythcraft";
    public static final ItemGroup GROUP = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.GOLDEN_SWORD);
        }
    };

    public MythCraft() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
