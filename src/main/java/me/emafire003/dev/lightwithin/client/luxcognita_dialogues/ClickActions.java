package me.emafire003.dev.lightwithin.client.luxcognita_dialogues;

public enum ClickActions {
    GO_DIALOGUE, /// needs to have a parameter like "GO_DIALOGUE<"id">"
    CLOSE,
    SEND_CHAT_MSG, /// needs to have a parameter like "SEND_CHAT_MSG<"translation_string">"
    SEND_OVERLAY_MSG, /// needs to have a parameter like "SEND_OVERLAY_MSG<"translation_string">"
    /// GO_IF_PROGRESS<"id", "LIST_TYPES">. Goes to the next dialogue only if the player has that specific {@link DialogueProgressState}.
    /// Otherwise, closes the screen
    GO_IF_PROGRESS,

    SHOW_TYPE_RUNES,
    SHOW_TARGET,
    SHOW_TYPE_INGREDIENT,
    SHOW_TARGET_INGREDIENT,
    SHOW_POWER,
    SHOW_DURATION,
    SHOW_MAXCOOLDOWN,
    SHOW_MAXCHARGES,
    SHOW_LIGHT_CONDITIONS,
    SHOW_TRIGGER_EVENTS,
    TEXT_INPUT,
    GO_TYPE_AFTER_INPUT,
    GO_TARGET_AFTER_INPUT
}
