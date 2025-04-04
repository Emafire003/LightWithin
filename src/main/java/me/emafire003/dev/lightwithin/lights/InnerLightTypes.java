package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.registry.Registry;

import static me.emafire003.dev.lightwithin.LightWithin.INNERLIGHT_REGISTRY;

@SuppressWarnings("unused")
public class InnerLightTypes {

    public static final InnerLight NONE = registerLight(new NoneLight(), "none");
    ///  The order matters so i'm registering these first
    //Frog? aka f = 6 r = 18 = F+2 o = 15 = E g = 7
    //If there is "frog" spelled as numbers of the alphabet, then your light is frog. Happy?
    public static final InnerLight FROG = registerLight(new FrogLight(
            type_bit -> type_bit.contains("6f2e7")), new FrogLight(null).getLightId().getPath());

    public static final InnerLight FOREST_AURA = registerLight(new ForestAuraLight(
            type_bit -> String.valueOf(type_bit.charAt(2)).matches("[f]")), new ForestAuraLight(null).getLightId().getPath());

    public static final InnerLight THUNDER_AURA = registerLight(new ThunderAuraLight(
            type_bit -> String.valueOf(type_bit.charAt(3)).matches("[f]")), new ThunderAuraLight(null).getLightId().getPath());


    public static final InnerLight HEAL = registerLight(new HealLight(
            type_bit -> String.valueOf(type_bit.charAt(1)).matches("[a-b]")), new HealLight(null).getLightId().getPath()); //Yes the way to get the id is a bit curse i know

    public static final InnerLight DEFENCE = registerLight(new DefenceLight(
            type_bit -> String.valueOf(type_bit.charAt(1)).matches("[c-d]")), new DefenceLight(null).getLightId().getPath());

    public static final InnerLight STRENGTH = registerLight(new StrengthLight(
            type_bit -> String.valueOf(type_bit.charAt(1)).matches("[e-f]")), new StrengthLight(null).getLightId().getPath());

    public static final InnerLight BLAZING = registerLight(new BlazingLight(
            type_bit -> String.valueOf(type_bit.charAt(1)).matches("[0-1]")), new BlazingLight(null).getLightId().getPath());

    public static final InnerLight FROST = registerLight(new FrostLight(
            type_bit -> String.valueOf(type_bit.charAt(1)).matches("[2-3]")), new FrostLight(null).getLightId().getPath());

    public static final InnerLight EARTHEN = registerLight(new EarthenLight(
            type_bit -> String.valueOf(type_bit.charAt(1)).matches("[4-5]")), new EarthenLight(null).getLightId().getPath());

    public static final InnerLight WIND = registerLight(new WindLight(
            type_bit -> String.valueOf(type_bit.charAt(1)).matches("[6-7]")), new WindLight(null).getLightId().getPath());

    public static final InnerLight AQUA = registerLight(new AquaLight(
            type_bit -> String.valueOf(type_bit.charAt(1)).matches("[8-9]")), new AquaLight(null).getLightId().getPath());


    /**Registers a new InnerLight type to the {@link me.emafire003.dev.lightwithin.LightWithin#INNERLIGHT_REGISTRY}*/
    public static InnerLight registerLight(InnerLight light, String id){
        return Registry.register(INNERLIGHT_REGISTRY, LightWithin.getIdentifier(id), light);
    }

    public static void registerLights(){
    }
}
