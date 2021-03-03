package io.github.davidqf555.minecraft.mythical_artifacts.common.util;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.items.ConquestCrown;
import io.github.davidqf555.minecraft.mythical_artifacts.common.items.DeathScythe;
import io.github.davidqf555.minecraft.mythical_artifacts.common.items.FamineScales;
import io.github.davidqf555.minecraft.mythical_artifacts.common.items.WarSword;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MythicalArtifacts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHandler {

    public static final Rarity ARTIFACT_RARITY = Rarity.create(MythicalArtifacts.MOD_ID + ":artifact_rarity", TextFormatting.RED);
    public static final Style LORE_STYLE = Style.EMPTY.setFormatting(TextFormatting.DARK_PURPLE).setItalic(true);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ConquestCrown().setRegistryName(MythicalArtifacts.MOD_ID, "conquest_crown"),
                new DeathScythe(4, -3).setRegistryName(MythicalArtifacts.MOD_ID, "death_scythe"),
                new FamineScales().setRegistryName(MythicalArtifacts.MOD_ID, "famine_scales"),
                new WarSword(-1, -2.6f).setRegistryName(MythicalArtifacts.MOD_ID, "war_sword")
        );
    }
}
