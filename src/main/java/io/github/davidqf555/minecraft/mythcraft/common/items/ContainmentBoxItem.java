package io.github.davidqf555.minecraft.mythcraft.common.items;

import io.github.davidqf555.minecraft.mythcraft.MythCraft;
import io.github.davidqf555.minecraft.mythcraft.common.util.RegistryHandler;
import io.github.davidqf555.minecraft.mythcraft.common.world.ArtifactData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ContainmentBoxItem extends Item {

    private static final int TOTAL = 2500;
    private static final IFormattableTextComponent LORE = new TranslationTextComponent("item." + MythCraft.MOD_ID + ".containment_box_item.lore");
    private static final String FILLED_KEY = "item." + MythCraft.MOD_ID + ".containment_box_item.filled";

    public ContainmentBoxItem() {
        super(new Item.Properties()
                .group(MythCraft.GROUP)
                .maxStackSize(1)
        );
    }

    public static int getFillAmount(LivingEntity entity) {
        return (int) (entity.getMaxHealth() + 0.5f);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(LORE.mergeStyle(RegistryHandler.LORE_STYLE));
        tooltip.add(new TranslationTextComponent(FILLED_KEY, getPercentFilled(stack)).mergeStyle(TextFormatting.GREEN));
        CompoundNBT tag = stack.getOrCreateChildTag(MythCraft.MOD_ID);
        if (tag.contains("Full", Constants.NBT.TAG_BYTE) && tag.getBoolean("Full")) {
            tooltip.add(new TranslationTextComponent(ArtifactType.MAXED_KEY, new StringTextComponent("[").append(ArtifactType.PANDORA_BOX.getItem().getName()).appendString("]"), ArtifactType.PANDORA_BOX.getMaxAmount()).mergeStyle(TextFormatting.DARK_RED));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn instanceof ServerWorld && getPercentFilled(stack) >= 1) {
            if (ArtifactData.get((ServerWorld) worldIn).isFull(ArtifactType.PANDORA_BOX)) {
                stack.getOrCreateChildTag(MythCraft.MOD_ID).putBoolean("Full", true);
            } else {
                IItemHandler inventory = entityIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
                inventory.extractItem(itemSlot, inventory.getSlotLimit(itemSlot), false);
                inventory.insertItem(itemSlot, ArtifactType.PANDORA_BOX.getItem().getDefaultInstance(), false);
            }
        }
    }

    private float getPercentFilled(ItemStack item) {
        CompoundNBT tag = item.getOrCreateChildTag(MythCraft.MOD_ID);
        int total = tag.contains("Filled", Constants.NBT.TAG_INT) ? tag.getInt("Filled") : 0;
        float percent = total * 1f / TOTAL;
        return (int) (percent * 10000) / 100f;
    }
}
