package io.github.davidqf555.minecraft.mythical_artifacts.common.items;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.entities.FenrirEntity;
import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class GjollItem extends Item {

    private static final TranslationTextComponent LORE = new TranslationTextComponent("item." + MythicalArtifacts.MOD_ID + ".gjoll_item.lore");

    public GjollItem() {
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

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn instanceof ServerWorld && !(entityIn instanceof PlayerEntity && ((PlayerEntity) entityIn).getCooldownTracker().hasCooldown(this))) {
            CompoundNBT tag = stack.getOrCreateChildTag(MythicalArtifacts.MOD_ID);
            UUID wolf = tag.contains("Fenrir", Constants.NBT.TAG_INT_ARRAY) ? tag.getUniqueId("Fenrir") : null;
            if (wolf == null || ((ServerWorld) worldIn).getEntityByUuid(wolf) == null) {
                FenrirEntity fenrir = RegistryHandler.FENRIR_ENTITY.get().create(worldIn);
                if (fenrir != null) {
                    tag.putUniqueId("Fenrir", fenrir.getUniqueID());
                    fenrir.setOwnerId(entityIn.getUniqueID());
                    fenrir.setTamed(true);
                    fenrir.setPositionAndRotation(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), entityIn.rotationYaw, entityIn.rotationPitch);
                    worldIn.addEntity(fenrir);
                }
            }
        }
    }
}
