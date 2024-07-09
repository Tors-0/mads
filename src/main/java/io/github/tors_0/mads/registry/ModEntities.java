package io.github.tors_0.mads.registry;

import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.entity.ShellEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

public class ModEntities {
    public static final EntityType<ShellEntity> SHELL_ENTITY =
            Registry.register(Registries.ENTITY_TYPE, Mads.getId("shell"),
                    QuiltEntityTypeBuilder.<ShellEntity>create(SpawnGroup.MISC, ShellEntity::new).setDimensions(new EntityDimensions(.65f, .65f, true)).build());

    public static void registerEntityTypes() {}
}
