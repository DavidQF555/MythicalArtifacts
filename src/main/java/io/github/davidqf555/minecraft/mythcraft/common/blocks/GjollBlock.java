package io.github.davidqf555.minecraft.mythcraft.common.blocks;

import io.github.davidqf555.minecraft.mythcraft.common.entities.FenrirEntity;
import io.github.davidqf555.minecraft.mythcraft.common.util.RegistryHandler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
public class GjollBlock extends Block {

    public static final int SUMMON_RADIUS = 8;

    public GjollBlock() {
        super(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.STONE)
                .tickRandomly()
                .hardnessAndResistance(20, 1200)
        );
    }

    @Deprecated
    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        super.randomTick(state, worldIn, pos, rand);
        AxisAlignedBB bounds = new AxisAlignedBB(pos.add(-SUMMON_RADIUS, -SUMMON_RADIUS, -SUMMON_RADIUS), pos.add(SUMMON_RADIUS, SUMMON_RADIUS, SUMMON_RADIUS));
        if (worldIn.getEntitiesWithinAABB(FenrirEntity.class, bounds, entity -> entity.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) <= SUMMON_RADIUS * SUMMON_RADIUS && pos.equals(entity.getGjoll())).isEmpty()) {
            FenrirEntity fenrir = RegistryHandler.FENRIR_ENTITY.get().create(worldIn);
            if (fenrir != null) {
                List<BlockPos> air = new ArrayList<>();
                for (int y = -SUMMON_RADIUS; y <= SUMMON_RADIUS; y++) {
                    double xRadius = Math.sqrt(SUMMON_RADIUS * SUMMON_RADIUS - y * y);
                    int xRounded = (int) xRadius;
                    for (int x = -xRounded; x <= xRounded; x++) {
                        int zRounded = (int) Math.sqrt(xRadius * xRadius - x * x);
                        for (int z = -zRounded; z <= zRounded; z++) {
                            BlockPos check = pos.add(x, y, z);
                            if (worldIn.isAirBlock(check) && worldIn.isTopSolid(check.down(), fenrir) && worldIn.canSeeSky(check)) {
                                air.add(check);
                            }
                        }
                    }
                }
                if (!air.isEmpty()) {
                    BlockPos spawn = air.get(rand.nextInt(air.size()));
                    fenrir.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
                    fenrir.setGjoll(pos);
                    worldIn.addEntity(fenrir);
                }
            }
        }
    }


}
