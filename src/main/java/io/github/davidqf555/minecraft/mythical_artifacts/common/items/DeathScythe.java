package io.github.davidqf555.minecraft.mythical_artifacts.common.items;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import java.util.function.Consumer;

public class DeathScythe extends SwordItem {

    private static final int DURATION = 100;
    private static final int LEVEL = 1;

    public DeathScythe(int damage, float speed) {
        super(ItemTier.NETHERITE, damage, speed, new Item.Properties()
                .rarity(RegistryHandler.ARTIFACT_RARITY)
                .group(MythicalArtifacts.GROUP)
        );
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hitEntity(stack, target, attacker);
        target.addPotionEffect(new EffectInstance(Effects.WITHER, DURATION, LEVEL - 1, false, true, true));
        attacker.heal(getAttackDamage() / 2);
        return true;
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
