package io.github.tors_0.mads.client;

import io.github.tors_0.mads.client.render.MortarBlockEntityRenderer;
import io.github.tors_0.mads.client.render.ShellEntityRenderer;
import io.github.tors_0.mads.client.render.model.MortarBlockEntityModel;
import io.github.tors_0.mads.client.render.model.ShellEntityModel;
import io.github.tors_0.mads.gui.MortarGuiDescription;
import io.github.tors_0.mads.network.ModNetworking;
import io.github.tors_0.mads.registry.ModBlockEntities;
import io.github.tors_0.mads.registry.ModEntities;
import io.github.tors_0.mads.registry.ModItems;
import io.github.tors_0.mads.screen.ModScreenHandlers;
import io.github.tors_0.mads.screen.MortarScreen;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

@ClientOnly
public class MadsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        BlockEntityRendererFactories.register(ModBlockEntities.MORTAR_BLOCK_ENTITY, MortarBlockEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SHELL_ENTITY, ShellEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MortarBlockEntityModel.LAYER_LOCATION, MortarBlockEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ShellEntityModel.LAYER_LOCATION, ShellEntityModel::getTexturedModelData);

        ColorProviderRegistry.ITEM.register(((itemStack, tintIndex) -> {
            return tintIndex != 0 ? -1 : PotionUtil.getColor(itemStack);
        }), ModItems.TIPPED_SHELL);
        ColorProviderRegistry.ITEM.register(((itemStack, tintIndex) -> {
            return tintIndex != 0 ? -1 : PotionUtil.getColor(itemStack);
        }), ModItems.ARMED_TIPPED_SHELL);

        HandledScreens.<MortarGuiDescription, MortarScreen>register(ModScreenHandlers.MORTAR_SCREEN_HANDLER, (gui, inventory, title) -> new MortarScreen(gui, inventory.player, title));

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.MORTAR_ANGLES_SYNC_ID, ((client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            int rotation = buf.readInt();
            int angle = buf.readInt();
            client.execute(() -> {
                client.world.getBlockEntity(pos, ModBlockEntities.MORTAR_BLOCK_ENTITY).get().setRotation(rotation);
                client.world.getBlockEntity(pos, ModBlockEntities.MORTAR_BLOCK_ENTITY).get().setAngle(angle);
            });
        }));
    }
}
