package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.util.Color;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class InnerLight {

    protected List<LivingEntity> targets = new ArrayList<>();
    protected double cooldown_time;
    protected int power_multiplier;
    protected int duration;
    protected Color color;
    protected LivingEntity caster;
    protected boolean rainbow_col;
    protected InnerLightTypes type = InnerLightTypes.GENERIC;

    public void execute(){
    }

    InnerLight(List<LivingEntity> targets, double cooldown_time, int power_multiplier, int duration, Color color, LivingEntity caster, boolean rainbow_col){

    }

    InnerLight(List<LivingEntity> targets, double cooldown_time, int power_multiplier, int duration, LivingEntity caster){

    }
}
