package me.emafire003.dev.lightwithin.client.renderers;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.x150.renderer.ClipStack;
import me.x150.renderer.Rectangle;
import me.x150.renderer.Renderer2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class TypeItemRenderer {

    private static boolean rendering = false;
    private static int ticks = 0;
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

    public static void setScale(double value){
        scale = value;
    }
    private static double scale = 5;
    private static float item_animation_multiplier = 1;

    /** Clipstack stuff must be called before this!*/
    private static void renderOverlay(MatrixStack matrixStack, int center_x, int center_y, double length){
        Renderer2d.renderTexture(matrixStack, new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/overlay.png"), center_x-length/2, center_y-length/2, length, length);
    }

    private static int frog_number = 0;

    public static void render(InnerLightType type, MatrixStack matrixStack){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            LOGGER.error("Can't display Luxcognita items renders! Client player is null!");
            return;
        }

        int center_x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;
        int center_y = MinecraftClient.getInstance().getWindow().getScaledHeight()/2;

        double length = 16* scale * item_animation_multiplier;
        //The blocks are 48px so the there is x3 scale "builtin" so i scale it back by 3
        double blockLength = 48 * scale/3 * item_animation_multiplier;

        if(type.equals(InnerLightType.EARTHEN)){
            ClipStack.addWindow(matrixStack, new Rectangle((center_x-blockLength/2)-40,(center_y-blockLength/2),(center_x+blockLength/2)+40,(center_y+blockLength/2)));
            Renderer2d.renderTexture(matrixStack, new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/earthen.png"), center_x-blockLength/2, center_y-blockLength/2, blockLength, blockLength);
            renderOverlay(matrixStack, center_x, center_y, length);
            ClipStack.popWindow();
            return;
        }
        if(type.equals(InnerLightType.FROG)){
            if(ticks%15 == 0){
                frog_number++;
                if(frog_number > 2){
                    frog_number = 0;
                }
            }
            ClipStack.addWindow(matrixStack, new Rectangle((center_x-blockLength/2)-40,(center_y-blockLength/2),(center_x+blockLength/2)+40,(center_y+blockLength/2)));
            Renderer2d.renderTexture(matrixStack, new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/frog_" + frog_number + ".png"), center_x-blockLength/2, center_y-blockLength/2, blockLength, blockLength);
            renderOverlay(matrixStack, center_x, center_y, length);
            ClipStack.popWindow();
            return;
        }

        ClipStack.addWindow(matrixStack, new Rectangle((center_x-length/2)-40,(center_y-length/2),(center_x+length/2)+40,(center_y+length/2)));
        Renderer2d.renderTexture(matrixStack, new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/" + type.toString().toLowerCase() + ".png"), center_x-length/2, center_y-length/2, length, length);
        renderOverlay(matrixStack, center_x, center_y, length);
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
