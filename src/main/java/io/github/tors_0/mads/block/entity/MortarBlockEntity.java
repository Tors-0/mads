package io.github.tors_0.mads.block.entity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import io.github.tors_0.mads.entity.ShellEntity;
import io.github.tors_0.mads.gui.MortarGuiDescription;
import io.github.tors_0.mads.item.MortarProjectile;
import io.github.tors_0.mads.item.ShellItem;
import io.github.tors_0.mads.network.ModNetworking;
import io.github.tors_0.mads.registry.ModBlockEntities;
import io.github.tors_0.mads.registry.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntity;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class MortarBlockEntity extends BlockEntity implements ImplementedInventory, InventoryProvider, PropertyDelegateHolder, NamedScreenHandlerFactory, QuiltBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int time = 0;

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    private int progress = 0;
    private int maxProgress = 20;

    public int getRotation() {
        return rotation;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        boolean canI = true;
        if (slot == 0 && !(stack.getItem() instanceof MortarProjectile proj && proj.isArmed())) {
            canI = false;
        } else if (slot == 1 && !(stack.isOf(Items.GUNPOWDER))) {
            canI = false;
        }
        return ImplementedInventory.super.canInsert(slot, stack, side) && canI;
    }

    public int getAngle() {
        return angle;
    }

    private int rotation = 0;
    private int angle = 85;

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.of(this);
    }

    public MortarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MORTAR_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MortarBlockEntity.this.progress;
                    case 1 -> MortarBlockEntity.this.maxProgress;
                    case 2 -> MortarBlockEntity.this.rotation;
                    case 3 -> MortarBlockEntity.this.angle;
                    default -> -1;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MortarBlockEntity.this.progress = value;
                    case 1 -> MortarBlockEntity.this.maxProgress = value;
                    case 2 -> MortarBlockEntity.this.rotation = value;
                    case 3 -> MortarBlockEntity.this.angle = value;
                }
                markDirty();
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("mortar.progress");
        rotation = nbt.getInt("mortar.rot");
        angle = nbt.getInt("mortar.angle");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("mortar.progress", progress);
        nbt.putInt("mortar.rot", rotation);
        nbt.putInt("mortar.angle", angle);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos blockPos, BlockState blockState) {
        if (world.isClient) return;
        this.time++;
        if (this.time >= 20) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(this.getPos());
            buf.writeInt(this.getRotation());
            buf.writeInt(this.getAngle());
            ServerPlayNetworking.send(PlayerLookup.tracking((ServerWorld) world, this.getPos()), ModNetworking.MORTAR_ANGLES_SYNC_ID, buf);
            this.time = 0;
        }

        if (this.hasRecipe() && (this.progress > 0 || this.getWorld().isReceivingRedstonePower(blockPos))) {
            this.increaseLaunchProgress();
            markDirty(world, blockPos, blockState);

            if (this.hasLaunchingFinished()) {
                this.launch();
                this.resetProgress();
            }
        } else {
            this.resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    public static float clampYaw(float yaw) {
        yaw = yaw % 360; // Gets the remainder when dividing angle by 360
        if(yaw < 0) {
            yaw = 360 - yaw;
        }
        return yaw;
    }

    /**
     * Creates a Vec3 using the pitch and yaw of the entity's rotation.
     */
    public static Vec3d getVectorForRotation(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d(f1 * f2, f3, f * f2);
    }

    private void launch() {

        Vec3d velocity = getVectorForRotation(angle, clampYaw(rotation)).multiply(-1);
        Vec3d pos = this.pos.ofCenter().add(velocity.normalize().multiply(1.5));

        ItemStack itemStack = this.getStack(0);
        ShellItem shellItem = (ShellItem) (itemStack.getItem() instanceof ShellItem ? itemStack.getItem() : ModItems.SHELL);
        ShellEntity shell = shellItem.createShell(getWorld(), itemStack, pos);

        shell.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ(), 2.5f, 0.1f);
        world.spawnEntity(shell);
        ((ServerWorld) world).spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX(), pos.getY(), pos.getZ(), 3, 0,0,0, 0.005);
        world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 7f, 5f);

        this.removeStack(0, 1);
        this.removeStack(1, 1);
    }

    private boolean hasLaunchingFinished() {
        return progress >= maxProgress;
    }

    private void increaseLaunchProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        return this.getStack(0).getItem() instanceof MortarProjectile projectile && projectile.isArmed() && this.getStack(1).isOf(Items.GUNPOWDER);
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return ImplementedInventory.of(inventory);
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new MortarGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
    }
}
