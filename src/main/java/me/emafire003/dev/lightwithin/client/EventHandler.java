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
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class EventHandler {

    public static int x = 10;//333;
    public static int y = 10;//305;
    //private int counter = 0;
    private boolean heal_runes = false;
    private int heal_ticks = 0;
    private boolean defense_runes = false;
    private int defense_ticks = 0;
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
            }
        });
    }

    public void renderRunes(InnerLightType type, ClientPlayerEntity player){
        LOGGER.info("Rendering runes?");
        if(type.equals(InnerLightType.HEAL)){
            heal_runes = true;
            player.playSound(LightSounds.HEAL_LIGHT, 1 ,1);
        }else if(type.equals(InnerLightType.DEFENCE)){
            defense_runes = true;
            player.playSound(LightSounds.DEFENSE_LIGHT, 1 ,1);
        }
    }


    //TODO config for how much should the runes be rendered
    public void registerRunesRenderer(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            //This makes the runes appear only for a configured amount of time
            if(heal_runes){
                heal_ticks++;
                if(heal_ticks > 20*3){
                    heal_ticks = 0;
                    heal_runes = false;
                }
            }
            if(defense_runes){
                defense_ticks++;
                if(defense_ticks > 20*3){
                    defense_ticks = 0;
                    defense_runes = false;
                }
            }
        });
    }
}
