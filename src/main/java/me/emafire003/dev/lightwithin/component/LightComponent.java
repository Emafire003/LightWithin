package me.emafire003.dev.lightwithin.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightComponent implements ComponentV3, AutoSyncedComponent {

    protected TargetType targets =  TargetType.NONE;
    protected int cooldown_time = -1;
    protected double power_multiplier = -1;
    protected int duration = -1;
    protected String color = "ffffff";
    protected String prev_color = "ffffff";
    protected boolean rainbow_col = false;
    private PlayerEntity caster;
    protected InnerLightType type = InnerLightType.NONE;
    protected boolean incooldown = false;
    private final boolean debug = false;
    protected int max_increment_percent = -1;

    public LightComponent(PlayerEntity playerEntity) {
        this.caster = playerEntity;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if(tag.contains("type")){
            if(debug){
                LOGGER.info("the type got: " + tag.getString("type"));
            }
            this.type = InnerLightType.valueOf(tag.getString("type"));
        }else{
            this.type = InnerLightType.NONE;
        }

        if(tag.contains("targets")){
            if(debug){LOGGER.info("the targets got: " + tag.getString("targets"));}
            this.targets = TargetType.valueOf(tag.getString("targets"));
        }else{
            this.targets = TargetType.NONE;
        }

        if(tag.contains("cooldown_time")){
            if(debug){LOGGER.info("the cooldown got: " + tag.getDouble("cooldown_time"));}
            this.cooldown_time = tag.getInt("cooldown_time");
        }else{
            this.cooldown_time = -1;
        }

        if(tag.contains("power_multiplier")){
            if(debug){LOGGER.info("the power got: " + tag.getDouble("power_multiplier"));}
            this.power_multiplier = tag.getDouble("power_multiplier");
        }else{
            this.power_multiplier = -1;
        }

        if(tag.contains("duration")){
            if(debug){LOGGER.info("the cduration got: " + tag.getInt("duration"));}
            this.duration= tag.getInt("duration");
        }else{
            this.duration = -1;
        }

        if(tag.contains("color")){
            if(debug){LOGGER.info("the color got: " + tag.getString("color"));}
            this.color = tag.getString("color");
        }else{
            this.color = "ffffff";
        }

        if(tag.contains("prev_color")){
            if(debug){LOGGER.info("the prev_color got: " + tag.getString("prev_color"));}
            this.prev_color = tag.getString("prev_color");
        }else{
            this.prev_color = "ffffff";
        }

        if(tag.contains("rainbow_col")){
            if(debug){LOGGER.info("the rainbow_col got: " + tag.getBoolean("rainbow_col"));}
            this.rainbow_col = tag.getBoolean("rainbow_col");
        }else{
            this.rainbow_col = false;
        }

        if(tag.contains("incooldown")){
            if(debug){LOGGER.info("the incooldown got: " + tag.getBoolean("incooldown"));}
            this.incooldown = tag.getBoolean("incooldown");
        }else{
            this.incooldown = false;
        }

        if(tag.contains("max_increment")){
            if(debug){LOGGER.info("the max_increement got: " + tag.getInt("max_increment"));}
            this.max_increment_percent= tag.getInt("max_increment");
        }else{
            this.max_increment_percent= -1;
        }

    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString("type", this.type.toString());
        tag.putString("targets", this.targets.toString());
        tag.putDouble("cooldown_time", this.cooldown_time);
        tag.putDouble("power_multiplier", this.power_multiplier);
        tag.putInt("duration", this.duration);
        tag.putString("color", this.color);
        tag.putString("prev_color", this.prev_color);
        tag.putBoolean("rainbow_col", this.rainbow_col);
        tag.putBoolean("incooldown", this.incooldown);
        tag.putInt("max_increment", this.max_increment_percent);
    }


    public InnerLightType getType() {
        return this.type;
    }

    public TargetType getTargets() {
        return this.targets;
    }

    public int getMaxCooldown() {
        return this.cooldown_time;
    }

    public double getPowerMultiplier() {
        return this.power_multiplier;
    }

    public int getDuration() {
        return this.duration;
    }

    public String getColor() {
        return this.color;
    }

    public int getMaxIncrementPercent() {
        return this.max_increment_percent;
    }

    public String getPrevColor() {
        return this.color;
    }

    public boolean getRainbow() {
        return this.rainbow_col;
    }

    public boolean getInCooldown() {
        return incooldown;
    }

    public void setType(InnerLightType type) {
        this.type = type;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setTargets(TargetType targets) {
        this.targets = targets;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setMaxCooldown(int cooldown) {
        this.cooldown_time = cooldown;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setCooldown(int cooldown) {
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

    public void setColor(String color) {
        this.color = color;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setPrevColor(String color) {
        this.prev_color = color;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setRainbow(boolean b) {
        this.rainbow_col = b;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setInCooldown(boolean incooldown) {
        this.incooldown = incooldown;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setMaxIncrementPercent(int max_increment) {
        this.max_increment_percent = max_increment;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setAll(InnerLightType type, TargetType targets, int cooldown, double power, int duration, String color, boolean b, boolean incooldown, int max_increment){
        this.type = type;
        this.targets = targets;
        this.cooldown_time = cooldown;
        this.power_multiplier = power;
        this.duration = duration;
        this.color = color;
        this.rainbow_col = b;
        this.incooldown = incooldown;
        this.max_increment_percent = max_increment;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void clear(){
        this.targets =  TargetType.NONE;
        this.cooldown_time = -1;
        this.power_multiplier = -1;
        this.duration = -1;
        this.color = "ffffff";
        this.rainbow_col = false;
        this.type = InnerLightType.NONE;
        this.max_increment_percent = -1;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

}
