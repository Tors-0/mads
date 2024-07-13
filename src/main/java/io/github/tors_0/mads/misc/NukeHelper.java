package io.github.tors_0.mads.misc;

import io.github.tors_0.mads.entity.ShellEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class NukeHelper {

    private static final HashMap<Block, Block> convertMap = new HashMap<>();

    static {
        convertMap.put(Blocks.SAND, Blocks.GLASS);
        convertMap.put(Blocks.STONE, Blocks.MAGMA_BLOCK);
        convertMap.put(Blocks.DEEPSLATE, Blocks.LAVA);
    }

    public static void createExplosion(ShellEntity entity) {
        explode(entity.getWorld(), entity.getBlockPos(), 35, true);

        ScreenshakeInstance detonationScreenShake = new PositionedScreenshakeInstance(70, entity.getPos(), 60f, 150f, Easing.CIRC_OUT).setIntensity(1.2f, 0f);
        ScreenshakeHandler.addScreenshake(detonationScreenShake);
    }

    public static void explode(World level, BlockPos pos, int diameter, boolean convert) {
        int radius = diameter / 2;
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
        Random random = new Random();
        // Perform the initial explosion
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        mutableBlockPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        if (level.getBlockState(mutableBlockPos).getHardness(level, mutableBlockPos) != -1.0F) {
                            level.setBlockState(mutableBlockPos, Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
        if (convert) {
            int outerRadius = radius + 1;
            for (int x = -outerRadius; x <= outerRadius; x++) {
                for (int y = -outerRadius; y <= outerRadius; y++) {
                    for (int z = -outerRadius; z <= outerRadius; z++) {
                        if (x * x + y * y + z * z > radius * radius && x * x + y * y + z * z <= outerRadius * outerRadius) {
                            mutableBlockPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                            if (convertMap.containsKey(level.getBlockState(mutableBlockPos).getBlock())) {
                                int chance = random.nextInt(100);
                                if (chance < 25) {
                                    level.setBlockState(mutableBlockPos, convertMap.get(level.getBlockState(mutableBlockPos).getBlock()).getDefaultState(), 3);

                                }
                            }
                        }
                    }
                }
            }
        }
        if (diameter >= 20) {
            int explosionsCount = diameter / 10;
            for (int i = 0; i < explosionsCount; i++) {
                double theta = random.nextDouble() * Math.PI * 2;
                double phi = random.nextDouble() * Math.PI - Math.PI / 2;
                int x = (int) (radius * Math.cos(theta) * Math.cos(phi));
                int y = (int) (radius * Math.sin(phi));
                int z = (int) (radius * Math.sin(theta) * Math.cos(phi));
                mutableBlockPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                level.createExplosion(null, mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ(), 10.0F, true, World.ExplosionSourceType.TNT);
            }
        }
        Box sphereArea = new Box(pos).expand(radius);
        List<Entity> entitiesWithinSphere = level.getOtherEntities(null, sphereArea);
        for (Entity entity : entitiesWithinSphere) {
            double distanceSquared = entity.getPos().squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distanceSquared <= radius * radius) {
                entity.damage(level.getDamageSources().explosion(null), 60.0F);
            } else if (distanceSquared <= (radius + 5) * (radius + 5)) {
                if (entity instanceof PlayerEntity) {
                    entity.damage(level.getDamageSources().explosion(null), 15.0F);
                }
            }
        }
    }
}
