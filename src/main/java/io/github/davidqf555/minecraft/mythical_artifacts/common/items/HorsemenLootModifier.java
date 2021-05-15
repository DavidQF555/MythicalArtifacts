package io.github.davidqf555.minecraft.mythical_artifacts.common.items;

import com.google.gson.JsonObject;
import io.github.davidqf555.minecraft.mythical_artifacts.common.world.ArtifactData;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HorsemenLootModifier extends LootModifier {

    private static final ArtifactType[] TYPES = new ArtifactType[]{ArtifactType.CONQUEST_CROWN, ArtifactType.DEATH_SCYTHE, ArtifactType.FAMINE_SCALES, ArtifactType.WAR_SWORD};

    public HorsemenLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        ArtifactData data = ArtifactData.get(context.getWorld());
        List<ArtifactType> types = Arrays.stream(TYPES).filter(type -> !data.isFull(type)).collect(Collectors.toList());
        if (!types.isEmpty()) {
            double chance = 1.0 / types.size();
            double current = 0;
            double random = context.getRandom().nextDouble();
            for (ArtifactType type : types) {
                current += chance;
                if (random < current) {
                    generatedLoot.add(type.getItem().getDefaultInstance());
                    break;
                }
            }
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<HorsemenLootModifier> {

        @Override
        public HorsemenLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] condition) {
            return new HorsemenLootModifier(condition);
        }

        @Override
        public JsonObject write(HorsemenLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
