package me.emafire003.dev.lightwithin.client.luxcognita_dialogues;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.util.ScreenPositionsPresets;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static me.emafire003.dev.lightwithin.LightWithin.MOD_ID;


/* JSON equivalent:
TODO add the final version of the json
 */
public class LuxDialogue {

    /// The id of the dialogue used to display it and link to it
    public String dialogueId = "default";
    /// The dialogue file version. DO NOT CHANGE
    public int fileVersion = 1;
    /// A translation string for the main text displayed in the dialogue
    public String mainText = "screen.lightwithin.luxdialogue.default";
    /// The main text's scale
    public float mainTextScale = 1.5f;
    /// Weather or not the presented string has placeholders that need to be replaced with something
    public boolean hasReplaceableMainText = false;
    /// The list of the things the placeholder(s) are going to be replaced by
    public List<Replaceables> replaceablesListMain = List.of(Replaceables.ACTIVATION_KEY);
    /// Weather or not to have a sub text
    public boolean subTextPresent = false;
    /// A translation string for the text displayed under the main text
    public String subText = "screen.lightwithin.luxdialogue.default";
    /// The sub text's scale
    public float subTextScale = 1f;
    /// Weather or not the presented string has placeholders that need to be replaced with something
    public boolean hasReplaceableSubText = false;
    /// The list of the things the placeholder(s) are going to be replaced by
    public List<Replaceables> replaceablesListSub = List.of(Replaceables.ACTIVATION_KEY);
    /// Weather or not to show the Luxcognita Berry in the screen
    public boolean showBerry = true;
    /// The position of the berry as described in {@link me.emafire003.dev.lightwithin.util.ScreenPositionsPresets}
    public ScreenPositionsPresets berryPos = ScreenPositionsPresets.TOP_LEFT;
    ///  The scale of the berry (the item is 16x16 so by default is 2)
    public float berryScale = 2f;
    ///  Weather or not to show an image
    public boolean showImage = false;
    /// The image's position as described for the berry
    public ScreenPositionsPresets imagePos = ScreenPositionsPresets.CENTER;
    /// The image's path location. It's an identifier
    //TODO maybe make all of these into lists? To have more images in one screen
    public Identifier imagePath = LightWithin.getIdentifier("textures/dialogues/default.png");
    /// Image width
    public int imageWidth = 32;
    /// Image height
    public int imageHeight = 32;
    /// The image's scale
    public float imageScale = 1f;
    /// Weather or not the image has stages. If it has they will start from name_0.png to name_x.png
    public boolean imageHasStages = false;
    /// A map of the texture files
    public List<Identifier> imageStages = List.of(LightWithin.getIdentifier("textures/lux_dialogue/default.png"));
    ///  The interval between each frame, in ticks (1s = 20t)
    public int imageInterval = 20;
    ///  Weather or not to show an item
    public boolean showItem = false;
    /// The image's position as described for the berry
    public ScreenPositionsPresets itemPos = ScreenPositionsPresets.CENTER;
    /// The item's identifier
    public Identifier item = Registries.ITEM.getId(LightItems.LUXINTUS_BERRY_POWDER);

    /// The item's scale
    public float itemScale = 3f;
    /// Weather or not to have multiple items show in sequence
    public boolean multipleItems = false;
    /// A List of the items
    public List<Identifier> items = List.of(Registries.ITEM.getId(LightItems.LUXINTUS_BERRY_POWDER), Registries.ITEM.getId(LightItems.LUXINTUS_BERRY));
    /// the interval between each display of an item
    public int itemsInterval = 20;

    /// Weather or not to pick only some of the buttons
    public boolean randomizedButtons = false;
    /// How many buttons to pick each time
    public int randomButtonsAmount = 3;
    /// The first string is a translatable string representing the text displayed on the button, while the second one represents an action that is performed on click, along with its potetential target
    public Map<String, String> buttons = Map.of("screen.lightwithin.luxdialogue.default.button0", "CLOSE");

    /// Weather or not seeing this screen/dialogue updates the dialogue progress
    public boolean dialogueProgress = false;
    /// The code of the dialogue progress state that is achieved one this dialogue has been seen see {@link DialogueProgressState}
    public DialogueProgressState dialogueProgressState = DialogueProgressState.NONE;
    /// if true adds the dialogue progress, otherwise removes it
    public boolean removeDialogueProgress = false;
    /// Weather or not this screen can redirect to another one if a certain progress in dialogue is made
    public boolean canRedirect = false;
    /// Redirects to this screen if the player has the right dialogue progress state
    public String redirectTo = "default";
    /// The dialogue state the player has to have in order to have the redirection
    public DialogueProgressState redirectStateRequired = DialogueProgressState.NONE;
    /// Weather or not the required state is inverted, aka must not have the state
    public boolean invertRedirectRequirement = false;


    public void serialize() {
        Gson serializedDialogue = new GsonBuilder().setPrettyPrinting()//.excludeFieldsWithoutExposeAnnotation()
                .create();

        try {
            Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID+"/jsontest/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter fileWriter = new FileWriter(String.valueOf(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID+"/jsontest/"+this.dialogueId+".json")), StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter, 4096)) {
            serializedDialogue.toJson(this, bufferedWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static LuxDialogue deserialize(String id){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            return gson.fromJson(new FileReader(FabricLoader.getInstance().getConfigDir()+"/"+MOD_ID+"/jsontest/"+id+".json"), LuxDialogue.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static LuxDialogue deserialize(File file){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            return gson.fromJson(new FileReader(file), LuxDialogue.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static LuxDialogue deserialize(Resource resource){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            return gson.fromJson(resource.getReader(), LuxDialogue.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
