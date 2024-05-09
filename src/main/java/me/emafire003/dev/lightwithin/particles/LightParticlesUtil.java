package me.emafire003.dev.lightwithin.particles;

import me.emafire003.dev.particleanimationlib.effects.VortexEffect;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.joml.Vec3f;

import java.util.ArrayList;

public class LightParticlesUtil {

    public static void spawnDefaultLightParticleSequence(ServerPlayerEntity player){
        PrecompiledParticleEffects.spawnLightCircle(player);
        spawnDefaultLightParticleColumns(player);
    }

    public static void spawnLightTypeParticle(ParticleEffect particle, ServerWorld world, Vec3d pos){
        /*for(int i = 0; i < 360; i++) {
                if(i % 20 == 0) {
                    world.spawnParticles(particle,
                            pos.getX(), pos.getY(), pos.getZ(), 1,
                            Math.cos(i) * 0.25d, 0.15d, Math.sin(i) * 0.25d, 0.01);
                }
        }*/
        world.spawnParticles(particle,
                pos.getX(), pos.getY(), pos.getZ(), 50,
                0.25, 0.15d, 0.25, 0.2);


    }

    
    public static void spawnDefaultLightParticleColumns(ServerPlayerEntity player){
        Vec3d pos = player.getPos();
        ArrayList<Vec3d> circle = LightParticlesUtil.getCircle(pos, 2, 8);
        ServerWorld world = (ServerWorld) player.getWorld();
        for(Vec3d column_pos : circle){
            world.spawnParticles(player, ParticleTypes.END_ROD, false, column_pos.x, pos.y, column_pos.z, 100, 0, 1.10, 0, 0.01);
        }
        world.spawnParticles(player, ParticleTypes.FLASH, false, pos.x, pos.y, pos.z, 3, 0, 0, 0, 0.01);

    }

    public static void spawnColumn(ServerPlayerEntity player, ParticleEffect particle, Vec3d column_pos){
        ServerWorld world = (ServerWorld) player.getWorld();
        world.spawnParticles(player, particle, true, column_pos.x, column_pos.y, column_pos.z, 100, 0.1, 1.10, 0.1, 0.01);
    }

    private static int column_ticks = 0;
    private static boolean stop_column = true;
    public static void spawnDescendingColumn(ServerPlayerEntity player, ParticleEffect particle, Vec3d column_pos){
        stop_column = false;
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if(stop_column){
                return;
            }
            column_ticks++;
            ServerWorld world = (ServerWorld) player.getWorld();
            world.spawnParticles(player, particle, true, column_pos.getX(), column_pos.getY()-0.1, column_pos.getZ(), 100, 0.1, 1.10, 0.1, 0);
            if(column_ticks == 20){
                column_ticks = 0;
                stop_column = true;
            }
        });
    }

    public static ArrayList<Vec3d> getCircle(Vec3d center, double radius, int amount) {
        double increment = (2 * Math.PI) / amount;
        ArrayList<Vec3d> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Vec3d(x, center.getY(), z));
        }
        return locations;
    }

    public static void spawnCircle(Vec3d center, double radius, int amount, ParticleEffect particle, ServerWorld world){
        for(Vec3d pos : getCircle(center, radius, amount)){
            world.spawnParticles(particle,
                    pos.getX(), pos.getY(), pos.getZ(), 1,
                    0, 0, 0, 0);
        }
    }

    public static void spawnCylinder(Vec3d center, double radius, int amount, double height, double distance, ParticleEffect particle, ServerWorld world){
        Vec3d c = center;
        for(double i = 0; i < height; i = i+distance){
            c = c.add(0, distance, 0);
            spawnCircle(center, radius, amount, particle, world);
        }
    }



    /*public static void spawnRedstoneParticles(World world, BlockPos pos){
        double d = 0.5625D;
        Random random = world.random;
        Direction[] var5 = Direction.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Direction direction = var5[var7];
            BlockPos blockPos = pos.offset(direction);
            if (!world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) {
                Direction.Axis axis = direction.getAxis();
                double e = axis == Direction.Axis.X ? 0.5D + 0.5625D * (double)direction.getOffsetX() : (double)random.nextFloat();
                double f = axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double)direction.getOffsetY() : (double)random.nextFloat();
                double g = axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double)direction.getOffsetZ() : (double)random.nextFloat();
                world.addParticle(DustParticleEffect.DEFAULT, (double)pos.getX() + e, (double)pos.getY() + f, (double)pos.getZ() + g, 0.0D, 0.0D, 0.0D);
            }
        }
    }*/

    /*public static void spawnEffectParticles(LivingEntity entity){
        Vec3d pos = entity.getPos();
        for(int i = 0; i<200; i++){
            entity.getWorld().addParticle(new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(43758).toVec3f()), 1.0F), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 0.2D, 0.2D, 0.2D);
        }
    }*/

    /*public static void spawnDustParticles(ServerPlayerEntity player, Vec3f color){

        DustParticleEffect a = new DustParticleEffect(new Vec3f(0, 255, 255), 2);
        Vec3d pos = player.getPos();
        double speed = 0;
        ServerWorld world = (ServerWorld) player.getWorld();
        //world.spawnParticles(player, new Vec3f(Vec3d.unpackRgb(43758)), false, pos.x, pos.y, pos.z, 100, 0, 0, 0, speed);
        world.addParticle(new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(43758).toVec3f()), 1.0F), pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);

    }*/

    /**Spawns yellow and light blue particles that go from about the head of the player to
     * the art in a small vorex*/
    public static void spawnLightBottledUpEffect(ServerPlayerEntity player){
        //Blueish color: 79f2de Yellowish color: f4f44b

        Vec3f YELLOW = new Vec3f(Vec3d.unpackRgb(16053323));
        Vec3f BLUE = new Vec3f(Vec3d.unpackRgb(7992030));
        DustParticleEffect yellow = new DustParticleEffect(YELLOW, 0.7f);
        DustParticleEffect light_blue = new DustParticleEffect( BLUE, 0.6f);

        VortexEffect vortexYellow = VortexEffect.builder(player.getWorld(), yellow, player.getPos())
                .yaw(90).pitch(-90f).radius(0.3f).radiusGrow(0.008f).lengthGrow(0.02f)
                .entityOrigin(player).originOffset(new Vec3d(.0, 0.6, .0))
                .updatePositions(true).shouldUpdateYPR(false)
                .circles(3).helixes(1).radials(5)
                .inverted(true).build();

        double runFor = 1;
        vortexYellow.runFor(runFor);
        VortexEffect vortexBlue = VortexEffect.builder(player.getWorld(), yellow, player.getPos()).build();
        VortexEffect.copy(vortexYellow, vortexBlue);
        vortexBlue.setParticle(light_blue);
        vortexBlue.setStartRange(3.14f);
        vortexBlue.runFor(runFor);

    }

    /**Spawns the particles for gaining a light charge, like when a BottledLight is used*/
    public static void spawnChargedParticles(ServerPlayerEntity user){
        VortexEffect vortexEffect = VortexEffect.builder((ServerWorld) user.getWorld(), LightParticles.LIGHT_PARTICLE, user.getPos())
                .helixes(2).yaw(90).pitch(-90)
                .entityOrigin(user).updatePositions(true).shouldUpdateYPR(false)
                .circles(1).radiusGrow(0).lengthGrow(0.065f)
                .radius(0.7f).radials(0.35)
                .build();
        vortexEffect.runFor(1.5);
        ((ServerWorld) user.getWorld()).spawnParticles(ParticleTypes.FLASH, user.getX(), user.getY()+0.5, user.getZ(), 1,0,0,0,1);

    }


}
