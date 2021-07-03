package io.github.davidqf555.minecraft.mythcraft.common.items;

import io.github.davidqf555.minecraft.mythcraft.MythCraft;
import io.github.davidqf555.minecraft.mythcraft.common.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.*;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class PandoraBoxItem extends Item {

    private static final int COOLDOWN = 6000;
    private static final int LEVEL = 3;
    private static final int RADIUS = 16;
    private static final int DURATION = 160;
    private static final float ROTATE_SPEED = 3;
    private static final IFormattableTextComponent LORE = new TranslationTextComponent("item." + MythCraft.MOD_ID + ".pandora_box_item.lore");
    private static final List<EntityType<?>> MONSTERS = ForgeRegistries.ENTITIES.getValues().stream().filter(type -> type.getClassification() == EntityClassification.MONSTER).collect(Collectors.toList());
    private static final List<Effect> HARMFUL = ForgeRegistries.POTIONS.getValues().stream().filter(effect -> effect.getEffectType() == EffectType.HARMFUL).collect(Collectors.toList());


    public PandoraBoxItem() {
        super(new Item.Properties()
                .maxStackSize(1)
                .rarity(RegistryHandler.ARTIFACT_RARITY)
                .group(MythCraft.GROUP)
        );
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(LORE.mergeStyle(RegistryHandler.LORE_STYLE));
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        CompoundNBT tag = stack.getOrCreateChildTag(MythCraft.MOD_ID);
        int ticksLeft = tag.contains("Ticks", Constants.NBT.TAG_INT) ? tag.getInt("Ticks") : 0;
        if (ticksLeft > 0) {
            float percent = (float) ((DURATION - ticksLeft) * 1.0 / DURATION);
            float angle = (float) Math.PI * 2 * ROTATE_SPEED * percent + entityIn.rotationYaw * (float) Math.PI / 180;
            if (ticksLeft % 10 == 0) {
                throwRandomMonster(entityIn, angle);
                if (ticksLeft % 20 == 0) {
                    applyEffect(entityIn);
                }
            }
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

    private void throwRandomMonster(Entity user, float angle) {
        World world = user.getEntityWorld();
        if (world instanceof ServerWorld) {
            List<MobEntity> valid = MONSTERS.stream()
                    .map(type -> type.create(world))
                    .filter(entity -> entity instanceof MobEntity && ((LivingEntity) entity).getMaxHealth() < 20 + LEVEL * 3)
                    .map(entity -> (MobEntity) entity)
                    .collect(Collectors.toList());
            if (!valid.isEmpty()) {
                Random random = world.getRandom();
                MobEntity entity = valid.get(random.nextInt(valid.size()));
                entity.setPositionAndRotation(user.getPosX(), user.getPosYEye(), user.getPosZ(), angle * 180 / (float) Math.PI, 0);
                double factor = random.nextDouble() * 0.9 + 0.2;
                Vector3d motion = new Vector3d(MathHelper.sin(angle) * RADIUS * 0.075, 0.75, MathHelper.cos(angle) * RADIUS * 0.075).mul(factor, factor, factor);
                entity.setMotion(motion);
                entity.onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(entity.getPosition()), SpawnReason.MOB_SUMMONED, null, null);
                ((ServerWorld) world).func_242417_l(entity);
            }
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack hand = playerIn.getHeldItem(handIn);
        if (worldIn instanceof ServerWorld && !playerIn.getCooldownTracker().hasCooldown(this)) {
            worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosYEye(), playerIn.getPosZ(), SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 3, worldIn.rand.nextFloat() * 2);
            hand.getOrCreateChildTag(MythCraft.MOD_ID).putInt("Ticks", DURATION);
            playerIn.getCooldownTracker().setCooldown(this, COOLDOWN);
            return ActionResult.resultSuccess(hand);
        }
        return ActionResult.resultPass(hand);
    }

    private void applyEffect(Entity user) {
        World world = user.getEntityWorld();
        if (world instanceof ServerWorld) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                double xRadius = Math.sqrt(RADIUS * RADIUS - y * y);
                int xRounded = (int) xRadius;
                for (int x = -xRounded; x <= xRounded; x++) {
                    int zRounded = (int) Math.sqrt(xRadius * xRadius - x * x);
                    for (int z = -zRounded; z <= zRounded; z++) {
                        BlockPos pos = user.getPosition().add(x, y, z);
                        List<BlockState> states = getRelatedStates((ServerWorld) world, pos, world.getBlockState(pos));
                        world.setBlockState(pos, states.get(world.getRandom().nextInt(states.size())));
                    }
                }
            }
            AxisAlignedBB bounds = new AxisAlignedBB(user.getPositionVec().subtract(RADIUS, RADIUS, RADIUS), user.getPositionVec().add(RADIUS, RADIUS, RADIUS));
            for (LivingEntity entity : world.getEntitiesWithinAABB(LivingEntity.class, bounds, entity -> entity.getDistanceSq(user) <= RADIUS * RADIUS)) {
                Map<Effect, Integer> level = new HashMap<>();
                for (int i = 0; i < LEVEL; i++) {
                    Effect effect = HARMFUL.get(world.getRandom().nextInt(HARMFUL.size()));
                    level.put(effect, level.getOrDefault(effect, 0) + 1);
                }
                for (Effect effect : level.keySet()) {
                    entity.addPotionEffect(new EffectInstance(effect, effect.isInstant() ? 1 : 80, level.get(effect) - 1, false, true, true));
                }
            }
        }
    }

    private List<BlockState> getRelatedStates(ServerWorld world, BlockPos pos, BlockState state) {
        List<BlockState> states = new ArrayList<>(state.getBlock().getStateContainer().getValidStates());
        Block.getDrops(state, world, pos, null).stream().map(ItemStack::getItem).filter(item -> item instanceof BlockItem).map(item -> ((BlockItem) item).getBlock()).forEach(block -> states.addAll(block.getStateContainer().getValidStates()));
        return states;
    }
}
