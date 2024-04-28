package me.emafire003.dev.lightwithin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;

public class LuxcognitaScreen extends Screen{
    private static final long MIN_LOAD_TIME_MS = 30000L;
    private boolean ready = false;
    private boolean closeOnNextTick = false;
    private final boolean shouldClose = false;
    private final long loadStartTime;

    protected LuxcognitaScreen(Text title) {
        super(title);
        this.loadStartTime = System.currentTimeMillis();

    }


    @Override
    public boolean shouldCloseOnEsc() {
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Ok but this should show a warning first!"));
        return true;
    }

    @Override
    protected boolean hasUsageText() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Whatever the berry is going to say").formatted(Formatting.AQUA), this.width / 2, this.height / 2 - 50, 16777215);
        //TODO i will need to add clickable buttons, so they can choose what they want to see, if it's the item, the type, or target, or target item
        //TODO change the renderlayer to something like the Seedlight Riftways one.
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
        fillWithLayer(context, RenderLayer.getEndPortal(), 0, 0, this.width, this.height, 0);
    }

    @Override
    public void tick() {
        if (this.shouldClose || System.currentTimeMillis() > this.loadStartTime + 30000L) {
            this.close();
        }
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}


