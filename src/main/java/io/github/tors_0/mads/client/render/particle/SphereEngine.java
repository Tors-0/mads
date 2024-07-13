package io.github.tors_0.mads.client.render.particle;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/// thanks to donut for helping with getting this working
@ClientOnly
public class SphereEngine {

    public static Vec3d latestCameraPos;
    private static List<Sphere> spheres;
    private static float previousTick;
    private static List<Sphere> scheduledForRemoval;

    public static void RenderSpheres(WorldRenderContext context) {
        if (spheres == null) spheres = new ArrayList<>();
        if (scheduledForRemoval == null) scheduledForRemoval = new ArrayList<>();

        float deltaTick = context.tickDelta();

        sortSpheres();
        for (Sphere sphere : spheres) {
            if (!sphere.isValid(deltaTick)) {
                markForRemoval(sphere);
                continue;
            }
            sphere.render(context.camera(), context.matrixStack(), deltaTick, sphere);
        }
        emptySchedule();
    }


    public static void addSphere(Sphere sphere) {
        if (spheres == null) spheres = new ArrayList<>();
        spheres.add(sphere);
    }


    public static void clear() {
        spheres = null;
    }


    public static void markForRemoval(Sphere sphere) {
        scheduledForRemoval.add(sphere);
    }


    public static void emptySchedule() {
        for (Sphere i : scheduledForRemoval) {
            spheres.remove(i);
        }
        scheduledForRemoval.clear();
    }


    public static void sortSpheres() {
        spheres.sort(new SphereComparator());
    }


    public static class Sphere {
        public RenderLayer type;
        public Vec3d pos;
        public float radius;
        public float opacity;
        public boolean isValid;
        public Color color;

        public Sphere(RenderLayer type, Vec3d pos, float radius, float opacity, Color color) {
            this.type = type;
            this.pos = pos;
            this.radius = radius;
            this.opacity = opacity;
            this.color = color;
            this.isValid = true;
        }

        public boolean isValid(float deltaTick) {
            return isValid;
        }


        public void render(Camera camera, MatrixStack stack, float deltaTick, Sphere sphere) {
            latestCameraPos = camera.getPos();
            Vec3d cameraPos = latestCameraPos;
            Vec3d spherePos = this.pos;
            float radius = this.radius;
            RenderLayer type = this.type;
            stack.push();
            stack.translate(spherePos.x - cameraPos.x, spherePos.y - cameraPos.y, spherePos.z - cameraPos.z);
            VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setRenderType(type);
            if (this.color != null)
                builder.setColor(this.color);
            builder.setAlpha(this.opacity).renderSphere(stack, radius, 20, 20);
            stack.pop();
        }

    }

    public static class TimerSphere extends Sphere {
        public int maxTime;
        public float lifeTime = 0;

        public TimerSphere(RenderLayer type, Vec3d pos, float radius, int maxTime, float opacity,Color color) {
            super(type, pos, radius, opacity,color);
            this.maxTime = maxTime;
        }

        @Override
        public boolean isValid(float deltaTick) {
            lifeTime += deltaTick;
            if (lifeTime > maxTime) return false;
            return true;
        }
    }

    public static class TimerGrowingSphere extends TimerSphere {
        public float minRadius;
        public float maxRadius;

        public TimerGrowingSphere(RenderLayer type, Vec3d pos, float minRadius, float maxRadius, int maxTime, float opacity, Color color) {
            super(type, pos, minRadius, maxTime, opacity, color);
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
        }

        @Override
        public boolean isValid(float deltaTick) {
            this.radius = minRadius + (lifeTime / maxTime) * (maxRadius - minRadius);
            lifeTime += deltaTick;
            if (lifeTime > maxTime) return false;
            return true;
        }
    }

    public static class OrbitalSphere extends Sphere {
        public Vec3d orbitCentre;
        public float orbitRadius;
        public int fullOrbitTime;
        public float lifeTime = 0;


        public OrbitalSphere(RenderLayer type, Vec3d orbitCentre, float sphereRadius, float orbitRadius, int fullOrbitTime, float opacity,Color color) {
            super(type, orbitCentre.add(orbitRadius, 0, 0), sphereRadius, opacity,color);
            this.orbitCentre = orbitCentre;
            this.orbitRadius = orbitRadius;
            this.fullOrbitTime = fullOrbitTime;
        }

        @Override
        public boolean isValid(float deltaTick) {
            this.lifeTime += deltaTick;
            this.pos = orbitCentre.add(orbitRadius * Math.cos((lifeTime * 2 * Math.PI) / fullOrbitTime), 0, orbitRadius * Math.sin((lifeTime * 2 * Math.PI) / (fullOrbitTime)));
            return true;
        }
    }

    public static class SphereComparator implements Comparator<Sphere> {
        @Override
        public int compare(Sphere o1, Sphere o2) {
            double distance1 = o1.pos.distanceTo(SphereEngine.latestCameraPos);
            double distance2 = o2.pos.distanceTo(SphereEngine.latestCameraPos);
            if (distance1 == distance2) {
                return 0;
            }
            if (distance1 > distance2) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}