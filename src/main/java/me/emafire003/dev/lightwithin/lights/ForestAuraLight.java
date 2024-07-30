package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.forestaura_puffs.ForestPuffColor;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;

//TODO actually make it into forest light instead of the copy of aqua light

/*Planned stuff:
* Targets:
*  - SELF or ALLIES, which applies the forest aura effect (traversing natural blocks)
*  - ALL, which either makes all terrain difficult to traverse with spikes or similar
*       OR puffs of plant like stuff that makes player drunk an stuff like that.
* Maybe it could have like different effects dependig on the color of the puff and the power level.
*
*
* */
public class ForestAuraLight extends InnerLight {

    public static final Item INGREDIENT = Items.OAK_SAPLING;

    //TODO move into forest aura light class
    public static final TagKey<Block> FOREST_AURA_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "forest_aura_blocks"));

    public static final String COLOR = "1BC131";
    public static final String ENEMY_COLOR = "560d03";
    public static final String ALLY_COLOR = "2ee878";

    public ForestAuraLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.FOREST_AURA;
    }

    public ForestAuraLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.FOREST_AURA;
        color = COLOR; //TODO modify color
    }

    public ForestAuraLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.FOREST_AURA;
        //color = "#35f4d1";
        //TODO
        color = "forest_aura";
    }

    private void checkSafety(){
        if(this.power_multiplier > BalanceConfig.FOREST_AURA_MAX_POWER){
            power_multiplier = BalanceConfig.FOREST_AURA_MAX_POWER;
        }
        if(this.power_multiplier < BalanceConfig.FOREST_AURA_MIN_POWER){
            power_multiplier = BalanceConfig.FOREST_AURA_MIN_POWER;
        }
        int max_duration = BalanceConfig.FOREST_AURA_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.FOREST_AURA_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < BalanceConfig.FOREST_AURA_MIN_DURATION){
            this.duration = BalanceConfig.FOREST_AURA_MIN_DURATION;
        }
    }

    @Override
    public void execute(){
        checkSafety();
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColor(this.caster);
            }else{
                CGLCompat.getLib().setColor(this.caster, this.color);
            }
        }


        //caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.AQUA_LIGHT, SoundCategory.PLAYERS, 1, 1);
        LightComponent component = LIGHT_COMPONENT.get(caster);

        //TODO playsound spawn particles and such

        //ALL section (drowneds)
        if(component.getTargets().equals(TargetType.SELF)){
            //The -1 is because status effect levels start from 0
            caster.addStatusEffect(new StatusEffectInstance(LightEffects.FOREST_AURA, this.duration*20, (int) this.power_multiplier-1));

        }
        else if(component.getTargets().equals(TargetType.ALL)){
            //I think it should be 1-(2 +1 per level) puffs

            if(caster.getWorld().isClient()){
                return;
            }

            int bonus = 0;
            if(power_multiplier > 5){
                bonus = Math.max(1, (int) ((power_multiplier-5)/2));
            }
            int puffs = caster.getRandom().nextBetween(1+bonus, (int) (2+power_multiplier+bonus));
            int total_duration = duration;

            for(int i = 0; i < puffs; i++){
                //-(puffs-i)
                //TODO test out. Cool. doesn't work
                int puff_duration = caster.getRandom().nextBetween(5, 5+total_duration);
                total_duration = total_duration-(puff_duration-5);
                if(power_multiplier > 5){
                    //TODO if i ever modify the number of puff colors, edit here if needed!
                    int puff = caster.getRandom().nextBetween(0, COLOR_PUFFS.size()-1);
                    Vec3d pos = getRandomPos(caster, caster.getPos().add(0,2,0), 1.5);
                    if(pos == null){
                        //TODO remove?
                        caster.sendMessage(Text.literal("Position null!"));
                    }else{
                        //TODO play puff sound
                        //TODO maybe add a slight delay between each of them?
                        createForestPuff(caster, pos, (ServerWorld) caster.getWorld(), COLOR_PUFFS.get(puff), puff_duration, (int) power_multiplier);
                    }

                }
            }


            //The puffs mechanism.

            /* The duration might be equal to twice the duration of the caster, divided by the number of puffs
    This for the total duration of all the generated puffs.
    For each puff it's a random number between 1 second and the total duration-the number of puffs yet to create
     *
    * */

        }

    }


    /*First chunck of Puffs:
     * Level 0-5:
     * - GREEN
     * - PURPLE
     * - YELLOW
     * - PINK
     * Level 5-10
     * - BLUE
     * - RED
     * - BLACK
     * - ORANGE
     * */
    /**The other of the list matters! The first four elements can be spawned with a power level lower than 6,
     * the other ones require a power level of at least 6
     * */
    public static List<Integer> COLOR_PUFFS = List.of(
            //Up to level 5
            ForestPuffColor.GREEN, ForestPuffColor.YELLOW, ForestPuffColor.PURPLE, ForestPuffColor.PINK,
            //Available after power level 5
            ForestPuffColor.BLUE, ForestPuffColor.ORANGE, ForestPuffColor.BLACK, ForestPuffColor.RED);


    //TODO either change this or the other one
    public static double PUFF_BLOCK_RANGE = 1.5;

    private static final int max_tries = 10000;

    public static Vec3d getRandomPos(LivingEntity entity, Vec3d origin, double dist){
        Box box = new Box(origin.getX(), origin.getY(), origin.getZ(), (origin.getX() + 1), (origin.getY() + 1), (origin.getZ() + 1)).expand(dist);
        double i = origin.getX();
        double j = origin.getY();
        double k = origin.getZ();

        for(int l = 0; l<max_tries; l++){
            double m = i + MathHelper.nextDouble(entity.getRandom(), dist,  dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            double n = j + MathHelper.nextDouble(entity.getRandom(), dist,  dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            double o = k + MathHelper.nextDouble(entity.getRandom(), dist,  dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            Vec3d pos = new Vec3d(m,n,o);
            if(box.contains(pos)){
                return pos;
            }
        }
        LOGGER.error("Exceeded max tries to spawn a new puff, skipping");

        //TODO maybe remove
        entity.sendMessage(Text.literal("§cExceeded max tries to spawn a new puff, skipping"));
        return null;

    }

    /**
     * @param duration In seconds*/
    public void createForestPuff(LivingEntity caster, Vec3d origin, ServerWorld world, int color, int duration, int power){
        //Converts from ticks to seconds
        int dur = duration * 20;
        float size = 0.7f;

        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class,
                new Box(origin.getX(), origin.getY(), origin.getZ(), (origin.getX() + 1), (origin.getY() + 1), (origin.getZ() + 1)).expand(PUFF_BLOCK_RANGE),
                (entity -> (
                        //TODO remove after debug and uncomment the other
                        true
                        //!entity.equals(caster) && !CheckUtils.CheckAllies.checkAlly(caster, entity)
                )));


        if(color == ForestPuffColor.GREEN){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.GREEN_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, dur, power), caster));
        }else if(color == ForestPuffColor.YELLOW){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.YELLOW_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, dur, power), caster));
        }else if(color == ForestPuffColor.RED){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.RED_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_FATIGUE, dur, power), caster));
        }else if(color == ForestPuffColor.PINK){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.PINK_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, dur, power), caster));
        }else if(color == ForestPuffColor.BLUE){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.BLUE_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, dur, power), caster));
        }else if(color == ForestPuffColor.BLACK){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.BLACK_END).toVector3f(), size, world);
            targets.forEach(entity -> entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, dur, power), caster));
        }else if(color == ForestPuffColor.PURPLE){
            LightParticlesUtil.spawnForestPuff(origin, Vec3d.unpackRgb(color).toVector3f(), Vec3d.unpackRgb(ForestPuffColor.PURPLE_END).toVector3f(), size, world);
            //TODO the drunk effect! Or something similar (WIP).
            // Which could consist of moving randomly to one or the other side, inverted controls,
            // and the super secret settings thing. And maybe someh
            targets.forEach(entity -> entity.sendMessage(Text.literal("Ur drunk")));
        }
    }

}
