package me.emafire003.dev.lightwithin.client;

import me.emafire003.dev.lightwithin.LightWithin;
import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.ClipStack;
import me.x150.renderer.renderer.MSAAFramebuffer;
import me.x150.renderer.renderer.Rectangle;
import me.x150.renderer.renderer.Renderer2d;
import net.minecraft.util.Identifier;

import java.awt.*;

public class EventHandler {
    @EventListener(shift= Shift.POST, type = EventType.HUD_RENDER)
    void preHudRender(RenderEvent re) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            if(LightWithinClient.isLightReady()){
                ClipStack.globalInstance.addWindow(re.getStack(),new Rectangle(20,10,110,110));
                //Renderer2d.renderRoundedQuad(re.getStack(), Color.WHITE,10,10,100,100,5,20);
                Renderer2d.renderTexture(re.getStack(), new Identifier(LightWithin.MOD_ID, "texture/lights/light_icon.png"), 30, 30, 16, 16);
                ClipStack.globalInstance.popWindow();
            }
        });
    }
}
