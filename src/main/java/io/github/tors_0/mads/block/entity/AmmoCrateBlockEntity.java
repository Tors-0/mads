package io.github.tors_0.mads.block.entity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import io.github.tors_0.mads.gui.AmmoCrateGuiDescription;
import io.github.tors_0.mads.item.MortarProjectile;
import io.github.tors_0.mads.network.ModNetworking;
import io.github.tors_0.mads.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.Collection;
import java.util.List;

public class AmmoCrateBlockEntity extends BlockEntity implements ImplementedInventory, InventoryProvider, PropertyDelegateHolder, NamedScreenHandlerFactory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);
    protected final PropertyDelegate propertyDelegate;
    private boolean inventoryDirty;

    @Override
    public NbtCompound toSyncedNbt() {
        return this.toNbt();
    }

    public void markInventoryDirty() {
        inventoryDirty = true;
    }

    public AmmoCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AMMO_CRATE_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {

            }

            @Override
            public int size() {
                return 0;
            }
        };
        inventoryDirty = true;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        inventoryDirty = true;
        return inventory;
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        inventoryDirty = true;
        return ImplementedInventory.of(inventory);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new AmmoCrateGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return this.propertyDelegate;
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        inventoryDirty = true;
        return ImplementedInventory.super.removeStack(slot, count);
    }

    @Override
    public ItemStack removeStack(int slot) {
        inventoryDirty = true;
        return ImplementedInventory.super.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventoryDirty = true;
        ImplementedInventory.super.setStack(slot, stack);
    }

    @Override
    public void clear() {
        inventoryDirty = true;
        ImplementedInventory.super.clear();
    }

    @Override
    public ItemStack getStack(int slot) {
        inventoryDirty = true;
        return ImplementedInventory.super.getStack(slot);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        inventoryDirty = true;
        return ImplementedInventory.super.canInsert(slot, stack, side) && stack.getItem() instanceof MortarProjectile proj && proj.isArmed();
    }

    public void setInventory(List<ItemStack> inventory) {
        for (int i = 0; i < 16; i++) {
            this.inventory.set(i, inventory.get(i));
        }
        inventoryDirty = true;
    }

    public void tick(World world, BlockPos blockPos, BlockState blockState) {
        if (world != null && !world.isClient && this.inventoryDirty) {
            Collection<ServerPlayerEntity> viewers = PlayerLookup.tracking(this);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);
            for (int i = 0; i < 16; i++) {
                buf.writeItemStack(inventory.get(i));
            }
            viewers.forEach(player -> ServerPlayNetworking.send(player, ModNetworking.AMMO_CRATE_ITEM_SYNC, buf));
            inventoryDirty = false;
        }
    }
}
