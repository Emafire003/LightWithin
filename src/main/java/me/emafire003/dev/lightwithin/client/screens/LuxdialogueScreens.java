package me.emafire003.dev.lightwithin.client.screens;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.LuxDialogue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuxdialogueScreens {

    /// The first (the key) is the id of the dialogue screen, the second an instance of it
    public static final HashMap<String, LuxcognitaScreenV2> LUXDIALOGUE_SCREENS = new HashMap<>();

    public static void registerDialogueScreens(){
        Map<Identifier, List<Resource>> dialogueFiles = MinecraftClient.getInstance().getResourceManager().findAllResources("dialogues", path -> path.getPath().endsWith(".json"));

        dialogueFiles.forEach( (id, dialogueFile) ->
                {
                    String dialogueId = id.getPath().replace("dialogues/", "").replace(".json", "");
                    if(LUXDIALOGUE_SCREENS.containsKey(dialogueId)){
                        LightWithin.LOGGER.error("There is a duplicate screen with the id '" + dialogueId + "', skipping it!");
                    }else{
                        LUXDIALOGUE_SCREENS.put(dialogueId, new LuxcognitaScreenV2(Text.literal(dialogueId),
                                LuxDialogue.deserialize(dialogueFile.get(0))));
                    }
                }
                );
        /*LuxDialogue dialogue = LuxDialogue.deserialize("mod1");
        LUXDIALOGUE_SCREENS.put("mod1", new LuxcognitaScreenV2(Text.literal("mod1"), dialogue));
        LuxDialogue dialogue1 = LuxDialogue.deserialize("default");
        LUXDIALOGUE_SCREENS.put("default", new LuxcognitaScreenV2(Text.literal("default"), dialogue1));
        LuxDialogue dialogue2 = LuxDialogue.deserialize("modder");
        LightWithin.LOGGER.info("The deserialized modder: " + dialogue2);
        LUXDIALOGUE_SCREENS.put("modder", new LuxcognitaScreenV2(Text.literal("modder"), dialogue2));
        LightWithin.LOGGER.warn("The modder screen: " + LUXDIALOGUE_SCREENS.get("modder"));*/
        LightWithin.LOGGER.info("Registered the files as: \n\n" + LUXDIALOGUE_SCREENS);
        LightWithin.LOGGER.info("The ids are: " + LUXDIALOGUE_SCREENS.keySet());
    }
}
