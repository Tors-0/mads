package io.github.tors_0.mads.item;

import io.github.tors_0.mads.entity.ShellEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TippedShellItem extends TippedArrowItem implements MortarProjectile {
    public TippedShellItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        return PotionUtil.setPotion(super.getDefaultStack(), Potions.POISON);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        PotionUtil.appendDetailsToTooltip(stack, tooltip, 0.125F);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return PotionUtil.getPotion(stack).finishTranslationKey(this.getTranslationKey() + ".effect.");
    }

    @Override
    public ShellEntity createShell(World world, ItemStack itemStack, Vec3d pos) {
        ShellEntity shellEntity = new ShellEntity(pos.x, pos.y, pos.z, world);
        shellEntity.initFromStack(itemStack);
        return shellEntity;
    }
}
