package io.github.tors_0.mads.client;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.systems.particle.screen.ScreenParticleHolder;


public class ClientEvents implements ParticleEmitterHandler.ItemParticleSupplier {
    public static final ScreenParticleHolder PARTICLES = new ScreenParticleHolder();

    @Override
    public void spawnLateParticles(ScreenParticleHolder target, World level, float partialTick, ItemStack stack, float x, float y) {
        target.addFrom(PARTICLES);
    }
}
