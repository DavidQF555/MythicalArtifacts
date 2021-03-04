package io.github.davidqf555.minecraft.mythical_artifacts.common.util;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.items.ArtifactType;
import io.github.davidqf555.minecraft.mythical_artifacts.common.items.WarSword;
import io.github.davidqf555.minecraft.mythical_artifacts.common.world.ArtifactData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MythicalArtifacts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusSubscriber {

    private static final String ARTIFACT_MAXED_KEY = "message." + MythicalArtifacts.MOD_ID + ".artifact_maxed";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity source = event.getSource().getTrueSource();
        if (source instanceof LivingEntity && !event.isCanceled()) {
            ItemStack sword = ((LivingEntity) source).getHeldItem(((LivingEntity) source).getActiveHand());
            if (sword.getItem() instanceof WarSword) {
                CompoundNBT tag = sword.getOrCreateChildTag(MythicalArtifacts.MOD_ID);
                int init = tag.contains("Kills", Constants.NBT.TAG_INT) ? tag.getInt("Kills") : 0;
                tag.putInt("Kills", init + 1);
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
            Map<ArtifactType, Integer> total = new EnumMap<>(ArtifactType.class);
            for (int i = event.player.inventory.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack stack;
                if (i < event.player.inventory.mainInventory.size()) {
                    stack = event.player.inventory.mainInventory.get(i);
                } else if (i - event.player.inventory.mainInventory.size() < event.player.inventory.armorInventory.size()) {
                    stack = event.player.inventory.armorItemInSlot(i - event.player.inventory.mainInventory.size());
                } else if (i - event.player.inventory.mainInventory.size() - event.player.inventory.armorInventory.size() < event.player.inventory.offHandInventory.size()) {
                    stack = event.player.inventory.offHandInventory.get(i - event.player.inventory.mainInventory.size() - event.player.inventory.armorInventory.size());
                } else {
                    stack = ItemStack.EMPTY;
                }
                Item item = stack.getItem();
                ArtifactType artifact = null;
                for (ArtifactType type : ArtifactType.values()) {
                    if (type.getItem().equals(item)) {
                        artifact = type;
                        break;
                    }
                }
                if (artifact != null) {
                    UUID[] owners = ArtifactData.get((ServerWorld) event.player.world).getOwners(artifact);
                    int count = 0;
                    for (UUID owner : owners) {
                        if (event.player.getUniqueID().equals(owner)) {
                            count++;
                        }
                    }
                    int amount = total.getOrDefault(artifact, 0);
                    if (amount < count) {
                        total.put(artifact, amount + 1);
                    } else {
                        int space = -1;
                        for (int j = 0; j < owners.length; j++) {
                            if (owners[j] == null) {
                                space = j;
                                break;
                            }
                        }
                        if (space == -1) {
                            event.player.inventory.removeStackFromSlot(i);
                            event.player.dropItem(stack, false);
                            event.player.sendMessage(new TranslationTextComponent(ARTIFACT_MAXED_KEY, new StringTextComponent("[").append(artifact.getItem().getName()).appendString("]").mergeStyle(TextFormatting.RED), artifact.getMaxAmount()).mergeStyle(TextFormatting.DARK_RED), Util.DUMMY_UUID);
                        } else {
                            owners[space] = event.player.getUniqueID();
                            total.put(artifact, amount + 1);
                        }
                    }
                }
            }
        }
    }
}
