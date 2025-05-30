package me.emafire003.dev.lightwithin.lights;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is the base InnerLight class. You have to extended it and override (pretty much all of) its methods
 * to implement the functionality. (as well as the fields)
 * <p>
 * Particles and sounds are registered using the light's id on initialization, and the files should be named "{id}_light_particle.png" and "{id}_light.ogg" for sounds.
 * */
public abstract class InnerLight {

    ///  The color used for the ColoredGlowLib color, aka the outline/glowing effect/spectral glowy arrow thingy
    protected String color;
    /// This is the list of the possible target types for this light type. It is ordered, from the most common target type to the least common one.
    /// You can insert multiple target types of the same type to modify the chances of those target type to appear
    /// This must be overridden in the light's implementation
    protected final List<TargetType> possibleTargetTypes = new ArrayList<>();
    /// This is the item that is going to be used as an ingredient for this light type's {@link me.emafire003.dev.lightwithin.items.BottledLightItem}.
    /// Must be overridden in the light's implementation
    public final Item INGREDIENT = LightItems.LUXCOGNITA_BERRY;

    /// Defines which events start the checking chain  to see if a light is triggerable
    protected final List<TriggerChecks> triggerChecks = new ArrayList<>();

    /// This is here for convenience represents the identifier of the light. You should use this to register the light.
    /// Must override this in the implementation
    protected final Identifier lightId = LightWithin.getIdentifier("none");

    /// This is used to determine which char(s) the player needs to have in their "lightype-section" of their UUID in order to have that particular light type
    protected TypeCreationRegex creationRegex;


    /**
     * Creates an instance of this InnerLight. Remember to register it!
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     * */
    public InnerLight(TypeCreationRegex regex){
        this.creationRegex = regex;
    }

    /** Only to be used by the {@link me.emafire003.dev.lightwithin.events.LightCreationAndEvent}
     * DO NOT OVERRIDE!
     * @return the regex with which to perform the check
     */
    public TypeCreationRegex getCreationRegex(){
        return creationRegex;
    }

    /** Starts the activation of the light, like searching for the targets ana allies and stuff.
     * Automatically calls {@link #activate(PlayerEntity, List, double, int, double)}
     * Must be overriden in the light's implementation
     *
     * @param component The player's component
     * @param player The player for which to start activating the light
     */
    public void startActivation(LightComponent component, PlayerEntity player){
    }

    /**Activates this innerlight type. It is automatically called after {@link #startActivation(LightComponent, PlayerEntity)}
     * so you should not need to use this manually. You need to override this method in the light's implementation.
     *
     * @param caster The player activating this InnerLight
     * @param targets The targets for this activation of the innerlight
     * @param power_multiplier The power multiplier for this activation
     * @param duration The duration for this activation
     * @param cooldown_time The cooldown time to apply for this activation*/
    protected void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time){
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            CGLCompat.getLib().setColor(caster, color);
        }
    }


    /** Checks if the parameters for the power multiplier and the duration are within
     * the ranges of this light. Must be overridden in the implementation.
     *
     * @return A pair where the first value is the power multiplier and the second is the duration*/
    protected Pair<Double, Integer> checkSafety(double power_multiplier, int duration){
        return new Pair<Double, Integer>(power_multiplier, duration);
    }

    /**Returns a list of the possible targets for this Light Type,
     * from the most likely/common to the least likely/common
     */
    public List<TargetType> getPossibleTargetTypes(){
        return possibleTargetTypes;
    }

    /**Returns a list of the trigger checks that start the chain of
     * checks to see if a light is triggerable. Not ordered.
     */
    public List<TriggerChecks> getTriggerChecks(){
        return triggerChecks;
    }

    /**Performs a check for the possibility of triggering this light type.
     * If the check is successful, send a packet to the player to reaady the light
     *
     * @param player the player for which we are performing this check
     * @param component the player's {@link LightComponent}
     * @param attacker the possible attacker of the player
     * @param target the possible target of the player
     */
    public void triggerCheck(PlayerEntity player, LightComponent component, @Nullable LivingEntity attacker, @Nullable LivingEntity target){

    }

    /**
     * Makes a check to see if there are the "light conditions",
     * aka specific blocks items or ambiental factors that can be
     * used as a "catalyst" to trigger the light. See the implementation of {@link #triggerCheck(PlayerEntity, LightComponent, LivingEntity, LivingEntity)}
     * or the wiki.
     *
     * @param player the player which is the possible caster of the light aka the one the checks are going to be performed on
     * @return false by default if not implemented by the specific light type
     */
    public boolean checkLightConditions(PlayerEntity player) {
        return false;
    }


    /** Returns the ingredient used in the BottledLight recipe used for this light type*/
    public Item getIngredient(){
        return INGREDIENT;
    }

    /** Returns the id of this light*/
    public Identifier getLightId() {
        return lightId;
    }

    @Override
    public String toString() {
        return this.lightId.getPath();
    }
}
