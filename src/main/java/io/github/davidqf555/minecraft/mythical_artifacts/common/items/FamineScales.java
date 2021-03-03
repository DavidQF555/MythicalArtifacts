package io.github.davidqf555.minecraft.mythical_artifacts.common.items;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class FamineScales extends Item {

    private static final TranslationTextComponent LORE = new TranslationTextComponent("item." + MythicalArtifacts.MOD_ID + ".famine_scales.lore");
    private static final int COOLDOWN = 1000;
    private static final int DURATION = 100;
    private static final int LEVEL = 1;
    private static final int RANGE = 5;

    public FamineScales() {
        super(new Item.Properties()
                .maxStackSize(1)
                .rarity(RegistryHandler.ARTIFACT_RARITY)
                .group(MythicalArtifacts.GROUP)
        );
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(LORE.mergeStyle(RegistryHandler.LORE_STYLE));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);
        applyEffect(playerIn);
        playerIn.getCooldownTracker().setCooldown(this, COOLDOWN);
        return ActionResult.resultSuccess(item);
    }

    private void applyEffect(PlayerEntity player) {
        List<LivingEntity> entities = player.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(player.getPositionVec().subtract(RANGE, RANGE, RANGE), player.getPositionVec().add(RANGE, RANGE, RANGE)), EntityPredicates.NOT_SPECTATING.and(EntityPredicates.IS_ALIVE).and(entity -> entity instanceof LivingEntity && entity.getDistanceSq(player) <= RANGE * RANGE));
        float avg = 0;
        for (LivingEntity entity : entities) {
            if (entity instanceof PlayerEntity) {
                FoodStats tar = ((PlayerEntity) entity).getFoodStats();
                avg += tar.getFoodLevel();
                tar.setFoodSaturationLevel(0);
            } else {
                avg += 20 * entity.getHealth() / entity.getMaxHealth();
            }
        }
        avg /= entities.size();
        int players = 0;
        for (LivingEntity entity : entities) {
            if (entity instanceof PlayerEntity) {
                FoodStats tar = ((PlayerEntity) entity).getFoodStats();
                tar.setFoodLevel((int) avg);
                if (!entity.equals(player)) {
                    entity.addPotionEffect(new EffectInstance(Effects.HUNGER, DURATION, LEVEL - 1, false, true, true));
                    players++;
                }
            } else {
                entity.setHealth(entity.getMaxHealth() * avg / 20);
            }
        }
        player.addPotionEffect(new EffectInstance(Effects.SATURATION, DURATION * players, LEVEL - 1, false, true, true));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }
}
