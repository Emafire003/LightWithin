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

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class EventHandler {

    public static int x = 10;//333;
    public static int y = 10;//305;
    //private int counter = 0;

    @EventListener(shift= Shift.POST, type = EventType.HUD_RENDER)
    void preHudRender(RenderEvent re) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            //int w = MinecraftClient.getInstance().getWindow().getScaledHeight();
            //int h = MinecraftClient.getInstance().getWindow().getScaledHeight();
            if(LightWithinClient.isLightReady()){
                ClipStack.globalInstance.addWindow(re.getStack(),new Rectangle(1,1,1000,1000));
                Renderer2d.renderTexture(re.getStack(), new Identifier(LightWithin.MOD_ID, "textures/lights/light.png"), x, y, 16, 16);
                ClipStack.globalInstance.popWindow();
            }
        });
    }
}
