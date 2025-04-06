package me.emafire003.dev.lightwithin.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.LightRenderLayer;
import me.emafire003.dev.lightwithin.client.LightWithinClient;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.items.LuxcognitaBerryItem;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;


//TODO when this is open the player should be mostly transparent and should also be invulnerable for a time. An attack will cancel the screen and make the player vulnerable again after 2/3 seconds.
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

        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().margin(4, this.height/2, 4, 0);
        gridWidget.setSpacing(10);
        GridWidget.Adder adder = gridWidget.createAdder(4);

        ButtonWidget lightTypeButton = ButtonWidget
                .builder(Text.translatable("screen.luxcognita_dialogue.lightTypeButton").formatted(Formatting.YELLOW),
                        this::lightTypeAction
                        //(buttonWidget) -> this.close()
                )
                .size((int) (center_x/2.3), 20)
                .build();

        ButtonWidget lightTypeIngredientButton = ButtonWidget.builder(Text.translatable("screen.luxcognita_dialogue.lightTypeIngredientButton").formatted(Formatting.YELLOW), this::lightTypeIngredientAction)
                .size((int) (center_x/2.3), 20)
                .build();

        ButtonWidget lightTargetButton = ButtonWidget.builder(Text.translatable("screen.luxcognita_dialogue.lightTargetButton").formatted(Formatting.YELLOW), this::lightTargetAction)
                .size((int) (center_x/2.3), 20)
                .build();

        ButtonWidget lightTargetIngredientButton = ButtonWidget.builder(Text.translatable("screen.luxcognita_dialogue.lightTargetIngredientButton").formatted(Formatting.YELLOW), this::lightTargetIngredientAction)
                .size((int) (center_x/2.3), 20)
                .build();

        adder.add(lightTypeButton);
        adder.add(lightTargetButton);
        adder.add(lightTypeIngredientButton);
        adder.add(lightTargetIngredientButton);

        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, 0, this.width, this.height, 0.5F, 0.25F);
        gridWidget.forEachChild(this::addDrawableChild);
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //TODO draw the LuxCognita berry at the center of the screen
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        // Low (lighter) 3735330
        //Middle 2415936
        // Top (darker) 2406703
        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        float textScale = 1.5f;
        matrixStack.scale(textScale, textScale, textScale);
        //2406703 16777215
        //TODO ok both of this work

        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("screen.luxcognita_dialogue.luxcognitaTalk"), (int) ((this.width / 2)/textScale), (int) ((this.height / 2 - 70)/textScale), getTextColor());

        matrixStack.scale(4f, 4f, 4f);
        //context.drawTexture(LightWithin.getIdentifier("textures/item/luxcognita_berry.png"), 20, 20, 1, 1, 16, 16, 16, 16);
        context.drawItem(new ItemStack(LightItems.LUXCOGNITA_BERRY), 10, 1);

        matrixStack.pop();


    }

    public void fillWithLayer(DrawContext context, RenderLayer layer, int startX, int startY, int endX, int endY, int z) {
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(layer);
        vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z).next();
        vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z).next();
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z).next();
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z).next();
        context.draw();
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
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


