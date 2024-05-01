package me.emafire003.dev.lightwithin.client.renderers;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.x150.renderer.ClipStack;
import me.x150.renderer.Rectangle;
import me.x150.renderer.Renderer2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class TypeItemRenderer {

    private static boolean rendering = false;
    private static int ticks = 0;
    //TODO make configurable
    private static int show_for = 5*20;
    
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

    //TODO config
    private static double item_scale = 5;
    private static float item_animation_multiplier = 1;

    /** Clipstack stuff must be called before this!*/
    private static void renderOverlay(DrawContext drawContext, int center_x, int center_y, double length){
        Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/overlay_0.png"), center_x-length/2, center_y-length/2, length, length);
    }

    public static void render(InnerLightType type, DrawContext drawContext){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            LOGGER.error("Can't display Luxcognita items renders! Client player is null!");
            return;
        }

        int center_x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;
        int center_y = MinecraftClient.getInstance().getWindow().getScaledHeight()/2;

        double length = 16* item_scale * item_animation_multiplier;

        if(type.equals(InnerLightType.HEAL)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/heal.png"), center_x-length/2, center_y-length/2, length, length);
            renderOverlay(drawContext, center_x, center_y, length);
            ClipStack.popWindow();
            //TODO maybe use a sound? Or maybe not. Maybe a general sound for each one. It's just an ingredient after all!
        }
        if(type.equals(InnerLightType.DEFENCE)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/defence.png"), center_x-length/2, center_y-length/2, length, length);
            renderOverlay(drawContext, center_x, center_y, length);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.STRENGTH)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/strength.png"), center_x-length/2, center_y-length/2, length, length);
            renderOverlay(drawContext, center_x, center_y, length);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.BLAZING)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/blazing.png"), center_x-length/2, center_y-length/2, length, length);
            renderOverlay(drawContext, center_x, center_y, length);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.FROST)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/frost.png"), center_x-length/2, center_y-length/2, length, length);
            renderOverlay(drawContext, center_x, center_y, length);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.EARTHEN)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/earthen.png"), center_x-length/2, center_y-length/2, length, length);
            renderOverlay(drawContext, center_x, center_y, length);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.WIND)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/wind.png"), center_x-length/2, center_y-length/2, length, length);
            renderOverlay(drawContext, center_x, center_y, length);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.AQUA)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/aqua.png"), center_x-length/2, center_y-length/2-5, length, length);
            renderOverlay(drawContext, center_x, center_y, length);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.FROG)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/frog.png"), center_x-length/2, center_y-length/2, length, length);
            renderOverlay(drawContext, center_x, center_y, length);
            ClipStack.popWindow();
        }
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
                //This makes the item appear slightly smaller each time
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
