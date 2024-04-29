package me.emafire003.dev.lightwithin.client.renderers;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.x150.renderer.ClipStack;
import me.x150.renderer.Rectangle;
import me.x150.renderer.Renderer2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class TypeItemRenderer {

    private static boolean rendering = false;
    private static int ticks = 0;
    //TODO make configurable
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

    private static double luxcongnita_item_scale = 5;
    private static float item_animation_multiplier = 1;
    
    public static void render(InnerLightType type, DrawContext drawContext){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            LOGGER.error("Can't display Luxcognita items renders! Client player is null!");
            return;
        }

        int center_x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;
        int center_y = MinecraftClient.getInstance().getWindow().getScaledHeight()/2;

        double length = 16*luxcongnita_item_scale*item_animation_multiplier;

        if(type.equals(InnerLightType.HEAL)){
            ClipStack.addWindow(drawContext.getMatrices(), new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/heal.png"), center_x-length/2, center_y-length/2, length, length);
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/overlay_0.png"), center_x-length/2, center_y-length/2, length, length);
            ClipStack.popWindow();
            //TODO maybe use a sound? Or maybe not. Maybe a general sound for each one. It's just an ingredient after all!
        }
        if(type.equals(InnerLightType.DEFENCE)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/defence.png"), center_x, center_y, length , 16*luxcongnita_item_scale*item_animation_multiplier);
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/overlay_0.png"), center_x, center_y, 16*luxcongnita_item_scale*item_animation_multiplier, 16*luxcongnita_item_scale*item_animation_multiplier);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.STRENGTH)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            VertexConsumerProvider provider = drawContext.getVertexConsumers();
            provider.getBuffer(RenderLayer.getTranslucent());
            MinecraftClient.getInstance().getItemRenderer()
                    .renderItem(Items.BLAZE_POWDER.getDefaultStack(), ModelTransformationMode.FIXED,
                            15728880, OverlayTexture.DEFAULT_UV,
                            drawContext.getMatrices(), provider, MinecraftClient.getInstance().world, 0);

            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.BLAZING)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));


            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.FROST)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/heal.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/overlay_0.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.EARTHEN)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/heal.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/overlay_0.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.WIND)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/heal.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/overlay_0.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.AQUA)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/heal.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/overlay_0.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            ClipStack.popWindow();
        }
        if(type.equals(InnerLightType.FROG)){
            ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(1,1,1000,1000));
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/type/heal.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            Renderer2d.renderTexture(drawContext.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/ingredients/overlay_0.png"), center_x, center_y, 16*luxcongnita_item_scale, 16*luxcongnita_item_scale);
            ClipStack.popWindow();
        }
    }
    
    public static void tick(){
        //This makes the runes appear only for a configured amount of time
        if(rendering){
            ticks++;
            if(ticks%4==0){
                item_animation_multiplier = 1.01f;
            }else{
                item_animation_multiplier = 1.0f;
            }
            if(ticks > show_for){
                ticks = 0;
                stop();
            }
        }
    }
}
