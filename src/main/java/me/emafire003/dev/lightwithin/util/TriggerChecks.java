package me.emafire003.dev.lightwithin.util;

public enum TriggerChecks {
    ENTITY_ATTACKED("light.trigger_check.entity_attacked"), ///  An entity (like the caster) gets attacked by another entity
    ENTITY_ATTACKS_ENTITY("light.trigger_check.entity_attacks_entity"), ///  An entity (like the caster) is the one attacking another entity
    ALLY_DIES("light.trigger_check.ally_dies"), ///  An ally of the caster dies
    ALLY_ATTACKED("light.trigger_check.ally_attacked"), ///  An ally of the caster is attacked
    ENTITY_FALLING("light.trigger_check.entity_falling"), /// An entity (like the caster, an ally a passive entity) is falling more than a few blocks
    ENTITY_DROWNING("light.trigger_check.entity_drowning"), ///  An entity is drowning
    ENTITY_STRUCK_BY_LIGHTNING("light.trigger_check.entity_struck_by_lightning"), /// An entity has been struck by a lightning bolt
    ENTITY_BURNING("light.trigger_check.entity_burning"), /// An entity is on fire/taking fire damage
    ENTITY_FREEZING("light.trigger_check.entity_freezing"), /// An entity is freezing/taking freeze damage (powder snow)
    ARMOR_OR_TOOL_BREAKS("light.trigger_check.equipment_break"),
    ; /// The equipment of an entity breaks

    private final String translationString;

    TriggerChecks(String s) {
        translationString = s;
    }

    public String getTranslationString(){
        return translationString;
    }

}
