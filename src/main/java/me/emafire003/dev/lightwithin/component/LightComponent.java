package me.emafire003.dev.lightwithin.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightTypes;
import me.emafire003.dev.lightwithin.util.TargetTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightComponent implements ComponentV3, AutoSyncedComponent {

    protected TargetTypes targets =  TargetTypes.NONE;
    protected double cooldown_time = -1;
    protected double power_multiplier = -1;
    protected int duration = -1;
    protected Color color = Color.getWhiteColor();
    protected boolean rainbow_col = false;
    private PlayerEntity caster;
    protected InnerLightTypes type = InnerLightTypes.NONE;

    public LightComponent(PlayerEntity playerEntity) {
        this.caster = playerEntity;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if(tag.contains("type")){
            LOGGER.info("the type got: " + tag.getString("type"));
            this.type = InnerLightTypes.valueOf(tag.getString("type"));
        }else{
            this.type = InnerLightTypes.NONE;
        }

        if(tag.contains("targets")){
            LOGGER.info("the targets got: " + tag.getString("targets"));
            this.targets = TargetTypes.valueOf(tag.getString("targets"));
        }else{
            this.targets = TargetTypes.NONE;
        }

        if(tag.contains("cooldown_time")){
            LOGGER.info("the cooldown got: " + tag.getDouble("cooldown_time"));
            this.cooldown_time = tag.getDouble("cooldown_time");
        }else{
            this.cooldown_time = -1;
        }

        if(tag.contains("power_multiplier")){
            LOGGER.info("the power got: " + tag.getDouble("power_multiplier"));
            this.power_multiplier = tag.getDouble("power_multiplier");
        }else{
            this.power_multiplier = -1;
        }

        if(tag.contains("duration")){
            LOGGER.info("the cduration got: " + tag.getInt("duration"));
            this.duration= tag.getInt("duration");
        }else{
            this.duration = -1;
        }

        if(tag.contains("color")){
            LOGGER.info("the color got: " + tag.getString("color"));
            this.color = Color.translateFromHEX(tag.getString("color"));
        }else{
            this.color = Color.getWhiteColor();
        }

        if(tag.contains("rainbow_col")){
            LOGGER.info("the rainbow_col got: " + tag.getBoolean("rainbow_col"));
            this.rainbow_col = tag.getBoolean("rainbow_col");
        }else{
            this.rainbow_col = false;
        }

    }

    //TODO search when/how is this triggered.
    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString("type", this.type.toString());
        tag.putString("targets", this.targets.toString());
        tag.putDouble("cooldown_time", this.cooldown_time);
        tag.putDouble("power_multiplier", this.power_multiplier);
        tag.putInt("duration", this.duration);
        tag.putString("color", this.color.toHEX());
        tag.putBoolean("rainbow_col", this.rainbow_col);
    }


    public InnerLightTypes getType() {
        return this.type;
    }

    public TargetTypes getTargets() {
        return this.targets;
    }

    public double getCooldown() {
        return this.cooldown_time;
    }

    public double getPowerMultiplier() {
        return this.power_multiplier;
    }

    public int getDuration() {
        return this.duration;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean getRainbow() {
        return this.rainbow_col;
    }

    public void setType(InnerLightTypes type) {
        this.type = type;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setTargets(TargetTypes targets) {
        this.targets = targets;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setCooldown(double cooldown) {
        this.cooldown_time = cooldown;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setPowerMultiplier(double power) {
        this.power_multiplier = power;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setDuration(int duration) {
        this.duration = duration;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setColor(Color color) {
        this.color = color;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setRainbow(boolean b) {
        this.rainbow_col = b;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setAll(InnerLightTypes type, TargetTypes targets, double cooldown, double power, int duration, Color color, boolean b){
        this.type = type;
        this.targets = targets;
        this.cooldown_time = cooldown;
        this.power_multiplier = power;
        this.duration = duration;
        this.color = color;
        this.rainbow_col = b;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

}
