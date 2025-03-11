package me.emafire003.dev.lightwithin.client.renderers;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.x150.renderer.ClipStack;
import me.x150.renderer.Rectangle;
import me.x150.renderer.Renderer2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class TargetRenderer {

    private static boolean rendering = false;
    private static int ticks = 0;
    private static int show_for = 3*20;
    
    public static boolean shouldRender(){
        return rendering;
    }

    public static void start(){
        rendering = true;
    }
    public static void stop(){
        rendering = false;
    }
    public static void setShowFor(int ticks){
        show_for = ticks;
    }

    public static void setScale(double value){
        scale = value;
    }

    private static double scale = 5;
    private static float item_animation_multiplier = 1;

    /** Clipstack stuff must be called before this!*/
    private static void renderOverlay(DrawContext drawContext, int center_x, int center_y, double length){
        Renderer2d.renderTexture(drawContext.getMatrices(), LightWithin.getIdentifier("textures/lights/ingredients/overlay.png"), center_x-length/2, center_y-length/2, length, length);
    }

    public static void render(TargetType targetType, DrawContext drawContext){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            LOGGER.error("Can't display Luxcognita items renders! Client player is null!");
            return;
        }

        int center_x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;
        int center_y = MinecraftClient.getInstance().getWindow().getScaledHeight()/2;

        double length = 16* scale * item_animation_multiplier;

        ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
        Renderer2d.renderTexture(drawContext.getMatrices(), LightWithin.getIdentifier("textures/lights/target_icons/blurred/" + targetType.toString().toLowerCase() + ".png"), center_x-length/2, center_y-length/2, length, length);
        renderOverlay(drawContext, center_x, center_y, length);
        ClipStack.popWindow();
    }

    private static int animationTicks = 0;
    public static void tick(){
        //This makes the runes appear only for a configured amount of time
        if(rendering){
            ticks++;
            if(animationTicks < 15){
                item_animation_multiplier = item_animation_multiplier + (float) animationTicks /1500;
                animationTicks++;
            }else if(animationTicks < 30){
                //This should make the item appear slightly smaller each time
                item_animation_multiplier = item_animation_multiplier - (float) (animationTicks-20) /1500;
                animationTicks++;
            }else{
                animationTicks = 0;
            }
            if(ticks > show_for){
                ticks = 0;
                animationTicks = 0;
                item_animation_multiplier = 1.0f;
                stop();
            }
        }
    }
}
