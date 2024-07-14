package io.github.tors_0.mads.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NukeSubExplosion extends Explosion {
    public NukeSubExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, List<BlockPos> affectedBlocks) {
        super(world, entity, x, y, z, power, affectedBlocks);
    }

    public NukeSubExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType) {
        super(world, entity, x, y, z, power, createFire, destructionType);
    }

    public NukeSubExplosion(World world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, DestructionType destructionType) {
        super(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
    }

    public NukeSubExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType, List<BlockPos> affectedBlocks) {
        super(world, entity, x, y, z, power, createFire, destructionType, affectedBlocks);
    }

    @Override
    public void affectWorld(boolean particles) {
        super.affectWorld(false);
    }
}
