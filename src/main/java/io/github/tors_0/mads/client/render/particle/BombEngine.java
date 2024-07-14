package io.github.tors_0.mads.client.render.particle;

import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.tors_0.mads.Mads;
import io.github.tors_0.mads.client.ClientEvents;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.registry.common.particle.LodestoneScreenParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.SimpleParticleOptions;
import team.lodestar.lodestone.systems.particle.builder.ScreenParticleBuilder;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;

import java.awt.*;
import java.util.Random;


/**
 * Thanks to makozort for providing the basis for this class:
 *  <a href="https://github.com/makozort">Makozort on GitHub</a><br>
 *  As a result of this contribution, the contents of this class do not fall under the ARR license
 *  the rest of the project is bound by. This class is under a GPLv3.0 License
 */
public class BombEngine {
    protected static final Identifier SHOCK_WAVE = Mads.getId("textures/vfx/shockwave.png");
    private static final RenderLayer SHOCK_WAVE_TYPE = LodestoneRenderTypeRegistry.TRANSPARENT_TEXTURE.applyWithModifier(RenderTypeToken.createToken(SHOCK_WAVE), b -> b.replaceVertexFormat(VertexFormat.DrawMode.TRIANGLES));
    public static Color centre = new Color(255, 255, 255);
    public static Color outer = new Color(180, 102, 15);
    public static Color smoke = new Color(0, 0, 0);
    public static float MAX_EXPLOSION_SCALE = 40f;
    public static float NORMAL_SPREAD = 23;
    public static float SMOKE_SCALE = 10f;
    public static float FIRE_OFFSET = 20f;
    public static float SMOKE_OFFSET = 15;

    public static void spawn(World level, BlockPos pos, float scalar, boolean smoke, boolean flash, boolean sphere) {
        centre(level, pos, scalar);
        if (smoke) {
            smoke(level, pos, scalar);
        }
        if (flash) {
            ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.WISP, ClientEvents.PARTICLES)
                    .setScaleData(GenericParticleData.create(999999999).setEasing(Easing.EXPO_IN_OUT).build())
                    .setTransparencyData(GenericParticleData.create(.5f, 0).setEasing(Easing.EXPO_OUT).build())
                    .setColorData(ColorParticleData.create(outer, outer).build())
                    .setLifetime(80)
                    .spawn(pos.getX(), (pos.getZ()));
        }
        if (sphere) {
            SphereEngine.addSphere(new SphereEngine.TimerGrowingSphere(SHOCK_WAVE_TYPE, pos.ofCenter(), 0, 10000, 2000, .5f,null));
        }
    }

    public static void centre(World level, BlockPos pos, float scalar) {
        for (int i = 0; i < 400; i++) {
            Random random = new Random();
            WorldParticleBuilder.create(LodestoneParticleRegistry.STAR_PARTICLE)
                    .setScaleData(GenericParticleData.create((MAX_EXPLOSION_SCALE * scalar) / 2).setEasing(Easing.EXPO_IN_OUT).build())
                    .setTransparencyData(GenericParticleData.create(.5f, 0).build())
                    .setColorData(ColorParticleData.create(centre, centre).build())
                    .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE.withDepthFade())
                    .enableForcedSpawn()
                    .setLifetime(80)
                    .enableNoClip()
                    .spawn(level, getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getX(), pos.getY(), getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getZ()); // initial inner white


            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(MAX_EXPLOSION_SCALE * scalar).setEasing(Easing.BOUNCE_IN).setEasing(Easing.BACK_OUT).build())
                    .setTransparencyData(GenericParticleData.create(.5f, 0).build())
                    .enableForcedSpawn()
                    .setColorData(ColorParticleData.create(outer, smoke).setEasing(Easing.BOUNCE_IN).build())
                    .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE.withDepthFade())
                    .setLifetime(80)
                    .enableNoClip()
                    .spawn(level, getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getX(), pos.getY(), getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getZ()); // outer orange

        }
        WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                .setScaleData(GenericParticleData.create((MAX_EXPLOSION_SCALE * 100) * scalar).setEasing(Easing.BOUNCE_IN).setEasing(Easing.BACK_OUT).build())
                .setTransparencyData(GenericParticleData.create(.8f).build())
                .setColorData(ColorParticleData.create(outer, outer).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN).build())
                .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE.withDepthFade())
                .setLifetime(20)
                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((level.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                .enableNoClip()
                .spawn(level, pos.getX(), pos.getY() + 5, pos.getZ());
    }

    public static void smoke(World level, BlockPos pos, float scalar) {
        for (int i = 0; i < 400; i++) {
            Random random = new Random();
            WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
                    .setScaleData(GenericParticleData.create(12.5f).build())
                    .setTransparencyData(GenericParticleData.create(.2f, 0).build())
                    .enableForcedSpawn()
                    .setColorData(ColorParticleData.create(outer, smoke).setEasing(Easing.BOUNCE_OUT).build())
                    .setLifetime(2000)
                    .setDiscardFunction(SimpleParticleOptions.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
                    .setRandomMotion(.2, 0, .2)
                    .addMotion(0, 0.2f, 0)
                    .enableNoClip()
                    .setRenderType(LodestoneWorldParticleRenderType.LUMITRANSPARENT)
                    .spawn(level, getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getX(), pos.getY(), getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getZ()); //mushroom cloud

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(SMOKE_SCALE * scalar).build())
                    .setTransparencyData(GenericParticleData.create(.2f, 0).build())
                    .enableForcedSpawn()
                    .setColorData(ColorParticleData.create(smoke, outer).setEasing(Easing.BOUNCE_OUT).build())
                    .setLifetime(2000)
                    .setDiscardFunction(SimpleParticleOptions.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
                    .setRandomMotion(.3 * ((double) i / 2), 0, .3 * ((double) i / 2))
                    .addMotion(0, 0.3 * ((double) i / 2), 0)
                    .enableNoClip()
                    .setRenderType(LodestoneWorldParticleRenderType.LUMITRANSPARENT)
                    .spawn(level, getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getX(), pos.getY(), getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getZ()); // smoke explosion

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(SMOKE_SCALE * scalar).build())
                    .setTransparencyData(GenericParticleData.create(.2f, 0).build())
                    .enableForcedSpawn()
                    .setColorData(ColorParticleData.create(smoke, smoke).setEasing(Easing.BOUNCE_OUT).build())
                    .setLifetime(2000)
                    .setDiscardFunction(SimpleParticleOptions.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
                    .setRandomMotion(.2, 0, .2)
                    .setRenderType(LodestoneWorldParticleRenderType.LUMITRANSPARENT)
                    .addMotion(0, 0.3 / (i), 0)
                    .enableNoClip()
                    .spawn(level, getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getX(), pos.getY() - 10, getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getZ()); // smoking spread

            WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
                    .setScaleData(GenericParticleData.create(SMOKE_SCALE * scalar).build())
                    .setTransparencyData(GenericParticleData.create(.2f, 0).build())
                    .enableForcedSpawn()
                    .setColorData(ColorParticleData.create(outer, smoke).setEasing(Easing.BOUNCE_OUT).build())
                    .setLifetime(2000)
                    .setRenderType(LodestoneWorldParticleRenderType.LUMITRANSPARENT)
                    .setDiscardFunction(SimpleParticleOptions.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
                    .addMotion(0, (double) 1 / (i), 0)
                    .enableNoClip()
                    .spawn(level, getRandomBlockPosInRange(pos, (int) (15 * scalar), random).getX(), pos.getY() - (SMOKE_OFFSET * scalar), getRandomBlockPosInRange(pos, (int) (15 * scalar), random).getZ()); // smoke collum rise

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(SMOKE_SCALE * scalar).build())
                    .setTransparencyData(GenericParticleData.create(.2f, 0).build())
                    .enableForcedSpawn()
                    .setColorData(ColorParticleData.create(outer, outer).build())
                    .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE.withDepthFade())
                    .setLifetime(2000)
                    .setDiscardFunction(SimpleParticleOptions.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
                    .enableNoClip()
                    .spawn(level, getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getX(), pos.getY() - (FIRE_OFFSET * scalar), getRandomBlockPosInRange(pos, (int) (NORMAL_SPREAD * scalar), random).getZ()); // FIRE
        }
    }

    public static BlockPos getRandomBlockPosInRange(BlockPos original, int range, Random random) {
        int x = original.getX() + random.nextInt(range * 2 + 1) - range;
        int y = original.getY() + random.nextInt(range * 2 + 1) - range;
        int z = original.getZ() + random.nextInt(range * 2 + 1) - range;
        return new BlockPos(x, y, z);
    }

}