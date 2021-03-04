package io.github.davidqf555.minecraft.mythical_artifacts.common.world;

import io.github.davidqf555.minecraft.mythical_artifacts.MythicalArtifacts;
import io.github.davidqf555.minecraft.mythical_artifacts.common.items.ArtifactType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArtifactData extends WorldSavedData {

    private static final String NAME = MythicalArtifacts.MOD_ID + "_ArtifactData";
    private final Map<ArtifactType, UUID[]> owners;

    public ArtifactData() {
        super(NAME);
        owners = new EnumMap<>(ArtifactType.class);
        for (ArtifactType type : ArtifactType.values()) {
            owners.put(type, new UUID[type.getMaxAmount()]);
        }
    }

    public static ArtifactData get(ServerWorld world) {
        return world.getServer().getWorld(World.OVERWORLD).getSavedData().getOrCreate(ArtifactData::new, NAME);
    }

    public void tick(ServerWorld world) {
        PlayerList players = world.getServer().getPlayerList();
        for (ArtifactType type : owners.keySet()) {
            UUID[] ids = owners.get(type);
            Map<ServerPlayerEntity, Integer> count = new HashMap<>();
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] != null) {
                    ServerPlayerEntity player = players.getPlayerByUUID(ids[i]);
                    if (player != null) {
                        int amount = count.getOrDefault(player, 0);
                        if (amount < getAmount(player.inventory, type.getItem())) {
                            count.put(player, amount + 1);
                        } else {
                            ids[i] = null;
                        }
                    }
                }
            }
        }
    }

    private int getAmount(PlayerInventory inventory, Item item) {
        return inventory.mainInventory.stream().filter(stack -> stack.getItem().equals(item)).mapToInt(ItemStack::getCount).sum() + inventory.armorInventory.stream().filter(stack -> stack.getItem().equals(item)).mapToInt(ItemStack::getCount).sum() + inventory.offHandInventory.stream().filter(stack -> stack.getItem().equals(item)).mapToInt(ItemStack::getCount).sum();
    }

    public UUID[] getOwners(ArtifactType type) {
        return owners.get(type);
    }

    @Override
    public void read(CompoundNBT nbt) {
        for (String key : nbt.keySet()) {
            ArtifactType type = ArtifactType.get(key);
            UUID[] ids = owners.get(type);
            ListNBT list = (ListNBT) nbt.get(key);
            int length = Math.min(ids.length, list.size());
            for (int i = 0; i < length; i++) {
                ids[i] = NBTUtil.readUniqueId(list.get(i));
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        for (ArtifactType type : owners.keySet()) {
            ListNBT list = new ListNBT();
            for (UUID id : owners.get(type)) {
                if (id != null) {
                    list.add(NBTUtil.func_240626_a_(id));
                }
            }
            compound.put(type.toString(), list);
        }
        return compound;
    }
}
