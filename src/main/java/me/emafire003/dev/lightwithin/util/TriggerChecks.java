package me.emafire003.dev.lightwithin.util;

public enum TriggerChecks {
    ENTITY_ATTACKED, ///  An entity (like the caster) gets attacked by another entity
    ENTITY_ATTACKS_ENTITY, ///  An entity (like the caster) is the one attacking another entity
    ALLY_DIES, ///  An ally of the caster dies
    ALLY_ATTACKED, ///  An ally of the caster is attacked
    ENTITY_FALLING, /// An entity (like the caster, an ally a passive entity) is falling more than a few blocks
    ENTITY_DROWNING, ///  An entity is drowning
    ENTITY_STRUCK_BY_LIGHTNING, /// An entity has been struck by a lightning bolt
    ENTITY_BURNING, /// An entity is on fire/taking fire damage
    ENTITY_FREEZING, /// An entity is freezing/taking freeze damage (powder snow)
    // TODO IMPLEMENT in the event thingies
    ARMOR_OR_TOOL_BREAKS, /// The equipment of an entity breaks

}
