package io.github.davidqf555.minecraft.mythical_artifacts.common.items;

import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import net.minecraft.item.Item;

import java.util.function.Supplier;

public enum ArtifactType {

    CONQUEST_CROWN(RegistryHandler.CONQUEST_CROWN_ITEM, 1),
    DEATH_SCYTHE(RegistryHandler.DEATH_SCYTHE_ITEM, 1),
    FAMINE_SCALES(RegistryHandler.FAMINE_SCALES_ITEM, 1),
    WAR_SWORD(RegistryHandler.WAR_SWORD_ITEM, 1),
    GJOLL(RegistryHandler.GJOLL_ITEM, 1),
    PANDORA_BOX(RegistryHandler.PANDORA_BOX_ITEM, 1);

    private final Supplier<? extends Item> item;
    private final int max;

    ArtifactType(Supplier<? extends Item> item, int max) {
        this.item = item;
        this.max = max;
    }

    public Item getItem() {
        return item.get();
    }

    public int getMaxAmount() {
        return max;
    }

}
