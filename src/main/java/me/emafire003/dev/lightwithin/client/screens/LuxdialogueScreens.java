package me.emafire003.dev.lightwithin.client.screens;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.LuxDialogue;
import net.minecraft.text.Text;

import java.util.HashMap;

public class LuxdialogueScreens {

    /// The first (the key) is the id of the dialogue screen, the second an instance of it
    public static final HashMap<String, LuxcognitaScreenV2> LUXDIALOGUE_SCREENS = new HashMap<>();

    public static void registerDialogueScreens(){
        //TODO implement (and put in the onInitialize)
        LuxDialogue dialogue = LuxDialogue.deserialize("mod1");
        LUXDIALOGUE_SCREENS.put("mod1", new LuxcognitaScreenV2(Text.literal("mod1"), dialogue));
        LuxDialogue dialogue1 = LuxDialogue.deserialize("default");
        LUXDIALOGUE_SCREENS.put("default", new LuxcognitaScreenV2(Text.literal("default"), dialogue1));
        LuxDialogue dialogue2 = LuxDialogue.deserialize("modder");
        LightWithin.LOGGER.info("The deserialized modder: " + dialogue2);
        LUXDIALOGUE_SCREENS.put("modder", new LuxcognitaScreenV2(Text.literal("modder"), dialogue2));
        LightWithin.LOGGER.warn("The modder screen: " + LUXDIALOGUE_SCREENS.get("modder"));
    }
}
