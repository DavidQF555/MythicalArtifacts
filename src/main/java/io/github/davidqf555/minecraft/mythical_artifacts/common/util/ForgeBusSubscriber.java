package io.github.davidqf555.minecraft.mythical_artifacts.common.util;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.items.WarSword;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MythicalArtifacts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusSubscriber {

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
}
