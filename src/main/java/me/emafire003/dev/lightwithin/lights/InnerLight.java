package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.util.Color;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class InnerLight {

    protected List<LivingEntity> targets = new ArrayList<>();
    protected double cooldown_time;
    protected double power_multiplier;
    protected int duration;
    protected Color color;
    protected LivingEntity caster;
    protected boolean rainbow_col;
    protected InnerLightTypes type = InnerLightTypes.GENERIC;

    public void execute(){
    }

    public InnerLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, LivingEntity caster, boolean rainbow_col){
        this.targets = targets;
        this.cooldown_time = cooldown_time;
        this.power_multiplier = power_multiplier;
        this.duration = duration;
        this.color = color;
        this.caster = caster;
        this.rainbow_col = rainbow_col;
    }

    public InnerLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster, boolean rainbow_col){
        this.targets = targets;
        this.cooldown_time = cooldown_time;
        this.power_multiplier = power_multiplier;
        this.duration = duration;
        this.caster = caster;
        this.rainbow_col = rainbow_col;
    }

    public InnerLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster){
        this.targets = targets;
        this.cooldown_time = cooldown_time;
        this.power_multiplier = power_multiplier;
        this.duration = duration;
        this.caster = caster;
    }
}
