package io.github.davidqf555.minecraft.mythical_artifacts.common.items;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;

;

public class ConquestCrown extends ArmorItem {

    private static final int LEVEL = 3;

    public ConquestCrown() {
        super(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD, new Item.Properties()
                .rarity(RegistryHandler.ARTIFACT_RARITY)
                .group(MythicalArtifacts.GROUP)
        );
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return MythicalArtifacts.MOD_ID + ":textures/models/armor/conquest_crown_layer_1.png";
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        EffectInstance effect = player.getActivePotionEffect(Effects.HERO_OF_THE_VILLAGE);
        if (effect == null || effect.getAmplifier() < LEVEL - 1) {
            player.addPotionEffect(new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 1, LEVEL - 1, true, false, false));
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }
}
