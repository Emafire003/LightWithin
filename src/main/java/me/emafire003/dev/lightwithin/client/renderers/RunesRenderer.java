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
    //TODO make configurable
    private static int show_runes_for = 3*20;
    
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
    
    public static void render(InnerLightType type, DrawContext drawContext){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            LOGGER.error("Can't display light runes renders! Client player is null!");
            return;
        }

        int center_x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2;
        int center_y = MinecraftClient.getInstance().getWindow().getScaledHeight()/2;
        double scale_factor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        
        if(type.equals(InnerLightType.HEAL)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/heal_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.DEFENCE)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/defense_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.STRENGTH)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/strength_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.BLAZING)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/blazing_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.FROST)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/frost_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.EARTHEN)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/earthen_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.WIND)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/wind_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.AQUA)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/aqua_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.FROG)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/frog_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
            ClipStack.popWindow();
        }
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
