package me.emafire003.dev.lightwithin.lights;

public enum InnerLightType {
    HEAL,
    STRENGTH,
    DEFENCE,
    PITCH_BLACK, //maybe there could be only one of a kind of these two per server/world and if they don't log in for some time a new one can be chosen
    PURE_WHITE,
    LEECH,//could be degeneration of both of the two legendary lights up here ^
    THUNDER_AURA,
    SHOCKWAVE,

    FROST_FIRE, //could be a special SELF eheh one

    //This could have a sort of stun effect where the player is kinda dizzy and the STUN effect which is not bucket removable and blocks the player from moving for a while, with a low chance of fumbling the weapon
    //elementals down below

    FROST, // Frost as well? Like one just ices the enemy and the other throws icecles? Needs ice around and
    //Frost could have a defensive mode fore allies/self and one as an attack to enemies (no ALL target). The attack one encases the enemies in ice, the defensive builds up an ice wall around the allies to shield them
    //if the player dies of frost there is going to be an ice sculpture of it
    //the player gets trapped in ice, i could make him invisible and spawn the ice scuplture and make him immovable
    //could do another "ring" effect but more like a cloud effect like, more layered
    //the block underneath will be converted to snow&blue ice
    BLAZING, //to trigger needs a heat source, either in HAND or in 3 block radious
    //the block underneath will be turned to path and magma blocks and such
    EARTHEN, //dirt/rock in hand or below feet
    //could throw rocks and blind the attackers while encasing the allies in dirt/stone
    //SPAWNS A GOLEM TO ATTACK THE ENEMIES AND SUCH (not sure if a normal one or a new gecko lib one)
    WIND, //in the air or... i dunno MAYBE MAKES THE PLAYER FLY FOR A BIT?
    //simply blow away the enemies and give speed and slow falling to self? The "ALLIES" could be like  buff both self and allies? With high speed and jump boost and stuff
    AQUA, //needs something watery (bottle/bucket) in hand or water around
    //this is going to be quite complex

    CONCEAL,
    SPEED,
    SLOW,
    FOREST_AURA, //spawn vines to block people, poisons enemies and such
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

