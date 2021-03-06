package io.github.davidqf555.minecraft.mythical_artifacts;

import io.github.davidqf555.minecraft.mythical_artifacts.common.entities.FenrirEntity;
import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("mythical_artifacts")
public class MythicalArtifacts {

    public static final String MOD_ID = "mythical_artifacts";
    public static final ItemGroup GROUP = new ItemGroup("itemGroup." + MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.GOLDEN_SWORD);
        }
    };

    public MythicalArtifacts() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Deprecated
    private void setup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> GlobalEntityTypeAttributes.put(RegistryHandler.FENRIR_ENTITY.get(), FenrirEntity.setAttributes().create()));
    }
}
