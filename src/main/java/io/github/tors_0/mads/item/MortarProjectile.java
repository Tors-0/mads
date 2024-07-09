package io.github.tors_0.mads.item;

import io.github.tors_0.mads.entity.ShellEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface MortarProjectile {
    public ShellEntity createShell(World world, ItemStack itemStack, Vec3d pos);
}
