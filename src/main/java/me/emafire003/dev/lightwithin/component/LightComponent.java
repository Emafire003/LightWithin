package me.emafire003.dev.lightwithin.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.DialogueProgressState;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.NoneLight;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class LightComponent implements ComponentV3, AutoSyncedComponent {

    public static final int CURRENT_VERSION = 3;

    /// This is going to be stored as the light's id in the component
    protected InnerLight type = new NoneLight();
    protected TargetType targets =  TargetType.NONE;
    protected int max_cooldown_time = -1;
    protected double power_multiplier = -1;
    protected int duration = -1;

    protected String prev_color = "ffffff";
    protected int max_increment_percent = -1;
    protected int max_light_charges = 1;
    protected int current_light_charges = 0;
    protected boolean has_triggered_naturally = false;

    //As in can the player use the light? Either in a specif time or place (needs to be set)
    protected boolean isLocked = Config.LIGHT_LOCKED_DEFAULT;
    protected int version = CURRENT_VERSION;

    //V3-V4 (cause of the alpha thingy on modrinth)
    protected List<DialogueProgressState> dialogueProgressStates = new ArrayList<>();

    private final PlayerEntity caster;

    public LightComponent(PlayerEntity playerEntity) {
        this.caster = playerEntity;
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void readFromNbt(NbtCompound tag) {
        boolean shouldWrite = false;

        boolean debug = false;
        if(tag.contains("type")){
            if(debug){
                LOGGER.info("the type got: " + tag.getString("type"));
            }

            if(tag.getInt("version") < 3){
                LOGGER.warn("Older LightComponent found, trying to parse type: " + MOD_ID+":"+tag.getString("type").toLowerCase());
                this.type = INNERLIGHT_REGISTRY.get(Identifier.tryParse(MOD_ID+":"+tag.getString("type").toLowerCase()));
                shouldWrite = true;
            }else{
                this.type = INNERLIGHT_REGISTRY.get(Identifier.tryParse(tag.getString("type")));
            }

        }else{
            this.type = new NoneLight();
        }

        if(tag.contains("targets")){
            if(debug){LOGGER.info("the targets got: " + tag.getString("targets"));}
            if(tag.getString("targets").equals("OTHER")){
                this.targets = TargetType.VARIANT;
            }else{
                this.targets = TargetType.valueOf(tag.getString("targets"));
            }
        }else{
            this.targets = TargetType.NONE;
        }

        if(tag.contains("cooldown_time")){
            if(debug){LOGGER.info("the cooldown got: " + tag.getDouble("cooldown_time"));}
            this.max_cooldown_time = tag.getInt("cooldown_time");
        }else{
            this.max_cooldown_time = -1;
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

        if(tag.contains("version")){
            if(debug){LOGGER.info("the version got: " + tag.getString("version"));}
            this.version = tag.getInt("version");
        }else{
            this.version = CURRENT_VERSION;
        }

        if(tag.contains("prev_color")){
            if(debug){LOGGER.info("the prev_color got: " + tag.getString("prev_color"));}
            this.prev_color = tag.getString("prev_color");
        }else{
            this.prev_color = "ffffff";
        }


        if(tag.contains("max_increment")){
            if(debug){LOGGER.info("the max_increement got: " + tag.getInt("max_increment"));}
            this.max_increment_percent= tag.getInt("max_increment");
        }else{
            this.max_increment_percent= -1;
        }

        if(tag.contains("max_light_stack")){
            if(debug){LOGGER.info("the max_light_stack got: " + tag.getInt("max_light_stack"));}
            this.max_light_charges = tag.getInt("max_light_stack");
        }else{
            this.max_light_charges = 1;
        }

        if(tag.contains("light_charges")){
            if(debug){LOGGER.info("the current light charges: " + tag.getInt("light_charges"));}
            this.current_light_charges = tag.getInt("light_charges");
        }else{
            this.current_light_charges = 0;
        }

        if(tag.contains("isLocked")){
            if(debug){LOGGER.info("the isLocked got: " + tag.getBoolean("isLocked"));}
            this.isLocked = tag.getBoolean("isLocked");
        }else{
            this.isLocked = Config.LIGHT_LOCKED_DEFAULT;
        }

        if(tag.contains("hasTriggeredNaturally")){
            if(debug){LOGGER.info("the hasTriggeredNaturally got: " + tag.getBoolean("hasTriggeredNaturally"));}
            this.has_triggered_naturally = tag.getBoolean("hasTriggeredNaturally");
        }else{
            this.has_triggered_naturally = false;
        }

        if(tag.contains("dialogueProgressStates")){
            if(debug){LOGGER.info("the dialogueProgressStates got: " + tag.getList("dialogueProgressStates", NbtElement.STRING_TYPE));}

            NbtList stringStates = tag.getList("dialogueProgressStates", NbtElement.STRING_TYPE);

            List<DialogueProgressState> stateList = new ArrayList<>();
            stringStates.forEach( nbtElement -> {
                if(DialogueProgressState.valueOf(nbtElement.asString()) != null){
                    stateList.add(DialogueProgressState.valueOf(nbtElement.asString()));
                }
            });
            this.dialogueProgressStates.clear();
            this.dialogueProgressStates.addAll(stateList);

        }else{
            this.has_triggered_naturally = false;
        }

        if(shouldWrite){
            this.writeToNbt(tag);
        }

    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        String typeId = "none";
        if(INNERLIGHT_REGISTRY.getId(this.type) != null){
            //noinspection DataFlowIssue
            typeId = INNERLIGHT_REGISTRY.getId(this.type).toString();
        }
        tag.putString("type", typeId);
        tag.putString("targets", this.targets.toString());
        tag.putDouble("cooldown_time", this.max_cooldown_time);
        tag.putDouble("power_multiplier", this.power_multiplier);
        tag.putInt("duration", this.duration);
        tag.putString("prev_color", this.prev_color);
        tag.putInt("max_increment", this.max_increment_percent);
        tag.putInt("max_light_stack", this.max_light_charges);
        tag.putInt("light_charges", this.current_light_charges);
        tag.putBoolean("isLocked", this.isLocked);
        tag.putBoolean("hasTriggeredNaturally", this.has_triggered_naturally);
        tag.putInt("version", this.version);

        NbtList diProgList = new NbtList();
        dialogueProgressStates.forEach( dialogueProgressState -> diProgList.add(NbtString.of(dialogueProgressState.toString())));
        tag.remove("dialogueProgressStates"); //clears it from the old stuff
        tag.put("dialogueProgressStates", diProgList);

    }


    public InnerLight getType() {
        return this.type;
    }

    public TargetType getTargets() {
        return this.targets;
    }

    public int getMaxCooldown() {
        return this.max_cooldown_time;
    }

    public double getPowerMultiplier() {
        return this.power_multiplier;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getMaxIncrementPercent() {
        return this.max_increment_percent;
    }
    public int getMaxLightCharges(){
        return this.max_light_charges;
    }
    public int getCurrentLightCharges(){
        return this.current_light_charges;
    }

    public String getPrevColor() {
        return this.prev_color;
    }


    public boolean getLocked() {
        return this.isLocked;
    }
    public boolean hasTriggeredNaturally() {
        if(Config.BYPASS_NATURAL_TRIGGER){
            return true;
        }
        return this.has_triggered_naturally;
    }

    public int getVersion() {
        return this.version;
    }

    public List<DialogueProgressState> getDialogueProgressStates() {
        return dialogueProgressStates;
    }

    public void setType(InnerLight type) {
        this.type = type;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setTargets(TargetType targets) {
        this.targets = targets;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setMaxCooldown(int cooldown) {
        this.max_cooldown_time = cooldown;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setLightCharges(int charges) {
        this.current_light_charges = charges;
        //This only goes towards the client and not the server uh
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void addLightCharges(int charges) {
        this.current_light_charges = this.current_light_charges + charges;
        //This only goes towards the client and not the server uh
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void removeLightCharges(int charges) {
        this.current_light_charges = this.current_light_charges - charges;
        //This only goes towards the client and not the server uh
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void removeLightCharges() {
        this.current_light_charges = this.current_light_charges-1;
        //This only goes towards the client and not the server uh
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

    public void setVersion(int ver) {
        this.version = ver;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setPrevColor(String color) {
        this.prev_color = color;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setIsLocked(boolean locked) {
        this.isLocked = locked;
        caster.sendMessage(Text.translatable("light.unlocked"));
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setTriggeredNaturally(boolean b) {
        this.has_triggered_naturally = b;
        caster.sendMessage(Text.translatable("light.triggered_naturally_first").formatted(Formatting.GREEN), true);
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setMaxIncrementPercent(int max_increment) {
        this.max_increment_percent = max_increment;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }


    public void setMaxLightStack(int max_stack) {
        this.max_light_charges = max_stack;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setDialogueProgressStates(List<DialogueProgressState> states){
        this.dialogueProgressStates.clear();
        this.dialogueProgressStates.addAll(states);
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void addDialogueProgressState(DialogueProgressState state){
        if(this.dialogueProgressStates.contains(state)){
            return;
        }
        this.dialogueProgressStates.add(state);
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void removeDialogueProgressState(DialogueProgressState state){
        this.dialogueProgressStates.remove(state);
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void setAll(InnerLight type, TargetType targets, int max_cooldown, double power, int duration, int max_increment, int max_light_stack, boolean locked, int version){
        this.type = type;
        this.targets = targets;
        this.max_cooldown_time = max_cooldown;
        this.power_multiplier = power;
        this.duration = duration;
        this.max_increment_percent = max_increment;
        this.max_light_charges = max_light_stack;
        this.isLocked = locked;
        this.version = version;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

    public void clear(){
        this.targets =  TargetType.NONE;
        this.max_cooldown_time = -1;
        this.power_multiplier = -1;
        this.duration = -1;
        //this.version = CURRENT_VERSION;
        this.type = new NoneLight();
        this.max_increment_percent = -1;
        this.max_light_charges = 1;
        this.isLocked = false;
        LightWithin.LIGHT_COMPONENT.sync(caster);
    }

}
