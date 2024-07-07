package io.github.tors_0.mads.block.entity;

import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;

public class ModBlockEntities {
    public static final BlockEntityType<MortarBlockEntity> MORTAR_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Mads.getId("mortar"),
                    QuiltBlockEntityTypeBuilder.create(MortarBlockEntity::new,
                            ModBlocks.MORTAR).build());

    public static void registerBlockEntities() {}
}
