package me.emafire003.dev.lightwithin.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.LightRenderLayer;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.x150.renderer.ClipStack;
import me.x150.renderer.Rectangle;
import me.x150.renderer.Renderer2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import org.joml.Matrix4f;


public class TransitionScreen extends Screen{
    private static final long MIN_LOAD_TIME_MS = 60000L;
    private long loadStartTime;
    private final LuxcognitaScreenV2 nextScreen;
    private boolean inverted = false;


    public TransitionScreen(Text title, LuxcognitaScreenV2 target) {
        super(title);
        this.nextScreen = target;
    }

    public TransitionScreen(Text title, LuxcognitaScreenV2 target, boolean inverted) {
        super(title);
        this.nextScreen = target;
        this.inverted = inverted;
    }

    @Override
    public void init(){
        this.loadStartTime = System.currentTimeMillis();
    }

    //The og dimension of the circle image thing
    int dim = 16;
    boolean inversionDone = false;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        int center_x = this.width/2;
        int center_y = this.height/2;


        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if(this.inverted && !this.inversionDone){
            inversionDone = true;
            dim = width;
        }

        ClipStack.addWindow(context.getMatrices(), new Rectangle(0,0, width, height));
        Renderer2d.renderTexture(context.getMatrices(), LightWithin.getIdentifier("textures/gui/transition_element.png"), center_x-dim/2, center_y-dim/2, dim, dim);
        ClipStack.popWindow();

        if(this.inverted){
            dim = dim - ClientConfig.LUXDIALOGUE_TRANSITION_SPEED; //og: 32

            if(dim < 0){
                MinecraftClient.getInstance().setScreen(nextScreen);
            }
        }else{
            dim = dim + ClientConfig.LUXDIALOGUE_TRANSITION_SPEED;

            if(dim > this.width){
                MinecraftClient.getInstance().setScreen(nextScreen);
            }
        }


    }

    public void fillWithLayer(DrawContext context, RenderLayer layer, int startX, int startY, int endX, int endY, int z) {
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(layer);
        vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z);
        vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z);
        context.draw();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        fillWithLayer(context, LightRenderLayer.getLightScreen(), 0, 0, this.width, this.height, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (System.currentTimeMillis() > this.loadStartTime + MIN_LOAD_TIME_MS) {
            this.close();
        }
    }


    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean shouldPause() {
        //This is the thing that allows the screen to keep being animated by the way.
        return false;
    }
}


