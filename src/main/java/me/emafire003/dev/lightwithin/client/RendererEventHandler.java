package me.emafire003.dev.lightwithin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.renderers.RunesRenderer;
import me.emafire003.dev.lightwithin.client.renderers.TargetItemRenderer;
import me.emafire003.dev.lightwithin.client.renderers.TargetRenderer;
import me.emafire003.dev.lightwithin.client.renderers.TypeItemRenderer;
import me.emafire003.dev.lightwithin.compat.replaymod.ReplayModCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.x150.renderer.ClipStack;
import me.x150.renderer.Rectangle;
import me.x150.renderer.RenderEvents;
import me.x150.renderer.Renderer2d;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.sound.SoundEvents;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class RendererEventHandler {

    public static int light_ready_x = 10;//333;
    public static int light_ready_y = 10;//305;
    public static int light_charge_x = light_ready_x;
    public static int light_charge_y = light_ready_y;
    private static boolean allow_draw_runes = true;
    private static boolean failed_to_use_charge = false;

    //Different from ticks which are used for rendering runes, so they don't overlap
    private short error_ticks = 0;
    int center_x = 0;
    int center_y = 0;
    double scale_factor;
    static double charge_icon_scale = 1.0;
    static double ready_icon_scale = 1.0;

    public static void updateFromConfig(){
        light_ready_x = ClientConfig.LIGHT_READY_ICON_X;
        light_ready_y = ClientConfig.LIGHT_READY_ICON_Y;
        light_charge_x = ClientConfig.LIGHT_CHARGE_ICON_X;
        light_charge_y = ClientConfig.LIGHT_CHARGE_ICON_Y;
        ready_icon_scale = ClientConfig.LIGHT_READY_SCALE_FACTOR;
        charge_icon_scale = ClientConfig.LIGHT_CHARGE_SCALE_FACTOR;
        LightWithinClient.setShouldDrawChargesCount(!ClientConfig.HIDE_LIGHT_CHARGE_ICON);
        allow_draw_runes = ClientConfig.SHOW_RUNES;
        RunesRenderer.setShowRunesFor(ClientConfig.SHOW_RUNES_FOR*20);
        TargetRenderer.setScale(ClientConfig.INGREDIENT_TARGET_SCALE);
        TargetRenderer.setShowFor(ClientConfig.SHOW_INGREDIENT_TARGET_FOR*20);
        TypeItemRenderer.setScale(ClientConfig.INGREDIENT_TARGET_SCALE);
        TypeItemRenderer.setShowFor(ClientConfig.SHOW_INGREDIENT_TARGET_FOR*20);
        TargetItemRenderer.setScale(ClientConfig.INGREDIENT_TARGET_SCALE);
        TargetItemRenderer.setShowFor(ClientConfig.SHOW_INGREDIENT_TARGET_FOR*20);
    }

    public void registerRenderEvent(){
        LOGGER.debug("Registering runes renderer...");
        updateFromConfig();
        RenderEvents.HUD.register(drawContext -> {

            if(MinecraftClient.getInstance().player == null){
                return;
            }

            
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            LightComponent component = LIGHT_COMPONENT.get(MinecraftClient.getInstance().player);

            if(MinecraftClient.getInstance().options.getPerspective().equals(Perspective.FIRST_PERSON)){
                InnerLightType type = component.getType();
                TargetType targetType = component.getTargets();
                if(RunesRenderer.shouldRender() && allow_draw_runes){
                    RunesRenderer.render(type, drawContext);
                }
                if(TypeItemRenderer.shouldRender()){
                    TypeItemRenderer.render(type, drawContext);
                }
                if(TargetItemRenderer.shouldRender()){
                    TargetItemRenderer.render(targetType, drawContext);
                }
                if(TargetRenderer.shouldRender()){
                    TargetRenderer.render(targetType, drawContext);
                }

            }

            //Done to fix the bug of the light being avilable even after death or after triggering
            if(MinecraftClient.getInstance().player == null){
                return;
            }
            if(MinecraftClient.getInstance().player.isDead()){
                LightWithinClient.setLightReady(false);
                return;
            }

            if(MinecraftClient.getInstance().player.hasStatusEffect(LightEffects.LIGHT_ACTIVE) || MinecraftClient.getInstance().player.hasStatusEffect(LightEffects.LIGHT_FATIGUE)){
                LightWithinClient.setLightReady(false);
            }

            //In the replay mod the player is by default in first person, so don't display the runes at all, since they are meant for first person.
            if(ReplayModCompat.isInReplayMode()){
                return;
            }

            center_x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;
            center_y = MinecraftClient.getInstance().getWindow().getScaledHeight()/2;
            scale_factor = MinecraftClient.getInstance().getWindow().getScaleFactor();

            if(LightWithinClient.isLightReady()){
                ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(light_ready_x,light_ready_y,light_ready_x*ready_icon_scale+40,light_ready_y*ready_icon_scale+40));
                Renderer2d.renderTexture(drawContext.getMatrices(), LightWithin.getIdentifier("textures/lights/light.png"), light_ready_x, light_ready_y, 20* charge_icon_scale, 20* charge_icon_scale);
                ClipStack.popWindow();
                //Be aware of the return here, may cause bugs in the future! 13.03.2024
                return;
            }

            if(failed_to_use_charge){
                int charges_number = component.getCurrentLightCharges();
                ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(light_charge_x,light_charge_y,light_charge_x* charge_icon_scale +40,light_charge_y* charge_icon_scale +40));
                Renderer2d.renderTexture(drawContext.getMatrices(), LightWithin.getIdentifier("textures/lights/light_charge_base_error.png"), light_charge_x, light_charge_y, 20* charge_icon_scale, 20* charge_icon_scale);
                Renderer2d.renderTexture(drawContext.getMatrices(), LightWithin.getIdentifier("textures/lights/light_charge_overlay_"+ charges_number +".png"), light_charge_x, light_charge_y, 20* charge_icon_scale, 20* charge_icon_scale);
                ClipStack.popWindow();
            } else if(LightWithinClient.shouldDrawChargesCount() && component.hasTriggeredNaturally()){
                int charges_number = component.getCurrentLightCharges();
                ClipStack.addWindow(drawContext.getMatrices(),new Rectangle(light_charge_x,light_charge_y,light_charge_x* charge_icon_scale +40,light_charge_y* charge_icon_scale +40));
                Renderer2d.renderTexture(drawContext.getMatrices(), LightWithin.getIdentifier("textures/lights/light_charge_base.png"), light_charge_x, light_charge_y, 20* charge_icon_scale, 20* charge_icon_scale);
                Renderer2d.renderTexture(drawContext.getMatrices(), LightWithin.getIdentifier("textures/lights/light_charge_overlay_"+ charges_number +".png"), light_charge_x, light_charge_y, 20* charge_icon_scale, 20* charge_icon_scale);
                ClipStack.popWindow();
            }

        });
    }

    public void renderRunes(){
        RunesRenderer.start();
    }
    public void renderTargetIcon(){
        TargetRenderer.start();
    }
    public void renderLuxTypeItem(){
        TypeItemRenderer.start();
    }
    public void renderLuxTargetItem(){
        TargetItemRenderer.start();
    }


    public void playLightSound(InnerLightType type){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null){
            LOGGER.error("Can't play light sounds! Client player is null!");
            return;
        }
        if(type.equals(InnerLightType.HEAL)){
            player.playSound(LightSounds.HEAL_LIGHT, 1 ,1);
        }
        if(type.equals(InnerLightType.DEFENCE)){
            player.playSound(LightSounds.DEFENSE_LIGHT, 1 ,1);
        }
        if(type.equals(InnerLightType.STRENGTH)){
            player.playSound(LightSounds.STRENGTH_LIGHT, 1 ,1);
        }
        if(type.equals(InnerLightType.BLAZING)){
            player.playSound(LightSounds.BLAZING_LIGHT, 1 ,1);
        }
        if(type.equals(InnerLightType.FROST)){
            player.playSound(LightSounds.FROST_LIGHT, 1 ,1);
        }
        if(type.equals(InnerLightType.EARTHEN)){
            player.playSound(LightSounds.EARTHEN_LIGHT, 1 ,1);
        }
        if(type.equals(InnerLightType.WIND)){
            player.playSound(LightSounds.WIND_LIGHT, 1 ,1);
        }
        if(type.equals(InnerLightType.AQUA)){
            player.playSound(LightSounds.AQUA_LIGHT, 1 ,1);
        }
        if(type.equals(InnerLightType.FROG)){
            player.playSound(SoundEvents.ENTITY_FROG_HURT, 1, 0.8f);
        }
    }

    public void registerRunesRenderer(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            //Not a rune but this works
            if(failed_to_use_charge){
                error_ticks++;
                if(error_ticks > 30){
                    error_ticks = 0;
                    failed_to_use_charge = false;
                }
            }

            if(RunesRenderer.shouldRender()){
                RunesRenderer.tick();
            }
            if(TypeItemRenderer.shouldRender()){
                TypeItemRenderer.tick();
            }
            if(TargetItemRenderer.shouldRender()){
                TargetItemRenderer.tick();
            }
            if(TargetRenderer.shouldRender()){
                TargetRenderer.tick();
            }

        });
    }

    public static void setFailedToUseCharge(){
        failed_to_use_charge = true;
    }
}
