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

public class RunesRenderer {

    private static boolean renderRunes = false;
    private static int runesTick = 0;
    private static int show_runes_for = 3*20;
    private static double runes_scale = 1;
    
    public static boolean shouldRender(){
        return renderRunes;
    }

    public static void start(){
        renderRunes = true;
    }
    public static void stop(){
        renderRunes = false;
    }
    public static void setShowRunesFor(int ticks){
        show_runes_for = ticks;
    }

    public static void setRunesScale(double scale){
        runes_scale = scale;
    }
    
    public static void render(InnerLightType type, DrawContext drawContext){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            LOGGER.error("Can't display light runes renders! Client player is null!");
            return;
        }

        int center_x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2;
        int center_y = MinecraftClient.getInstance().getWindow().getScaledHeight()/2;
        //double scale_factor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        double width = 400*runes_scale;
        double height = 160*runes_scale;

        ClipStack.addWindow(drawContext.getMatrices(), new Rectangle((center_x-width/2)-40,(center_y-height/2),(center_x+width/2)+40,(center_y+height/2)));
        Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/"+ type.toString().toLowerCase() + "_light_runes.png"), center_x-width/2, center_y-height/2, width, height);
        ClipStack.popWindow();
    }
    
    public static void tick(){
        //This makes the runes appear only for a configured amount of time
        if(renderRunes){
            runesTick++;
            if(runesTick > show_runes_for){
                runesTick = 0;
                stop();
            }
        }
    }
}
