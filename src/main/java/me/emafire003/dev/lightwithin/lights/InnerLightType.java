package me.emafire003.dev.lightwithin.lights;

public enum InnerLightType {
    HEAL,
    STRENGTH,
    DEFENCE,
    PITCH_BLACK,
    PURE_WHITE,
    LEECH,//could be degeneration of both of the two legendary lights up here ^
    THUNDER_AURA,
    SHOCKWAVE,

    //This could have a sort of stun effect where the player is kinda dizzy and the STUN effect which is not bucket removable and blocks the player from moving for a while, with a low chance of fumbling the weapon
    //elementals down below

    FROST, // Frost as well? Like one just ices the enemy and the other throws icecles? Needs ice around and
    BLAZING, //to trigger needs a heat source, either in HAND or in 3 block radious
    EARTHEN, //dirt/rock in hand or below feet
    WIND, //in the air or... i dunno
    AQUA, //needs something watery (bottle/bucket) in hand or water around

    CONCEAL,
    SPEED,
    SLOW,
    ENDER_AURA,
    PROJECTILE,
    SPIDER_SENSE,//scale up walls?
    SCATTER, //Maybe explodes taking some of the player's hp and scattering projectiles?
    JUMPY, //Check if there are blocks too high and the player needs. I dunno. Something? Maybe only when in combat?
    FIREFLIES_MASTER, //Welp, not coming with 1.19 sooooo. It's gonna shoot out firefiles that attack or explode integrations with the Fireflally mod xD
    GENERIC,
    NONE
}
//TODO for the elemental ones spawn block structures under the player's feet. Maybe they could have two stages targets, like one where they attack and one where they defend. FOr example, summoning a wall of thin ice to protect an ally from direct hit. Like some kinda ice which has low durability and shatters easly and melts afeter a few seconds. This could be done for the other elements as well.

