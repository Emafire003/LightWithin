package me.emafire003.dev.lightwithin.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.LightRenderLayer;
import me.emafire003.dev.lightwithin.client.LightWithinClient;
import me.emafire003.dev.lightwithin.items.LuxcognitaBerryItem;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;


public class LuxcognitaScreen extends Screen{
    private static final long MIN_LOAD_TIME_MS = 60000L;
    private final long loadStartTime;

    public LuxcognitaScreen(Text title) {
        super(title);
        this.loadStartTime = System.currentTimeMillis();

    }

    @Override
    public void init(){
        int center_x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;

        int scaled_width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int button_width = (int) (center_x/2.3);
        int spacing = (scaled_width-4*button_width)/5;

        ButtonWidget lightTypeButton = new ButtonWidget(spacing, this.height/2, button_width, 20, Text.translatable("screen.luxcognita_dialogue.lightTypeButton").formatted(Formatting.YELLOW),
                this::lightTypeAction);

        ButtonWidget lightTypeIngredientButton = new ButtonWidget(2*spacing+button_width, this.height/2, button_width, 20, Text.translatable("screen.luxcognita_dialogue.lightTypeIngredientButton").formatted(Formatting.YELLOW),
                this::lightTypeIngredientAction);

        ButtonWidget lightTargetButton = new ButtonWidget(3*spacing+2*button_width, this.height/2, button_width, 20, Text.translatable("screen.luxcognita_dialogue.lightTargetButton").formatted(Formatting.YELLOW),
                this::lightTargetAction);

        ButtonWidget lightTargetIngredientButton = new ButtonWidget(4*spacing+3*button_width, this.height/2, button_width, 20, Text.translatable("screen.luxcognita_dialogue.lightTargetIngredientButton").formatted(Formatting.YELLOW),
                this::lightTargetIngredientAction);



        this.addDrawableChild(lightTypeButton);
        this.addDrawableChild(lightTypeIngredientButton);
        this.addDrawableChild(lightTargetButton);
        this.addDrawableChild(lightTargetIngredientButton);
    }

    public void playLuxcognitaDisplaySound(){
        if(MinecraftClient.getInstance().player == null){
            LightWithin.LOGGER.error("Error! Can't play the Luxcognita sound the ClientPlayerEntity is null");
            return;
        }
        MinecraftClient.getInstance().player.playSound(LightSounds.LUXCOGNITA_DISPLAY, 1f, 1f);
    }
    public void lightTypeAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderRunes();
        LuxcognitaBerryItem.sendLightTypeMessage(MinecraftClient.getInstance().player);
        this.close();
    }

    public void lightTypeIngredientAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderLuxTypeItem();
        this.close();
    }

    public void lightTargetAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderTargetIcon();
        LuxcognitaBerryItem.sendLightTargetMessage(MinecraftClient.getInstance().player);
        this.close();
    }

    public void lightTargetIngredientAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderLuxTargetItem();
        this.close();
    }

    private int colorTicks = 0;

    /**Periodically changes the color, to make a little animation thing.
     * It remains in the greens anyway*/
    private int getTextColor(){
        //#3ad94f #31c83f #28b72f #1ea71e #129706
        //TODO make better?
        colorTicks++;
        if(colorTicks < 10){
            return 3856719;
        }
        if(colorTicks < 20){
            return 3262527;
        }
        if(colorTicks < 30){
            return 2668335;
        }
        if(colorTicks < 40){
            return 2008862;
        }
        if(colorTicks < 50){
            return 1218310;
        }
        if(colorTicks < 60){
            return 2008862;
        }
        if(colorTicks < 70){
            return 2668335;
        }
        if(colorTicks < 80){
            return 3262527;
        }
        if(colorTicks < 90){
            return 3856719;
        }
        colorTicks = 0;
        return 3856719;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        //TODO draw the LuxCognita berry at the center of the screen
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);

        // Low (lighter) 3735330
        //Middle 2415936
        // Top (darker) 2406703
        matrixStack.push();
        float textScale = 1.5f;
        matrixStack.scale(textScale, textScale, textScale);
        //2406703 16777215
        DrawableHelper.drawCenteredTextWithShadow(matrixStack, this.textRenderer, Text.translatable("screen.luxcognita_dialogue.luxcognitaTalk").asOrderedText(), (int) ((this.width / 2)/textScale), (int) ((this.height / 2 - 70)/textScale), getTextColor());
        matrixStack.pop();

    }

    //TODO this does not work :(
    public void fillWithLayer(MatrixStack matrixStack, RenderLayer layer, int startX, int startY, int endX, int endY, int z) {
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        VertexConsumer vertexConsumer = immediate.getBuffer(layer);
        vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z).next();
        vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z).next();
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z).next();
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z).next();
        immediate.draw();
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        super.renderBackground(matrices);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        fillWithLayer(matrices, LightRenderLayer.getLightScreen(), 0, 0, this.width, this.height, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (System.currentTimeMillis() > this.loadStartTime + MIN_LOAD_TIME_MS) {
            this.close();
        }
    }

    @Override
    public void close() {
        playLuxcognitaDisplaySound();
        super.close();
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


