package me.emafire003.dev.lightwithin.particles;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LightParticlesUtil {

    public static void spawnDefaultLightParticleSequence(ServerPlayerEntity player){
        spawnLightCircle(player);
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
        for(Vec3d column_pos : circle){
            player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, column_pos.x, pos.y, column_pos.z, 100, 0, 1.10, 0, 0.01);
        }
        player.getWorld().spawnParticles(player, ParticleTypes.FLASH, false, pos.x, pos.y, pos.z, 3, 0, 0, 0, 0.01);

    }

    public static void spawnColumn(ServerPlayerEntity player, ParticleEffect particle, Vec3d column_pos){
        player.sendMessage(Text.literal("hello spawning coloumn, "));
        player.getWorld().spawnParticles(player, particle, true, column_pos.x, column_pos.y, column_pos.z, 100, 0.1, 1.10, 0.1, 0.01);
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
            player.getWorld().spawnParticles(player, particle, true, column_pos.getX(), column_pos.getY()-0.1, column_pos.getZ(), 100, 0.1, 1.10, 0.1, 0);
            if(column_ticks == 20){
                column_ticks = 0;
                stop_column = true;
            }
        });
    }

    public static ArrayList<Vec3d> getCircle(Vec3d center, double radius, int amount) {
        double increment = (2 * Math.PI) / amount;
        ArrayList<Vec3d> locations = new ArrayList<Vec3d>();
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

    public static void spawnEffectParticles(LivingEntity entity){
        Vec3d pos = entity.getPos();
        for(int i = 0; i<200; i++){
            entity.getWorld().addParticle(new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(43758)), 1.0F), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 0.2D, 0.2D, 0.2D);
        }
    }

    /*public static void spawnDustParticles(ServerPlayerEntity player){
        Vec3d pos = player.getPos();
        double speed = 0;
        //player.getWorld().spawnParticles(player, new Vec3f(Vec3d.unpackRgb(43758)), false, pos.x, pos.y, pos.z, 100, 0, 0, 0, speed);
        player.getWorld().addParticle(new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(43758)), 1.0F), pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
    }*/

    //Generated using ParticleConverter. This is done not only because for me it's easier but also because it doesn't need to calculate each team the position of hunderds of particles.+
    //So it's it's like it has been compiled, performing better
    @SuppressWarnings("all")
    public static void spawnLightCircle(@NotNull ServerPlayerEntity player){
        Vec3d pos = player.getPos();
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.4, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+-0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+-0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+-0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+-0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+-0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.3333333, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2666667, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2666667, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2666667, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2666667, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2666667, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2666667, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2666667, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2, pos.y+0, pos.z+-0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2, pos.y+0, pos.z+0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.2, pos.y+0, pos.z+0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.1333333, pos.y+0, pos.z+-1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.1333333, pos.y+0, pos.z+-0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.1333333, pos.y+0, pos.z+1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.1333333, pos.y+0, pos.z+1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-1.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+-0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2.0666667, pos.y+0, pos.z+1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2, pos.y+0, pos.z+-1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2, pos.y+0, pos.z+-1.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2, pos.y+0, pos.z+1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2, pos.y+0, pos.z+1.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2, pos.y+0, pos.z+1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+2, pos.y+0, pos.z+1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.9333333, pos.y+0, pos.z+-1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.9333333, pos.y+0, pos.z+-1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.9333333, pos.y+0, pos.z+1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.9333333, pos.y+0, pos.z+1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.9333333, pos.y+0, pos.z+1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8666667, pos.y+0, pos.z+-1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8666667, pos.y+0, pos.z+-1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8666667, pos.y+0, pos.z+-1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8666667, pos.y+0, pos.z+1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8666667, pos.y+0, pos.z+1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8666667, pos.y+0, pos.z+1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8, pos.y+0, pos.z+-1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8, pos.y+0, pos.z+-1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8, pos.y+0, pos.z+-1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8, pos.y+0, pos.z+1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.8, pos.y+0, pos.z+1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.7333333, pos.y+0, pos.z+-1.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.7333333, pos.y+0, pos.z+1.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.6666667, pos.y+0, pos.z+-1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.6666667, pos.y+0, pos.z+1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.6666667, pos.y+0, pos.z+1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.6, pos.y+0, pos.z+-1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.6, pos.y+0, pos.z+-1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.6, pos.y+0, pos.z+-0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.6, pos.y+0, pos.z+0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.6, pos.y+0, pos.z+1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.5333333, pos.y+0, pos.z+-1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.5333333, pos.y+0, pos.z+-1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.5333333, pos.y+0, pos.z+-0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.5333333, pos.y+0, pos.z+0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.5333333, pos.y+0, pos.z+1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.5333333, pos.y+0, pos.z+1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4666667, pos.y+0, pos.z+-1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4666667, pos.y+0, pos.z+-1.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4666667, pos.y+0, pos.z+-0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4666667, pos.y+0, pos.z+0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4666667, pos.y+0, pos.z+1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4666667, pos.y+0, pos.z+1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4666667, pos.y+0, pos.z+1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4, pos.y+0, pos.z+-1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4, pos.y+0, pos.z+1.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.4, pos.y+0, pos.z+1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.3333333, pos.y+0, pos.z+-1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.3333333, pos.y+0, pos.z+-0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.3333333, pos.y+0, pos.z+1.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2666667, pos.y+0, pos.z+-2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2666667, pos.y+0, pos.z+-1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2666667, pos.y+0, pos.z+-1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2666667, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2666667, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2666667, pos.y+0, pos.z+1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2666667, pos.y+0, pos.z+2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2, pos.y+0, pos.z+-1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2, pos.y+0, pos.z+1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.2, pos.y+0, pos.z+2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.1333333, pos.y+0, pos.z+-1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.1333333, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.1333333, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.1333333, pos.y+0, pos.z+2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.0666667, pos.y+0, pos.z+-2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.0666667, pos.y+0, pos.z+-1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.0666667, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1.0666667, pos.y+0, pos.z+2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1, pos.y+0, pos.z+-2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+1, pos.y+0, pos.z+1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.9333333, pos.y+0, pos.z+-2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.9333333, pos.y+0, pos.z+-1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.9333333, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.9333333, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.9333333, pos.y+0, pos.z+1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8666667, pos.y+0, pos.z+-2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8666667, pos.y+0, pos.z+-1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8666667, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8666667, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8666667, pos.y+0, pos.z+1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8666667, pos.y+0, pos.z+2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8, pos.y+0, pos.z+-1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.8, pos.y+0, pos.z+2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+-2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+-0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+-0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+-0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.7333333, pos.y+0, pos.z+2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6666667, pos.y+0, pos.z+-2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6666667, pos.y+0, pos.z+-0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6666667, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6666667, pos.y+0, pos.z+-0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6666667, pos.y+0, pos.z+0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6666667, pos.y+0, pos.z+0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6666667, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6666667, pos.y+0, pos.z+2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6666667, pos.y+0, pos.z+2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+-0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+-0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+-0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.6, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+-2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+-0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.5333333, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4666667, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4666667, pos.y+0, pos.z+-2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4666667, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4666667, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4666667, pos.y+0, pos.z+-0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4666667, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4666667, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4666667, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4666667, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4, pos.y+0, pos.z+-2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.4, pos.y+0, pos.z+2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.3333333, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.3333333, pos.y+0, pos.z+-2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.3333333, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.3333333, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.3333333, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.3333333, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.3333333, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.3333333, pos.y+0, pos.z+2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2666667, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2666667, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2666667, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2666667, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2666667, pos.y+0, pos.z+2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.2, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.1333333, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.1333333, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.1333333, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.1333333, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.1333333, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.0666667, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.0666667, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.0666667, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0.0666667, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-2.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+0, pos.y+0, pos.z+2.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.0666667, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.0666667, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.0666667, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.0666667, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.1333333, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.1333333, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.1333333, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.1333333, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.1333333, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2, pos.y+0, pos.z+2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2666667, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2666667, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2666667, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2666667, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2666667, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.2666667, pos.y+0, pos.z+2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.3333333, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.3333333, pos.y+0, pos.z+-2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.3333333, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.3333333, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.3333333, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.3333333, pos.y+0, pos.z+2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4, pos.y+0, pos.z+-2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x - 0.4, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+-2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+-2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+-0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.4666667, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+-2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+-0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.5333333, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+-0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+-0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+-0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6666667, pos.y+0, pos.z+-2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6666667, pos.y+0, pos.z+-2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6666667, pos.y+0, pos.z+-0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6666667, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6666667, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6666667, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6666667, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6666667, pos.y+0, pos.z+2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.6666667, pos.y+0, pos.z+2.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.7333333, pos.y+0, pos.z+-2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.7333333, pos.y+0, pos.z+-0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.7333333, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.7333333, pos.y+0, pos.z+-0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.7333333, pos.y+0, pos.z+0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.7333333, pos.y+0, pos.z+0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.7333333, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.7333333, pos.y+0, pos.z+2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.7333333, pos.y+0, pos.z+2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8, pos.y+0, pos.z+-1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8, pos.y+0, pos.z+1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8, pos.y+0, pos.z+2.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8666667, pos.y+0, pos.z+-2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8666667, pos.y+0, pos.z+-1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8666667, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8666667, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8666667, pos.y+0, pos.z+1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.8666667, pos.y+0, pos.z+2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.9333333, pos.y+0, pos.z+-2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.9333333, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.9333333, pos.y+0, pos.z+1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-0.9333333, pos.y+0, pos.z+2.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1, pos.y+0, pos.z+-2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1, pos.y+0, pos.z+-1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1, pos.y+0, pos.z+1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.0666667, pos.y+0, pos.z+-2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.0666667, pos.y+0, pos.z+-2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.0666667, pos.y+0, pos.z+-1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.0666667, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.0666667, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.0666667, pos.y+0, pos.z+1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.0666667, pos.y+0, pos.z+2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.1333333, pos.y+0, pos.z+-2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.1333333, pos.y+0, pos.z+-1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.1333333, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.1333333, pos.y+0, pos.z+2.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.2, pos.y+0, pos.z+-1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.2, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.2, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.2, pos.y+0, pos.z+1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.2, pos.y+0, pos.z+2.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.2666667, pos.y+0, pos.z+-2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.2666667, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.2666667, pos.y+0, pos.z+1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.2666667, pos.y+0, pos.z+2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.3333333, pos.y+0, pos.z+-2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.3333333, pos.y+0, pos.z+-1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.3333333, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.3333333, pos.y+0, pos.z+1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.3333333, pos.y+0, pos.z+2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.4, pos.y+0, pos.z+-1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.4, pos.y+0, pos.z+-1.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.4, pos.y+0, pos.z+-0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.4, pos.y+0, pos.z+0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.4, pos.y+0, pos.z+1.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.4, pos.y+0, pos.z+1.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.4666667, pos.y+0, pos.z+-1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.4666667, pos.y+0, pos.z+0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.4666667, pos.y+0, pos.z+1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.5333333, pos.y+0, pos.z+-1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.5333333, pos.y+0, pos.z+-1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.5333333, pos.y+0, pos.z+-0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.5333333, pos.y+0, pos.z+0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.5333333, pos.y+0, pos.z+1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.5333333, pos.y+0, pos.z+1.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.5333333, pos.y+0, pos.z+1.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.6, pos.y+0, pos.z+-1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.6, pos.y+0, pos.z+-0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.6, pos.y+0, pos.z+1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.6666667, pos.y+0, pos.z+-1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.6666667, pos.y+0, pos.z+-0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.6666667, pos.y+0, pos.z+1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.6666667, pos.y+0, pos.z+1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.6666667, pos.y+0, pos.z+1.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.7333333, pos.y+0, pos.z+-1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.7333333, pos.y+0, pos.z+-1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.7333333, pos.y+0, pos.z+1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.7333333, pos.y+0, pos.z+1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.7333333, pos.y+0, pos.z+1.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8, pos.y+0, pos.z+-1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8, pos.y+0, pos.z+-1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8, pos.y+0, pos.z+-1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8, pos.y+0, pos.z+1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8, pos.y+0, pos.z+1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8666667, pos.y+0, pos.z+-1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8666667, pos.y+0, pos.z+-1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8666667, pos.y+0, pos.z+-1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8666667, pos.y+0, pos.z+1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8666667, pos.y+0, pos.z+1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8666667, pos.y+0, pos.z+1.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.8666667, pos.y+0, pos.z+1.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.9333333, pos.y+0, pos.z+-1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.9333333, pos.y+0, pos.z+-1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.9333333, pos.y+0, pos.z+-1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.9333333, pos.y+0, pos.z+1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.9333333, pos.y+0, pos.z+1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-1.9333333, pos.y+0, pos.z+1.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2, pos.y+0, pos.z+-1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2, pos.y+0, pos.z+-1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2, pos.y+0, pos.z+1.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2, pos.y+0, pos.z+1.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-1.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+-0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.2666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.3333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.4, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+1, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.0666667, pos.y+0, pos.z+1.2, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.1333333, pos.y+0, pos.z+-1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.1333333, pos.y+0, pos.z+1.0666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.1333333, pos.y+0, pos.z+1.1333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.2, pos.y+0, pos.z+-0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.2, pos.y+0, pos.z+0.8666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.2, pos.y+0, pos.z+0.9333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.2666667, pos.y+0, pos.z+-0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.2666667, pos.y+0, pos.z+-0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.2666667, pos.y+0, pos.z+-0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.2666667, pos.y+0, pos.z+0.7333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.2666667, pos.y+0, pos.z+0.8, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.3333333, pos.y+0, pos.z+-0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.3333333, pos.y+0, pos.z+-0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.3333333, pos.y+0, pos.z+-0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.3333333, pos.y+0, pos.z+0.4666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.3333333, pos.y+0, pos.z+0.5333333, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.3333333, pos.y+0, pos.z+0.6, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.3333333, pos.y+0, pos.z+0.6666667, 1, 0, 0, 0, 0);
        player.getWorld().spawnParticles(player, ParticleTypes.END_ROD, false, pos.x+-2.4, pos.y+0, pos.z+0, 1, 0, 0, 0, 0);

    }
}
