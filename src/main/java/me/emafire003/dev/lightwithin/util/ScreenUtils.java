package me.emafire003.dev.lightwithin.util;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.client.MinecraftClient;

public class ScreenUtils {

    public static Pair<Integer, Integer> getXYIcons(ScreenPositionsPresets preset, double scale){
        if(preset.equals(ScreenPositionsPresets.TOP_LEFT)){
            return new Pair<>(10, 10);
        }
        if(preset.equals(ScreenPositionsPresets.TOP_RIGHT)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(10+20*scale)), 10);
        }
        if(preset.equals(ScreenPositionsPresets.BOTTOM_LEFT)){
            return new Pair<>(10, (int)(MinecraftClient.getInstance().getWindow().getScaledHeight()-(10+20*scale)));
        }
        if(preset.equals(ScreenPositionsPresets.BOTTOM_RIGHT)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(10+20*scale)), (int)(MinecraftClient.getInstance().getWindow().getScaledHeight()-(10+20*scale)));
        }
        if(preset.equals(ScreenPositionsPresets.CENTER)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(10+20*scale))/2, (int)(MinecraftClient.getInstance().getWindow().getScaledHeight()-(10+20*scale))/2);
        }
        if(preset.equals(ScreenPositionsPresets.TOP_CENTER)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(10+20*scale))/2, 10);
        }
        if(preset.equals(ScreenPositionsPresets.BOTTOM_CENTER)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(10+20*scale))/2, MinecraftClient.getInstance().getWindow().getScaledHeight()+(10+20));
        }
        LightWithin.LOGGER.error("The preset position '" + preset + "' is invalid, defaulting to: " + ScreenPositionsPresets.TOP_LEFT);
        return new Pair<>(10, 10);
    }

    public static Pair<Integer, Integer> getXYImgs(ScreenPositionsPresets preset, double scale, int imgWidth, int imgHeight, int padding){
        if(preset.equals(ScreenPositionsPresets.TOP_LEFT)){
            return new Pair<>(padding, padding);
        }
        if(preset.equals(ScreenPositionsPresets.TOP_RIGHT)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(padding+imgWidth*scale)), padding);
        }
        if(preset.equals(ScreenPositionsPresets.BOTTOM_LEFT)){
            return new Pair<>(padding, (int)(MinecraftClient.getInstance().getWindow().getScaledHeight()-(padding+imgHeight*scale)));
        }
        if(preset.equals(ScreenPositionsPresets.BOTTOM_RIGHT)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(padding+imgWidth*scale)), (int)(MinecraftClient.getInstance().getWindow().getScaledHeight()-(padding+imgHeight*scale)));
        }
        if(preset.equals(ScreenPositionsPresets.CENTER)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(padding+imgWidth*scale))/2, (int)(MinecraftClient.getInstance().getWindow().getScaledHeight()-(padding+imgHeight*scale))/2);
        }
        if(preset.equals(ScreenPositionsPresets.TOP_CENTER)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(padding+imgWidth*scale))/2, padding);
        }
        if(preset.equals(ScreenPositionsPresets.BOTTOM_CENTER)){
            return new Pair<>((int) (MinecraftClient.getInstance().getWindow().getScaledWidth()-(padding+imgWidth*scale))/2, MinecraftClient.getInstance().getWindow().getScaledHeight()+(padding+imgHeight));
        }
        LightWithin.LOGGER.error("The preset position '" + preset + "' is invalid, defaulting to: " + ScreenPositionsPresets.TOP_LEFT);
        return new Pair<>(padding, padding);
    }


    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public static Pair<Integer, Integer> getXYItems(ScreenPositionsPresets preset, double scale, int screenWidth, int screenHeight, int padding, int dimX, int dimY){

        if(preset.equals(ScreenPositionsPresets.TOP_LEFT)){
            return new Pair<>(padding, padding);
        }
        if(preset.equals(ScreenPositionsPresets.TOP_RIGHT)){
            return new Pair<>((int) ((screenWidth-(padding+dimX)*scale)/scale), padding);
        }
        if(preset.equals(ScreenPositionsPresets.BOTTOM_LEFT)){
            //return new Pair<>(padding, (int)(height-(padding+dim*scale)));
            return new Pair<>(padding, (int)((screenHeight-(padding+dimY)*scale)/scale));
        }
        if(preset.equals(ScreenPositionsPresets.BOTTOM_RIGHT)){
            return new Pair<>((int) ((screenWidth-(padding+dimX)*scale)/scale), (int)((screenHeight-(padding+dimY)*scale)/scale));
        }
        if(preset.equals(ScreenPositionsPresets.CENTER)){
            return new Pair<>((int) ((screenWidth/2-(padding+dimX)*(scale/2))/scale), (int)((screenHeight/2-(padding+dimY)*(scale/2))/scale));
        }
        if(preset.equals(ScreenPositionsPresets.TOP_CENTER)){
            return new Pair<>((int) ((screenWidth/2-(padding+dimX)*(scale/2))/scale), padding);
        }
        if(preset.equals(ScreenPositionsPresets.BOTTOM_CENTER)){
            return new Pair<>((int) ((screenWidth/2-(padding+dimX)*(scale/2))/scale), (int) ((screenHeight-((padding+dimY)*scale))/scale));
        }
        LightWithin.LOGGER.error("The preset position '" + preset + "' is invalid, defaulting to: " + ScreenPositionsPresets.TOP_LEFT);
        return new Pair<>(padding, padding);
    }
}
