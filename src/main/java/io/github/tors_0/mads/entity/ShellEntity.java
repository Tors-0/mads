package io.github.tors_0.mads.entity;

import com.google.common.collect.Sets;
import io.github.tors_0.mads.registry.ModEntities;
import io.github.tors_0.mads.registry.ModItems;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Set;

public class ShellEntity extends PersistentProjectileEntity {
    private static final int EXPOSED_POTION_DECAY_DURATION = 600;
    private static final int DEFAULT_COLOR = -1;
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(ShellEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final byte EFFECT_PARTICLE_SPAWN_INTERVAL = 0;
    private Potion potion = Potions.EMPTY;
    private final Set<StatusEffectInstance> effects = Sets.<StatusEffectInstance>newHashSet();
    private boolean colorSet;

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

    public void initFromStack(ItemStack stack) {
        if (stack.isOf(ModItems.ARMED_TIPPED_SHELL)) {
            this.potion = PotionUtil.getPotion(stack);
            Collection<StatusEffectInstance> collection = PotionUtil.getCustomPotionEffects(stack);
            if (!collection.isEmpty()) {
                for (StatusEffectInstance statusEffectInstance : collection) {
                    this.effects.add(new StatusEffectInstance(statusEffectInstance));
                }
            }

            int i = getCustomPotionColor(stack);
            if (i == -1) {
                this.initColor();
            } else {
                this.setColor(i);
            }
        } else if (stack.isOf(ModItems.ARMED_SHELL)) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.dataTracker.set(COLOR, -1);
        }
    }

    public static int getCustomPotionColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.contains("CustomPotionColor", NbtElement.NUMBER_TYPE) ? nbtCompound.getInt("CustomPotionColor") : -1;
    }

    private void initColor() {
        this.colorSet = false;
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.dataTracker.set(COLOR, -1);
        } else {
            this.dataTracker.set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR, -1);
    }

    public void addEffect(StatusEffectInstance effect) {
        this.effects.add(effect);
        this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        this.detonate();
        return false;
    }

    private void spawnParticles(int amount) {
        int i = this.getColor();
        if (i != -1 && amount > 0) {
            double d = (double)(i >> 16 & 0xFF) / 255.0;
            double e = (double)(i >> 8 & 0xFF) / 255.0;
            double f = (double)(i >> 0 & 0xFF) / 255.0;

            for (int j = 0; j < amount; j++) {
                this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
            }
        }
    }

    public int getColor() {
        return this.dataTracker.get(COLOR);
    }

    private void setColor(int color) {
        this.colorSet = true;
        this.dataTracker.set(COLOR, color);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.potion != Potions.EMPTY) {
            nbt.putString("Potion", Registries.POTION.getId(this.potion).toString());
        }

        if (this.colorSet) {
            nbt.putInt("Color", this.getColor());
        }

        if (!this.effects.isEmpty()) {
            NbtList nbtList = new NbtList();

            for (StatusEffectInstance statusEffectInstance : this.effects) {
                nbtList.add(statusEffectInstance.writeNbt(new NbtCompound()));
            }

            nbt.put("CustomPotionEffects", nbtList);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Potion", NbtElement.STRING_TYPE)) {
            this.potion = PotionUtil.getPotion(nbt);
        }

        for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(nbt)) {
            this.addEffect(statusEffectInstance);
        }

        if (nbt.contains("Color", NbtElement.NUMBER_TYPE)) {
            this.setColor(nbt.getInt("Color"));
        } else {
            this.initColor();
        }
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.SOUNDS;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (this.potion != Potions.EMPTY) {
            AreaEffectCloudEntity areaEffectCloud = new AreaEffectCloudEntity(getWorld(),
                    blockHitResult.getPos().getX(), blockHitResult.getPos().getY(), blockHitResult.getPos().getZ());

            areaEffectCloud.setRadius(3.0F);
            areaEffectCloud.setRadiusOnUse(-0.5F);
            areaEffectCloud.setWaitTime(10);
            areaEffectCloud.setRadiusGrowth(-areaEffectCloud.getRadius() / (float) areaEffectCloud.getDuration());
            areaEffectCloud.setPotion(potion);

            for (StatusEffectInstance statusEffectInstance : PotionUtil.getPotionEffects(this.potion, potion.getEffects())) {
                areaEffectCloud.addEffect(
                        new StatusEffectInstance(
                                statusEffectInstance.getEffectType(),
                                Math.max(statusEffectInstance.mapDuration(i -> i / 8), 1),
                                statusEffectInstance.getAmplifier(),
                                statusEffectInstance.isAmbient(),
                                statusEffectInstance.shouldShowParticles()
                        ));
            }

            areaEffectCloud.setColor(this.getColor());

            this.getWorld().spawnEntity(areaEffectCloud);

            this.getWorld()
                    .playSound(
                            this,
                            getBlockPos(),
                            SoundEvents.ENTITY_GENERIC_EXPLODE,
                            SoundCategory.BLOCKS,
                            4.0F,
                            (1.0F + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2F) * 0.7F
                    );
            this.discard();
        } else {
            this.detonate();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity && !(entity instanceof EndermanEntity)) {
            entity.damage(this.getDamageSources().mobProjectile(this, null), 10f);
            entity.addVelocity(this.getVelocity());
            entity.velocityDirty = true;

            for (StatusEffectInstance statusEffectInstance : this.potion.getEffects()) {
                ((LivingEntity) entity).addStatusEffect(
                        new StatusEffectInstance(
                                statusEffectInstance.getEffectType(),
                                Math.max(statusEffectInstance.mapDuration(i -> i / 8), 1),
                                statusEffectInstance.getAmplifier(),
                                statusEffectInstance.isAmbient(),
                                statusEffectInstance.shouldShowParticles()
                        ),
                        this
                );
            }
        }
    }

    @Override
    protected ItemStack asItemStack() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(ModItems.ARMED_SHELL);
        } else {
            ItemStack itemStack = new ItemStack(ModItems.ARMED_TIPPED_SHELL);
            PotionUtil.setPotion(itemStack, this.potion);
            PotionUtil.setCustomPotionEffects(itemStack, this.effects);
            if (this.colorSet) {
                itemStack.getOrCreateNbt().putInt("CustomPotionColor", this.getColor());
            }

            return itemStack;
        }
    }
}
