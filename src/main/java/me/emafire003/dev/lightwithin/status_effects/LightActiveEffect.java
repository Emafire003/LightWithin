package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;

public class LightActiveEffect extends StatusEffect {

    //LORE: Your light after being used decays and well it needs to rechange so you can't use it for a while
    //it's a cool way to make a cooldown visible for the player too. As lot's have said, it's not a bug it's a feature
    //just look at it the right way
    //xD
    //TO.DO mixin into the GlowingEffect and make it so it can clear the CGLCompat.getLib() color
    //10.01.2024 I don't even know what I meant

    public LightActiveEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xF3FF28);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        //TODO why is this here?
        if(entity instanceof ServerPlayerEntity){
            LightComponent component = LIGHT_COMPONENT.get(entity);
            if(component.getType().equals(InnerLightType.WIND) && !component.getTargets().equals(TargetType.VARIANT)){

                ((ServerWorld) (entity).getWorld()).spawnParticles((ServerPlayerEntity) entity, ParticleTypes.CLOUD, false, entity.getX(), entity.getY(), entity.getZ(), 5, 0, 0, 0, 0.1);
            }
        }
    }

    //TODO after updating CGL to 3.0.0 I'll need to remove this most likely
    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        if(!entity.hasStatusEffect(StatusEffects.GLOWING)){
            entity.setGlowing(false);
        }
        LightComponent component = LIGHT_COMPONENT.get(entity);
        if(component.getPrevColor() != null && FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(component.getPrevColor() == null){
                CGLCompat.getLib().setColorToEntity(entity, CGLCompat.fromHex("#ffffff"));
            }else{
                CGLCompat.getLib().setColorToEntity(entity, CGLCompat.fromHex(component.getPrevColor()));
            }
        }
        if(entity instanceof PlayerEntity){
            entity.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_FATIGUE, (int) (Config.COOLDOWN_MULTIPLIER*20*component.getMaxCooldown()), 1));
        }
        super.onRemoved(entity, attributes, amplifier);
    }
}
