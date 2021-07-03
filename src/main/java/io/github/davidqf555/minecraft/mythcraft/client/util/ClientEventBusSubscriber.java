package io.github.davidqf555.minecraft.mythcraft.client.util;

import io.github.davidqf555.minecraft.mythcraft.MythCraft;
import io.github.davidqf555.minecraft.mythcraft.client.render.FenrirRenderer;
import io.github.davidqf555.minecraft.mythcraft.common.util.RegistryHandler;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventBusSubscriber {

    @Mod.EventBusSubscriber(modid = MythCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.FENRIR_ENTITY.get(), FenrirRenderer::new);
            ItemModelsProperties.registerProperty(RegistryHandler.PANDORA_BOX_ITEM.get(), new ResourceLocation(MythCraft.MOD_ID, "opened"), (stack, world, entity) -> {
                CompoundNBT tag = stack.getOrCreateChildTag(MythCraft.MOD_ID);
                return tag.contains("Ticks", Constants.NBT.TAG_INT) && tag.getInt("Ticks") > 0 ? 1 : 0;
            });
        }
    }
}
