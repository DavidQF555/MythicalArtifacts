package io.github.davidqf555.minecraft.mythical_artifacts.client.util;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.client.render.FenrirRenderer;
import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventBusSubscriber {

    @Mod.EventBusSubscriber(modid = MythicalArtifacts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.FENRIR_ENTITY.get(), FenrirRenderer::new);
        }
    }
}
