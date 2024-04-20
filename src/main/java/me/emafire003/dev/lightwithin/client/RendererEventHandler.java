package me.emafire003.dev.lightwithin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.replaymod.ReplayModCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.renderer.ClipStack;
import me.emafire003.dev.lightwithin.util.renderer.Rectangle;
import me.emafire003.dev.lightwithin.util.renderer.RenderEvents;
import me.emafire003.dev.lightwithin.util.renderer.Renderer2d;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class RendererEventHandler {

    public static int light_ready_x = 10;//333;
    public static int light_ready_y = 10;//305;
    public static int light_charge_x = light_ready_x;
    public static int light_charge_y = light_ready_y;
    private static boolean draw_runes = true;
    private static int show_runes_for = 3*20;
    private boolean heal_runes = false;
    private boolean defense_runes = false;
    private boolean strength_runes = false;
    private boolean blazing_runes = false;
    private boolean frost_runes = false;
    private boolean earthen_runes = false;
    private boolean wind_runes = false;
    private boolean aqua_runes = false;
    private boolean frog_runes = false;
    private static boolean failed_to_use_charge = false;
    private int ticks = 0;
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
        draw_runes = ClientConfig.SHOW_RUNES;
        show_runes_for = ClientConfig.SHOW_RUNES_FOR*20;
    }

    public void registerRenderEvent(){
        LOGGER.info("Registering runes renderer...");
        updateFromConfig();
        RenderEvents.HUD.register(matrixStack -> {

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

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            if(LightWithinClient.isLightReady()){
                ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(light_ready_x,light_ready_y,light_ready_x*ready_icon_scale+40,light_ready_y*ready_icon_scale+40));
                Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/light.png"), light_ready_x, light_ready_y, 20* charge_icon_scale, 20* charge_icon_scale);
                ClipStack.popWindow();
                //Be aware of the return here, may cause bugs in the future! 13.03.2024
                return;
            }

            LightComponent component = LIGHT_COMPONENT.get(MinecraftClient.getInstance().player);

            if(failed_to_use_charge){
                int charges_number = component.getCurrentLightCharges();
                ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(light_charge_x,light_charge_y,light_charge_x* charge_icon_scale +40,light_charge_y* charge_icon_scale +40));
                Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/light_charge_base_error.png"), light_charge_x, light_charge_y, 20* charge_icon_scale, 20* charge_icon_scale);
                Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/light_charge_overlay_"+ charges_number +".png"), light_charge_x, light_charge_y, 20* charge_icon_scale, 20* charge_icon_scale);
                ClipStack.popWindow();
            } else if(LightWithinClient.shouldDrawChargesCount() && component.hasTriggeredNaturally()){
                int charges_number = component.getCurrentLightCharges();
                ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(light_charge_x,light_charge_y,light_charge_x* charge_icon_scale +40,light_charge_y* charge_icon_scale +40));
                Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/light_charge_base.png"), light_charge_x, light_charge_y, 20* charge_icon_scale, 20* charge_icon_scale);
                Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/light_charge_overlay_"+ charges_number +".png"), light_charge_x, light_charge_y, 20* charge_icon_scale, 20* charge_icon_scale);
                ClipStack.popWindow();
            }

            if(MinecraftClient.getInstance().options.getPerspective().equals(Perspective.FIRST_PERSON) && draw_runes){
                if(heal_runes){
                    ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/heal_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.popWindow();
                }
                if(defense_runes){
                    ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/defense_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.popWindow();
                }
                if(strength_runes){
                    ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/strength_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.popWindow();
                }
                if(blazing_runes){
                    ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/blazing_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.popWindow();
                }
                if(frost_runes){
                    ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/frost_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.popWindow();
                }
                if(earthen_runes){
                    ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/earthen_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.popWindow();
                }
                if(wind_runes){
                    ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/wind_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.popWindow();
                }
                if(aqua_runes){
                    ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/aqua_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.popWindow();
                }
                if(frog_runes){
                    ClipStack.addWindow(matrixStack.getMatrices(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(matrixStack.getMatrices(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/frog_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.popWindow();
                }
            }
        });
    }

    public void renderRunes(InnerLightType type, ClientPlayerEntity player){
        if(type.equals(InnerLightType.HEAL)){
            heal_runes = true;
            player.playSound(LightSounds.HEAL_LIGHT, 1 ,1);
        }else if(type.equals(InnerLightType.DEFENCE)){
            defense_runes = true;
            player.playSound(LightSounds.DEFENSE_LIGHT, 1 ,1);
        }else if(type.equals(InnerLightType.STRENGTH)){
            strength_runes = true;
            player.playSound(LightSounds.STRENGTH_LIGHT, 1 ,1);
        }else if(type.equals(InnerLightType.BLAZING)){
            blazing_runes = true;
            player.playSound(LightSounds.BLAZING_LIGHT, 1 ,1);
        }
        else if(type.equals(InnerLightType.FROST)){
            frost_runes = true;
            player.playSound(LightSounds.FROST_LIGHT, 1 ,1);
        }
        else if(type.equals(InnerLightType.EARTHEN)){
            earthen_runes = true;
            player.playSound(LightSounds.EARTHEN_LIGHT, 1 ,1);
        }
        else if(type.equals(InnerLightType.WIND)){
            wind_runes = true;
            player.playSound(LightSounds.WIND_LIGHT, 1 ,1);
        }
        else if(type.equals(InnerLightType.AQUA)){
            aqua_runes = true;
            player.playSound(LightSounds.AQUA_LIGHT, 1 ,1);
        }
        else if(type.equals(InnerLightType.FROG)){
            frog_runes = true;
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

            //This makes the runes appear only for a configured amount of time
            if(heal_runes){
                ticks++;
                if(ticks > show_runes_for){
                    ticks = 0;
                    heal_runes = false;
                }
            }
            if(defense_runes){
                ticks++;
                if(ticks > show_runes_for){
                    ticks = 0;
                    defense_runes = false;
                }
            }
            if(strength_runes){
                ticks++;
                if(ticks > show_runes_for){
                    ticks = 0;
                    strength_runes = false;
                }
            }
            if(blazing_runes){
                ticks++;
                if(ticks > show_runes_for){
                    ticks = 0;
                    blazing_runes = false;
                }
            }
            if(frost_runes){
                ticks++;
                if(ticks > show_runes_for){
                    ticks = 0;
                    frost_runes = false;
                }
            }
            if(earthen_runes){
                ticks++;
                if(ticks > show_runes_for){
                    ticks = 0;
                    earthen_runes = false;
                }
            }
            if(wind_runes){
                ticks++;
                if(ticks > show_runes_for){
                    ticks = 0;
                    wind_runes = false;
                }
            }
            if(aqua_runes){
                ticks++;
                if(ticks > show_runes_for){
                    ticks = 0;
                    aqua_runes = false;
                }
            }
            if(frog_runes){
                ticks++;
                if(ticks > show_runes_for){
                    ticks = 0;
                    frog_runes = false;
                }
            }

        });
    }

    public static void setFailedToUseCharge(){
        failed_to_use_charge = true;
    }
}
