package io.github.davidqf555.minecraft.mythcraft.common.util;

import io.github.davidqf555.minecraft.mythcraft.MythCraft;
import io.github.davidqf555.minecraft.mythcraft.common.entities.FenrirEntity;
import io.github.davidqf555.minecraft.mythcraft.common.items.ArtifactType;
import io.github.davidqf555.minecraft.mythcraft.common.items.ContainmentBoxItem;
import io.github.davidqf555.minecraft.mythcraft.common.items.WarSwordItem;
import io.github.davidqf555.minecraft.mythcraft.common.world.ArtifactData;
import io.github.davidqf555.minecraft.mythcraft.common.world.gen.GjollFeature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

public class EventBusSubscriber {

    @Mod.EventBusSubscriber(modid = MythCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingDeath(LivingDeathEvent event) {
            Entity source = event.getSource().getTrueSource();
            if (source instanceof LivingEntity && !event.isCanceled()) {
                ItemStack sword = ((LivingEntity) source).getHeldItem(((LivingEntity) source).getActiveHand());
                if (sword.getItem() instanceof WarSwordItem) {
                    CompoundNBT tag = sword.getOrCreateChildTag(MythCraft.MOD_ID);
                    int init = tag.contains("Kills", Constants.NBT.TAG_INT) ? tag.getInt("Kills") : 0;
                    tag.putInt("Kills", init + 1);
                }
                LivingEntity entity = event.getEntityLiving();
                if (entity instanceof MonsterEntity) {
                    IItemHandler inventory = source.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
                    for (int i = 0; i < inventory.getSlots(); i++) {
                        ItemStack stack = inventory.getStackInSlot(i);
                        if (stack.getItem() instanceof ContainmentBoxItem) {
                            CompoundNBT tag = stack.getOrCreateChildTag(MythCraft.MOD_ID);
                            int init = tag.contains("Filled", Constants.NBT.TAG_INT) ? tag.getInt("Filled") : 0;
                            tag.putInt("Filled", init + ContainmentBoxItem.getFillAmount(entity));
                            break;
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.world instanceof ServerWorld && event.phase == TickEvent.Phase.START) {
                ArtifactData.get((ServerWorld) event.world).tick((ServerWorld) event.world);
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.player.world instanceof ServerWorld && !event.isCanceled()) {
                UUID id = event.player.getUniqueID();
                Set<ArtifactType> messages = EnumSet.noneOf(ArtifactType.class);
                Map<ArtifactType, Integer> spaces = new EnumMap<>(ArtifactType.class);
                Map<ArtifactType, Integer> empty = new EnumMap<>(ArtifactType.class);
                ArtifactData data = ArtifactData.get((ServerWorld) event.player.world);
                IItemHandler inventory = event.player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        ArtifactType artifact = null;
                        for (ArtifactType type : ArtifactType.values()) {
                            if (type.getItem().equals(stack.getItem())) {
                                artifact = type;
                                break;
                            }
                        }
                        if (artifact != null) {
                            int count = stack.getCount();
                            if (!spaces.containsKey(artifact)) {
                                int amount = 0;
                                for (UUID owner : data.getOwners(artifact)) {
                                    if (owner == null || id.equals(owner)) {
                                        amount++;
                                    }
                                }
                                empty.put(artifact, amount);
                                spaces.put(artifact, amount);
                            }
                            int spacesLeft = spaces.get(artifact);
                            if (spacesLeft < count) {
                                ItemStack drop = stack.split(count - spacesLeft);
                                event.player.dropItem(drop, false, false);
                                messages.add(artifact);
                                spaces.put(artifact, 0);
                            } else {
                                spaces.put(artifact, spacesLeft - count);
                            }
                        }
                    }
                }
                for (ArtifactType artifact : spaces.keySet()) {
                    UUID[] owners = data.getOwners(artifact);
                    int amount = empty.get(artifact) - spaces.get(artifact);
                    for (UUID owner : owners) {
                        if (id.equals(owner)) {
                            amount--;
                        }
                    }
                    for (int i = 0; i < owners.length && amount > 0; i++) {
                        if (owners[i] == null) {
                            owners[i] = id;
                            amount--;
                        }
                    }
                }
                for (ArtifactType artifact : messages) {
                    event.player.sendMessage(new TranslationTextComponent(ArtifactType.MAXED_KEY, new StringTextComponent("[").append(artifact.getItem().getName()).appendString("]").mergeStyle(TextFormatting.RED), artifact.getMaxAmount()).mergeStyle(TextFormatting.DARK_RED), Util.DUMMY_UUID);
                }
            }
        }

        @SubscribeEvent
        public static void onBiomeLoading(BiomeLoadingEvent event) {
            if (event.getCategory() == Biome.Category.ICY && event.getCategory() != Biome.Category.OCEAN && event.getCategory() != Biome.Category.RIVER) {
                event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_DECORATION).add(GjollFeature.FEATURE);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MythCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {

        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(RegistryHandler.FENRIR_ENTITY.get(), FenrirEntity.setAttributes().create());
        }
    }
}
