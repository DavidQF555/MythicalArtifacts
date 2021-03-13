package io.github.davidqf555.minecraft.mythical_artifacts.common.items;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class PandoraBoxItem extends Item {

    private static final int COOLDOWN = 6000;
    private static final int LEVEL = 7;
    private static final int RADIUS = 16;
    private static final int DURATION = 160;
    private static final float ROTATE_SPEED = 3;

    public PandoraBoxItem() {
        super(new Item.Properties()
                .maxStackSize(1)
                .rarity(RegistryHandler.ARTIFACT_RARITY)
                .group(MythicalArtifacts.GROUP)
        );
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        CompoundNBT tag = stack.getOrCreateChildTag(MythicalArtifacts.MOD_ID);
        int ticksLeft = tag.contains("Ticks", Constants.NBT.TAG_INT) ? tag.getInt("Ticks") : 0;
        if (ticksLeft > 0) {
            float percent = (float) ((DURATION - ticksLeft) * 1.0 / DURATION);
            float angle = (float) Math.PI * 2 * ROTATE_SPEED * percent + entityIn.rotationYaw * (float) Math.PI / 180;
            float dY = 1 + percent;
            for (int i = 0; i < 4; i++) {
                float sin = MathHelper.sin(angle) / (1 + percent);
                float cos = MathHelper.cos(angle) / (1 + percent);
                worldIn.addParticle(ParticleTypes.LARGE_SMOKE, entityIn.getPosX(), entityIn.getPosYEye(), entityIn.getPosZ(), sin, dY, cos);
                if (worldIn instanceof ServerWorld) {
                    ((ServerWorld) worldIn).getServer().getPlayerList().sendToAllNearExcept(entityIn instanceof PlayerEntity ? (PlayerEntity) entityIn : null, entityIn.getPosX(), entityIn.getPosYEye(), entityIn.getPosZ(), 64, worldIn.getDimensionKey(), new SSpawnParticlePacket(ParticleTypes.LARGE_SMOKE, false, entityIn.getPosX(), entityIn.getPosYEye(), entityIn.getPosZ(), sin, dY, cos, 1, 0));
                }
                angle += Math.PI / 2;
            }
            tag.putInt("Ticks", ticksLeft - 1);
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack hand = playerIn.getHeldItem(handIn);
        if (worldIn instanceof ServerWorld && !playerIn.getCooldownTracker().hasCooldown(this)) {
            worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosYEye(), playerIn.getPosZ(), SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 3, worldIn.rand.nextFloat() * 2);
            applyEffect((ServerWorld) worldIn, playerIn);
            hand.getOrCreateChildTag(MythicalArtifacts.MOD_ID).putInt("Ticks", DURATION);
            playerIn.getCooldownTracker().setCooldown(this, COOLDOWN);
            return ActionResult.resultSuccess(hand);
        }
        return ActionResult.resultPass(hand);
    }

    private void applyEffect(ServerWorld world, Entity user) {
        for (int y = -RADIUS; y <= RADIUS; y++) {
            double xRadius = Math.sqrt(RADIUS * RADIUS - y * y);
            int xRounded = (int) xRadius;
            for (int x = -xRounded; x <= xRounded; x++) {
                int zRounded = (int) Math.sqrt(xRadius * xRadius - x * x);
                for (int z = -zRounded; z <= zRounded; z++) {
                    BlockPos pos = user.getPosition().add(x, y, z);
                    List<BlockState> states = getRelatedStates(world, pos, world.getBlockState(pos));
                    world.setBlockState(pos, states.get(world.getRandom().nextInt(states.size())));
                }
            }
        }
        List<Effect> harmful = ForgeRegistries.POTIONS.getValues().stream().filter(effect -> effect.getEffectType() == EffectType.HARMFUL).collect(Collectors.toList());
        AxisAlignedBB bounds = new AxisAlignedBB(user.getPositionVec().subtract(RADIUS, RADIUS, RADIUS), user.getPositionVec().add(RADIUS, RADIUS, RADIUS));
        for (LivingEntity entity : world.getEntitiesWithinAABB(LivingEntity.class, bounds, entity -> entity.getDistanceSq(user) <= RADIUS * RADIUS)) {
            Map<Effect, Integer> level = new HashMap<>();
            for (int i = 0; i < LEVEL; i++) {
                Effect effect = harmful.get(world.getRandom().nextInt(harmful.size()));
                level.put(effect, level.getOrDefault(effect, 0) + 1);
            }
            for (Effect effect : level.keySet()) {
                entity.addPotionEffect(new EffectInstance(effect, effect.isInstant() ? 1 : DURATION, level.get(effect) - 1, false, true, true));
            }
        }
    }

    private List<BlockState> getRelatedStates(ServerWorld world, BlockPos pos, BlockState state) {
        List<BlockState> states = new ArrayList<>(state.getBlock().getStateContainer().getValidStates());
        Block.getDrops(state, world, pos, null).stream().map(ItemStack::getItem).filter(item -> item instanceof BlockItem).map(item -> ((BlockItem) item).getBlock()).forEach(block -> states.addAll(block.getStateContainer().getValidStates()));
        return states;
    }
}
