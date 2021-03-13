package io.github.davidqf555.minecraft.mythical_artifacts.common.items;

import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum ArtifactType {

    CONQUEST_CROWN(RegistryHandler.CONQUEST_CROWN, 1),
    DEATH_SCYTHE(RegistryHandler.DEATH_SCYTHE, 1),
    FAMINE_SCALES(RegistryHandler.FAMINE_SCALES, 1),
    WAR_SWORD(RegistryHandler.WAR_SWORD, 1),
    GJOLL(RegistryHandler.GJOLL_ITEM, 1),
    PANDORA_BOX(RegistryHandler.PANDORA_BOX_ITEM, 1);

    private final Supplier<Item> item;
    private final int max;

    ArtifactType(Supplier<Item> item, int max) {
        this.item = item;
        this.max = max;
    }

    @Nullable
    public static ArtifactType get(String name) {
        for (ArtifactType type : values()) {
            if (type.toString().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public Item getItem() {
        return item.get();
    }

    public int getMaxAmount() {
        return max;
    }

    @Override
    public String toString() {
        return getItem().toString();
    }
}
