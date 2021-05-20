package io.github.davidqf555.minecraft.mythical_artifacts.common.util;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.blocks.GjollBlock;
import io.github.davidqf555.minecraft.mythical_artifacts.common.entities.FenrirEntity;
import io.github.davidqf555.minecraft.mythical_artifacts.common.items.*;
import io.github.davidqf555.minecraft.mythical_artifacts.common.world.gen.GjollFeature;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = MythicalArtifacts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHandler {

    public static final Rarity ARTIFACT_RARITY = Rarity.create(MythicalArtifacts.MOD_ID + ":artifact_rarity", TextFormatting.RED);
    public static final Style LORE_STYLE = Style.EMPTY.setFormatting(TextFormatting.DARK_PURPLE).setItalic(true);

    public static final RegistryObject<ConquestCrownItem> CONQUEST_CROWN_ITEM = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "conquest_crown_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<DeathScytheItem> DEATH_SCYTHE_ITEM = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "death_scythe_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<FamineScalesItem> FAMINE_SCALES_ITEM = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "famine_scales_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<WarSwordItem> WAR_SWORD_ITEM = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "war_sword_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<GjollItem> GJOLL_ITEM = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "gjoll_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<PandoraBoxItem> PANDORA_BOX_ITEM = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "pandora_box_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<BlockItem> GJOLL_BLOCK_ITEM = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "gjoll_block_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ContainmentBoxItem> CONTAINMENT_BOX_ITEM = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "containment_box_item"), ForgeRegistries.ITEMS);

    public static final RegistryObject<EntityType<FenrirEntity>> FENRIR_ENTITY = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "fenrir_entity"), ForgeRegistries.ENTITIES);

    public static final RegistryObject<GjollBlock> GJOLL_BLOCK = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "gjoll_block"), ForgeRegistries.BLOCKS);

    public static final RegistryObject<GjollFeature> GJOLL_FEATURE = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "gjoll_feature"), ForgeRegistries.FEATURES);

    public static final RegistryObject<GlobalLootModifierSerializer<HorsemenLootModifier>> HORSEMEN_LOOT_MODIFIER_SERIALIZER = RegistryObject.of(new ResourceLocation(MythicalArtifacts.MOD_ID, "horsemen_loot_modifier"), ForgeRegistries.LOOT_MODIFIER_SERIALIZERS);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ConquestCrownItem().setRegistryName(MythicalArtifacts.MOD_ID, "conquest_crown_item"),
                new DeathScytheItem(4, -3).setRegistryName(MythicalArtifacts.MOD_ID, "death_scythe_item"),
                new FamineScalesItem().setRegistryName(MythicalArtifacts.MOD_ID, "famine_scales_item"),
                new WarSwordItem(-1, -2.6f).setRegistryName(MythicalArtifacts.MOD_ID, "war_sword_item"),
                new GjollItem().setRegistryName(MythicalArtifacts.MOD_ID, "gjoll_item"),
                new PandoraBoxItem().setRegistryName(MythicalArtifacts.MOD_ID, "pandora_box_item"),
                new ContainmentBoxItem().setRegistryName(MythicalArtifacts.MOD_ID, "containment_box_item"),
                new BlockItem(RegistryHandler.GJOLL_BLOCK.get(), new Item.Properties().group(MythicalArtifacts.GROUP)).setRegistryName(MythicalArtifacts.MOD_ID, "gjoll_block_item")
        );
    }

    @SubscribeEvent
    public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                EntityType.Builder.create((type, world) -> new FenrirEntity(world), EntityClassification.CREATURE).size(0.6f, 0.85f).build(new ResourceLocation(MythicalArtifacts.MOD_ID, "fenrir_entity").toString()).setRegistryName(MythicalArtifacts.MOD_ID, "fenrir_entity")
        );
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new GjollBlock().setRegistryName(MythicalArtifacts.MOD_ID, "gjoll_block")
        );
    }

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().registerAll(
                new GjollFeature().setRegistryName(MythicalArtifacts.MOD_ID, "gjoll_feature")
        );
    }

    @SubscribeEvent
    public static void registerGlobalLootModifierSerializers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().registerAll(
                new HorsemenLootModifier.Serializer().setRegistryName(MythicalArtifacts.MOD_ID, "horsemen_loot_modifier")
        );
    }
}
