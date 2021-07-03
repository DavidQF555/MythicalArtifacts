package io.github.davidqf555.minecraft.mythcraft.common.items;

import io.github.davidqf555.minecraft.mythcraft.MythCraft;
import io.github.davidqf555.minecraft.mythcraft.common.util.RegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class WarSwordItem extends SwordItem {

    private static final TranslationTextComponent LORE = new TranslationTextComponent("item." + MythCraft.MOD_ID + ".war_sword_item.lore");
    private static final String KILLS_KEY = "item." + MythCraft.MOD_ID + ".war_sword_item.kills";

    public WarSwordItem(int damage, float speed) {
        super(ItemTier.NETHERITE, damage, speed, new Item.Properties()
                .rarity(RegistryHandler.ARTIFACT_RARITY)
                .group(MythCraft.GROUP)
        );
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(LORE.mergeStyle(RegistryHandler.LORE_STYLE));
        CompoundNBT tag = stack.getOrCreateChildTag(MythCraft.MOD_ID);
        int kills = tag.contains("Kills", Constants.NBT.TAG_INT) ? tag.getInt("Kills") : 0;
        tooltip.add(new TranslationTextComponent(KILLS_KEY, kills).mergeStyle(TextFormatting.DARK_RED));
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hitEntity(stack, target, attacker);
        CompoundNBT tag = stack.getOrCreateChildTag(MythCraft.MOD_ID);
        int kills = tag.contains("Kills", Constants.NBT.TAG_INT) ? tag.getInt("Kills") : 0;
        target.attackEntityFrom(attacker instanceof PlayerEntity ? DamageSource.causePlayerDamage((PlayerEntity) attacker) : DamageSource.causeMobDamage(attacker), kills / 25f);
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
