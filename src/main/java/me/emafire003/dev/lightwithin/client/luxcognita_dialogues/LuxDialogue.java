package me.emafire003.dev.lightwithin.client.luxcognita_dialogues;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.util.ScreenPositionsPresets;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import static me.emafire003.dev.lightwithin.LightWithin.MOD_ID;


/* JSON equivalent:
{
  "dialogueId": "default",
  "fileVersion": 1,
  "mainText": "lightwithin.screen.luxdialogue.0",
  "showBerry": true,
  "berryPos": "TOP_LEFT",
  "showImage": false,
  "imgPos": "CENTER",
  "imagePath": {
    "namespace": "lightwithin",
    "path": "textures/lux_dialogue/default.png"
  },
  "showItem": false,
  "itemPos": "CENTER",
  "item": {
    "namespace": "lightwithin",
    "path": "item/luxintus_berry_powder"
  },
  "buttons": {
    "lightwithin.screen.luxdialogue.0.button0": "CLOSE"
  }
}
 */
public class LuxDialogue {

    /// The id of the dialogue used to display it and link to it
    public String dialogueId = "default";
    /// The dialogue file version. DO NOT CHANGE
    public int fileVersion = 1;
    /// A translation string for the main text displayed in the dialogue
    public String mainText = "lightwithin.screen.luxdialogue.0";
    /// Weather or not to show the Luxcognita Berry in the screen
    public boolean showBerry = true;
    /// The position of the berry as described in {@link me.emafire003.dev.lightwithin.util.ScreenPositionsPresets}
    public ScreenPositionsPresets berryPos = ScreenPositionsPresets.TOP_LEFT;
    ///  Weather or not to show an image
    public boolean showImage = false;
    /// The image's position as described for the berry
    public ScreenPositionsPresets imgPos = ScreenPositionsPresets.CENTER;
    /// The image's path location. It's an identifier
    public Identifier imagePath = LightWithin.getIdentifier("textures/lux_dialogue/default.png");
    ///  Weather or not to show an item
    public boolean showItem = false;
    /// The image's position as described for the berry
    public ScreenPositionsPresets itemPos = ScreenPositionsPresets.CENTER;
    /// The item's identifier
    public Identifier item = LightWithin.getIdentifier("item/luxintus_berry_powder");

    /// The first string is a translatable string representing the text displayed on the button, while the second one represents an action that is performed on click, along with its potetential target
    public Map<String, String> buttons = Map.of("lightwithin.screen.luxdialogue.0.button0", "CLOSE");


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

    @Override
    public String toString() {
        return "LuxDialogue{" +
                "dialogueId='" + dialogueId + '\'' +
                ", mainText='" + mainText + '\'' +
                ", showBerry=" + showBerry +
                ", berryPos=" + berryPos +
                ", showImage=" + showImage +
                ", imgPos=" + imgPos +
                ", imagePath=" + imagePath +
                ", showItem=" + showItem +
                ", itemPos=" + itemPos +
                ", item=" + item +
                ", buttons=" + buttons +
                '}';
    }
}
