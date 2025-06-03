package me.emafire003.dev.lightwithin.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import me.emafire003.dev.lightwithin.client.LightRenderLayer;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.LuxDialogue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;


public class LuxcognitaScreenAttacked extends LuxcognitaScreenV2{

    public static final long CLOSING_TIME = 3000L; //should be 2 seconds, maybe i should do 3?

    public LuxcognitaScreenAttacked(Text title, LuxDialogue dialogue) {
        super(title, dialogue);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        fillWithLayer(context, LightRenderLayer.getLightScreenAttacked(), 0, 0, this.width, this.height, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (System.currentTimeMillis() > this.loadStartTime + CLOSING_TIME) {
            this.close();
        }
    }
}


