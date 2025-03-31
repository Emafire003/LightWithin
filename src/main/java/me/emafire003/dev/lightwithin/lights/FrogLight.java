package me.emafire003.dev.lightwithin.lights;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;
import static me.emafire003.dev.lightwithin.LightWithin.getBoxExpansionAmount;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendLightTriggered;

public class FrogLight extends InnerLight {

    public static final List<Item> INGREDIENTS = List.of(Items.VERDANT_FROGLIGHT, Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT);

    private final List<TargetType> possibleTargetTypes = Arrays.asList(TargetType.ALL);
    private final List<TriggerChecks> triggerChecks = List.of(TriggerChecks.ENTITY_ATTACKED);
    private final Identifier lightId = LightWithin.getIdentifier("frog");


    /**
     * Creates an instance of this InnerLight. Remember to register it!
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     */
    public FrogLight(TypeCreationRegex regex) {
        super(regex);
    }

    /*Possible triggers:
       - self low health
       - allies low health
       - surrounded+++
       - NEEDS to have in hand dirt/rock or be around them.
     */

    /*Possible targets:
    * - self, -> dash away + enemis pushed away/high velocity and jump
    * - ally/self -> launch up in the air and give jump boost velocity and
    * - ALL MAYBE, but not sure. -> everything/one boosted away*/

    @Override
    public List<TargetType> getPossibleTargetTypes() {
        return possibleTargetTypes;
    }

    @Override
    public List<TriggerChecks> getTriggerChecks() {
        return triggerChecks;
    }

    @Override
    public Item getIngredient() {
        return INGREDIENT;
    }

    @Override
    public Identifier getLightId() {
        return lightId;
    }


    @Override
    protected Pair<Double, Integer> checkSafety(double power_multiplier, int duration) {
        LOGGER.info("Oh a frog easter egg light has been activated!");
        return null;
    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        List<LivingEntity> targets = new ArrayList<>(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(getBoxExpansionAmount()), (entity1 -> true)));
        player.sendMessage(Text.translatable("light.description.activation.frog"), true);
        activate(player, targets, component.getPowerMultiplier(), component.getDuration(), component.getMaxCooldown());
    }

    @Override
    protected void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time) {
        super.activate(caster, targets, power_multiplier, duration, cooldown_time);
        checkSafety(power_multiplier, duration);
        Random random = caster.getRandom();
        int frogs = (int) (power_multiplier+random.nextBetween(0, 5));
        LightParticlesUtil.spawnLightTypeParticle(LightParticles.FROGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), SoundEvents.ENTITY_FROG_HURT, SoundCategory.PLAYERS, 1, 0.8f);

        for(int i = 0; i<frogs; i++){
            for(LivingEntity target : targets){
                caster.getWorld().playSound(null, BlockPos.ofFloored(target.getPos()), SoundEvents.ENTITY_FROG_HURT, SoundCategory.PLAYERS, 1, 0.8f);

                if(!target.isSpectator()){
                    target.damage(caster.getWorld().getDamageSources().magic(), frogs);
                }
                if(target instanceof PlayerEntity){
                    ((PlayerEntity) target).sendMessage(Text.translatable("light.description.activation.frog.damage"), true);
                }
            }
            FrogEntity frog = new FrogEntity(EntityType.FROG, caster.getWorld());
            RegistryKey<FrogVariant> variant = FrogVariant.TEMPERATE;
            int v = random.nextBetween(0, 2);
            if(v == 0){
                variant = FrogVariant.COLD;
            }else if(v == 1){
                variant = FrogVariant.WARM;
            }
            frog.setVariant(Registries.FROG_VARIANT.entryOf(variant));
            frog.setPos(caster.getX()+random.nextDouble(), caster.getY()+2, caster.getZ()+random.nextDouble());
            caster.getWorld().spawnEntity(frog);
        }

    }

    @Override
    public void triggerCheck(PlayerEntity player, LightComponent component, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+25)
                || CheckUtils.checkSurrounded(player)
        ){
            sendLightTriggered((ServerPlayerEntity) player);
        }
        else if(target instanceof FrogEntity){
            sendLightTriggered((ServerPlayerEntity) player);
        }else if(attacker instanceof FrogEntity){
            sendLightTriggered((ServerPlayerEntity) player);
        }
    }

    @Override
    public String toString() {
        return this.lightId.getPath();
    }
}
