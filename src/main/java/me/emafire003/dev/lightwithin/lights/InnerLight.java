package me.emafire003.dev.lightwithin.lights;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class InnerLight {

    protected List<LivingEntity> targets = new ArrayList<>();
    protected double cooldown_time;
    protected double power_multiplier;
    protected int duration;
    protected String color;
    protected PlayerEntity caster;
    protected boolean rainbow_col;
    protected InnerLightType type = InnerLightType.NONE;

    public void execute(){
    }

    public InnerLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col){
        this.targets = targets;
        this.cooldown_time = cooldown_time;
        this.power_multiplier = power_multiplier;
        this.duration = duration;
        this.color = color;
        this.caster = caster;
        this.rainbow_col = rainbow_col;
    }

    public InnerLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col){
        this.targets = targets;
        this.cooldown_time = cooldown_time;
        this.power_multiplier = power_multiplier;
        this.duration = duration;
        this.caster = caster;
        this.rainbow_col = rainbow_col;
    }

    public InnerLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster){
        this.targets = targets;
        this.cooldown_time = cooldown_time;
        this.power_multiplier = power_multiplier;
        this.duration = duration;
        this.caster = caster;
    }


}
