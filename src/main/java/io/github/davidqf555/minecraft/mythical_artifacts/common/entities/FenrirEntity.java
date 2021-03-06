package io.github.davidqf555.minecraft.mythical_artifacts.common.entities;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FenrirEntity extends WolfEntity {

    private static final int RESPAWN = 1200;
    private static final int PARTICLES = 4;

    public FenrirEntity(World worldIn) {
        super(RegistryHandler.FENRIR_ENTITY.get(), worldIn);
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return WolfEntity.func_234233_eS_()
                .createMutableAttribute(Attributes.ARMOR, 10)
                .createMutableAttribute(Attributes.MAX_HEALTH, 20)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 8)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.4);
    }

    @Override
    public void livingTick() {
        if (world instanceof ServerWorld) {
            LivingEntity owner = getOwner();
            if (owner == null || getGjoll(owner).isEmpty()) {
                remove();
            }
        } else {
            for (int i = 0; i < PARTICLES; i++) {
                world.addParticle(ParticleTypes.SMOKE, getPosXRandom(1), getPosYRandom(), getPosZRandom(1), 0, 0, 0);
                world.addParticle(ParticleTypes.FLAME, getPosXRandom(1), getPosYRandom(), getPosZRandom(1), 0, 0, 0);
            }
        }
        super.livingTick();
    }

    private ItemStack getGjoll(LivingEntity entity) {
        IItemHandler inventory = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack item = inventory.getStackInSlot(i);
            CompoundNBT tag = item.getOrCreateChildTag(MythicalArtifacts.MOD_ID);
            UUID id = tag.contains("Fenrir", Constants.NBT.TAG_INT_ARRAY) ? tag.getUniqueId("Fenrir") : null;
            if (!item.isEmpty() && getUniqueID().equals(id)) {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void applyEnchantments(LivingEntity entityLivingBaseIn, Entity entityIn) {
        entityIn.setFire(140);
        super.applyEnchantments(entityLivingBaseIn, entityIn);
    }

    @Override
    public void setTamed(boolean tamed) {
        byte data = dataManager.get(TAMED);
        dataManager.set(TAMED, (byte) (tamed ? data | 4 : data & -5));
        setupTamedAI();
    }

    @Override
    public void onDeath(DamageSource cause) {
        LivingEntity owner = getOwner();
        if (owner instanceof PlayerEntity) {
            ((PlayerEntity) owner).getCooldownTracker().setCooldown(RegistryHandler.GJOLL_ITEM.get(), RESPAWN);
        }
        super.onDeath(cause);
    }

    @Override
    public int getExperiencePoints(PlayerEntity player) {
        return 0;
    }

    @Override
    public DyeColor getCollarColor() {
        return DyeColor.BLACK;
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();
        if (item.isFood() && item.getFood().isMeat() && this.getHealth() < this.getMaxHealth()) {
            if (!player.abilities.isCreativeMode) {
                stack.shrink(1);
            }
            this.heal((float) item.getFood().getHealing());
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public boolean isSitting() {
        return false;
    }

    @Override
    public float getRenderScale() {
        return 2;
    }
}
