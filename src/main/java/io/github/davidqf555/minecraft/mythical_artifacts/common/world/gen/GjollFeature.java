package io.github.davidqf555.minecraft.mythical_artifacts.common.world.gen;

import io.github.davidqf555.minecraft.mythical_artifacts.common.items.ArtifactType;
import io.github.davidqf555.minecraft.mythical_artifacts.common.util.RegistryHandler;
import io.github.davidqf555.minecraft.mythical_artifacts.common.world.ArtifactData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class GjollFeature extends Feature<NoFeatureConfig> {

    public static final Supplier<ConfiguredFeature<?, ?>> FEATURE = () -> RegistryHandler.GJOLL_FEATURE.get().withConfiguration(NoFeatureConfig.field_236559_b_).chance(150);
    private static final int MIN_DEPTH = 1;
    private static final int MAX_DEPTH = 5;

    public GjollFeature() {
        super(NoFeatureConfig.field_236558_a_);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        if (!ArtifactData.get(reader.getWorld()).isFull(ArtifactType.GJOLL)) {
            int y = reader.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
            List<Integer> depths = new ArrayList<>();
            for (int i = MIN_DEPTH; i <= MAX_DEPTH; i++) {
                if (Tags.Blocks.STONE.contains(reader.getBlockState(new BlockPos(pos.getX(), y - i, pos.getZ())).getBlock())) {
                    depths.add(i);
                }
            }
            if (!depths.isEmpty()) {
                int depth = depths.get(rand.nextInt(depths.size()));
                y -= depth;
                BlockPos gen = new BlockPos(pos.getX(), y, pos.getZ());
                setBlockState(reader, gen, RegistryHandler.GJOLL_BLOCK.get().getDefaultState());
                return true;
            }
        }
        return false;
    }
}
