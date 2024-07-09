package io.github.tors_0.mads.entity;

import io.github.tors_0.mads.registry.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class ShellEntity extends PersistentProjectileEntity {
    public ShellEntity(double x, double y, double z, World world) {
        super(ModEntities.SHELL_ENTITY, x, y, z, world);
    }

    public ShellEntity(LivingEntity owner, World world) {
        super(ModEntities.SHELL_ENTITY, owner, world);
    }

    public ShellEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void detonate() {
        this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 2.3f, false, World.ExplosionSourceType.TNT);
        this.discard();
    }

    @Override
    protected float getDragInWater() {
        return 1;
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        this.detonate();
        return false;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.SOUNDS;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.detonate();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity && !(entity instanceof EndermanEntity)) {
            entity.damage(this.getDamageSources().mobProjectile(this, null), 10f);
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return null;
    }
}
