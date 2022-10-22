package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class LightFatigueEffect extends StatusEffect {

    //LORE: Basicly your light after being used decays and well it needs to rechange so you can't use it for a while
    //it's a cool way to make a cooldown visible for the player too. As lot's have said, it's not a bug it's a feature
    //just look at it the right way
    //xD
    //TODO mixin into the GlowingEffect and make it so it can clear the CGLCompat.getLib() color

    public LightFatigueEffect() {
        super(StatusEffectCategory.HARMFUL, 0x9EC1BE);
    }

    private String former_color = "ffffff";
    private boolean rainbow;

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            former_color = CGLCompat.toHex(CGLCompat.getLib().getEntityColor(entity));
            rainbow = CGLCompat.getLib().getEntityRainbowColor(entity);
            if(rainbow){
                CGLCompat.getLib().setRainbowColorToEntity(entity, false);
            }
            CGLCompat.getLib().setColorToEntity(entity, CGLCompat.fromHex("9EC1BE"));
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        if(former_color != null ){
            if(!former_color.equals("ffffff")){
                CGLCompat.getLib().setColorToEntity(entity, CGLCompat.fromHex(former_color));
            }
            if(rainbow){
                CGLCompat.getLib().setRainbowColorToEntity(entity, true);
            }
        }
    }
}
