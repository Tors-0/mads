package io.github.tors_0.mads.item;

import io.github.tors_0.mads.entity.ShellEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShellItem extends ArrowItem implements MortarProjectile {
    @Override
    public boolean isArmed() {
        return armed;
    }

    private final boolean armed;

    @Override
    public ShellEntity createShell(World world, ItemStack stack, Vec3d pos) {
        ShellEntity shellEntity = new ShellEntity(pos.x, pos.y, pos.z, world);
        shellEntity.initFromStack(stack);
        return shellEntity;
    }

    public ShellItem(Settings settings, boolean armed) {
        super(settings);
        this.armed = armed;
    }
}
