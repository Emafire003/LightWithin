package me.emafire003.dev.lightwithin.client;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.ClipStack;
import me.x150.renderer.renderer.MSAAFramebuffer;
import me.x150.renderer.renderer.Rectangle;
import me.x150.renderer.renderer.Renderer2d;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.Identifier;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class EventHandler {

    public static int x = 10;//333;
    public static int y = 10;//305;
    //private int counter = 0;
    private boolean heal_runes = false;
    private boolean defense_runes = false;
    private boolean strength_runes = false;
    private boolean blazing_runes = false;
    private boolean frost_runes = false;
    private boolean earthen_runes = false;
    private boolean wind_runes = false;
    private boolean aqua_runes = false;
    private int ticks = 0;
    int center_x = 0;
    int center_y = 0;
    double scale_factor;

    @EventListener(shift= Shift.POST, type = EventType.HUD_RENDER)
    void preHudRender(RenderEvent re) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            center_x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;
            center_y = MinecraftClient.getInstance().getWindow().getScaledHeight()/2;
            scale_factor = MinecraftClient.getInstance().getWindow().getScaleFactor();

            if(LightWithinClient.isLightReady()){
                ClipStack.globalInstance.addWindow(re.getStack(),new Rectangle(1,1,1000,1000));
                Renderer2d.renderTexture(re.getStack(), new Identifier(LightWithin.MOD_ID, "textures/lights/light.png"), x, y, 20, 20);
                ClipStack.globalInstance.popWindow();
            }
            if(MinecraftClient.getInstance().options.getPerspective().equals(Perspective.FIRST_PERSON)){
                if(heal_runes){
                    ClipStack.globalInstance.addWindow(re.getStack(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(re.getStack(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/heal_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.globalInstance.popWindow();
                }
                if(defense_runes){
                    ClipStack.globalInstance.addWindow(re.getStack(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(re.getStack(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/defense_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.globalInstance.popWindow();
                }
                if(strength_runes){
                    ClipStack.globalInstance.addWindow(re.getStack(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(re.getStack(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/strength_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.globalInstance.popWindow();
                }
                if(blazing_runes){
                    ClipStack.globalInstance.addWindow(re.getStack(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(re.getStack(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/blazing_light_runes.png"), center_x-(400/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.globalInstance.popWindow();
                }
                if(frost_runes){
                    ClipStack.globalInstance.addWindow(re.getStack(),new Rectangle(1,1,1000,1000));
                    Renderer2d.renderTexture(re.getStack(), new Identifier(LightWithin.MOD_ID, "textures/lights/runes/frost_light_runes.png"), center_x-(435/scale_factor)/2, center_y-(160/scale_factor)/2, (400/scale_factor)*1.2, (160/scale_factor)*1.2);
                    ClipStack.globalInstance.popWindow();
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
    }


    //TODO config for how much should the runes be rendered
    public void registerRunesRenderer(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            //This makes the runes appear only for a configured amount of time
            if(heal_runes){
                ticks++;
                if(ticks > 20*3){
                    ticks = 0;
                    heal_runes = false;
                }
            }
            if(defense_runes){
                ticks++;
                if(ticks > 20*3){
                    ticks = 0;
                    defense_runes = false;
                }
            }
            if(strength_runes){
                ticks++;
                if(ticks > 20*3){
                    ticks = 0;
                    strength_runes = false;
                }
            }
            if(blazing_runes){
                ticks++;
                if(ticks > 20*3){
                    ticks = 0;
                    blazing_runes = false;
                }
            }
            if(frost_runes){
                ticks++;
                if(ticks > 20*3){
                    ticks = 0;
                    frost_runes = false;
                }
            }
        });
    }
}
