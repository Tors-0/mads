package io.github.tors_0.mads.client.render.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.tors_0.mads.block.entity.AmmoCrateBlockEntity;
import io.github.tors_0.mads.client.render.model.ShellEntityModel;
import io.github.tors_0.mads.registry.ModBlockEntities;
import io.github.tors_0.mads.registry.ModItems;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AmmoCrateBlockEntityRenderer<T extends AmmoCrateBlockEntity, M extends ShellEntityModel<?>> implements BlockEntityRenderer<T> {
    private final M model;

    public AmmoCrateBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super();
        model = (M) new ShellEntityModel<>(context.getLayerModelPart(ShellEntityModel.LAYER_LOCATION));
    }

    @Override
    public void render(AmmoCrateBlockEntity crate, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.scale(-1.0F, -1.0F, 1.0F);
        matrices.translate(-1.125f, -1.5635F, 0.125f);
        float n = 0.0F;
        float o = 0.0F;

        this.model.setAngles(null, o, n, 0, 0,0);
        AtomicReference<List<ItemStack>> inv = new AtomicReference<>();
        crate.getWorld().getBlockEntity(crate.getPos(), ModBlockEntities.AMMO_CRATE_BLOCK_ENTITY).ifPresent(crateBlockEntity -> inv.set(crateBlockEntity.getItems()));

        if (inv.get() != null && !inv.get().isEmpty()) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    matrices.translate(0.25f, 0, 0);
                    if (inv.get().get(j + i * 4).isEmpty()) continue;
                    RenderLayer renderLayer = RenderLayer.getEntitySolid(getTexture(inv.get().get(j + i * 4)));
                    if (renderLayer != null) {
                        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
                        int p = OverlayTexture.getUv(0, false);
                        this.model.render(matrices, vertexConsumer, light, p, 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
                matrices.translate(-1, 0, 0.25f);
            }
        }

        matrices.pop();
    }

    public Identifier getTexture(ItemStack stack) {
        return stack.isOf(ModItems.ARMED_SHELL) ? ShellEntityModel.SHELL_TEXTURE :
                stack.isOf(ModItems.ARMED_NAPALM_SHELL) ? ShellEntityModel.NAPALM_SHELL_TEXTURE :
                        stack.isOf(ModItems.ARMED_TIPPED_SHELL) ? ShellEntityModel.TIPPED_SHELL_TEXTURE :
                                stack.isOf(ModItems.ARMED_HIGH_YIELD_SHELL) ? ShellEntityModel.HIGH_YIELD_SHELL_TEXTURE :
                                        ShellEntityModel.NUKE_SHELL_TEXTURE;
    }
}
