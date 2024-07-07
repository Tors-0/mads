package io.github.tors_0.mads.block.entity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import io.github.tors_0.mads.screen.MortarScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class MortarBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, InventoryProvider, PropertyDelegateHolder {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;
    private int rot = 0;
    private int angle = 88;

    public MortarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MORTAR_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MortarBlockEntity.this.progress;
                    case 1 -> MortarBlockEntity.this.maxProgress;
                    case 2 -> MortarBlockEntity.this.rot;
                    case 3 -> MortarBlockEntity.this.angle;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MortarBlockEntity.this.progress = value;
                    case 1 -> MortarBlockEntity.this.maxProgress = value;
                    case 2 -> MortarBlockEntity.this.rot = value;
                    case 3 -> MortarBlockEntity.this.angle = value;
                }
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
        rot = nbt.getInt("mortar.rot");
        angle = nbt.getInt("mortar.angle");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("mortar.progress", progress);
        nbt.putInt("mortar.rot", rot);
        nbt.putInt("mortar.angle", angle);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.mads.mortar");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new MortarScreenHandler(i, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos blockPos, BlockState blockState) {
        if (world.isClient) return;

        if (this.hasRecipe()) {
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

    private void launch() {
        this.removeStack(0, 1);
    }

    private boolean hasLaunchingFinished() {
        return progress >= maxProgress;
    }

    private void increaseLaunchProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        return this.getStack(0).isOf(Items.COBBLESTONE) && this.getStack(0).getCount() > 0;
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return ImplementedInventory.of(inventory);
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }
}
