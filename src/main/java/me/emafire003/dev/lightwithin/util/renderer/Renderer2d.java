package me.emafire003.dev.lightwithin.util.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Math;
import org.joml.Matrix4f;

//Removed unnecessary methods to make the jar size smaller

/**
 * The rendering class for the 2nd dimension, used in the hud renderer or in screens
 */
@SuppressWarnings("unused")
public class Renderer2d {
    /**
     * Reference to the minecraft client
     */
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void beginScissor(double x, double y, double endX, double endY) {
        double width = endX - x;
        double height = endY - y;
        width = Math.max(0, width);
        height = Math.max(0, height);
        float d = (float) client.getWindow().getScaleFactor();
        int ay = (int) ((client.getWindow().getScaledHeight() - (y + height)) * d);
        RenderSystem.enableScissor((int) (x * d), ay, (int) (width * d), (int) (height * d));
    }

    public static void endScissor() {
        RenderSystem.disableScissor();
    }

    /**
     * <p>Renders a texture</p>
     * <p>Make sure to link your texture using {@link RenderSystem#setShaderTexture(int, Identifier)} before using this</p>
     *
     * @param matrices      The context MatrixStack
     * @param x0            The X coordinate
     * @param y0            The Y coordinate
     * @param width         The width of the rendered area
     * @param height        The height of the rendered area
     * @param u             The U of the initial texture (0 for none)
     * @param v             The V of the initial texture (0 for none)
     * @param regionWidth   The UV Region width of the initial texture (can be width)
     * @param regionHeight  The UV Region width of the initial texture (can be height)
     * @param textureWidth  The texture width (can be width)
     * @param textureHeight The texture height (can be height)
     */
    public static void renderTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth,
                                     double textureHeight) {
        double x1 = x0 + width;
        double y1 = y0 + height;
        double z = 0;
        renderTexturedQuad(
                matrices.peek().getPositionMatrix(),
                x0,
                x1,
                y0,
                y1,
                z,
                (u + 0.0F) / (float) textureWidth,
                (u + (float) regionWidth) / (float) textureWidth,
                (v + 0.0F) / (float) textureHeight,
                (v + (float) regionHeight) / (float) textureHeight
        );
    }

    private static void renderTexturedQuad(Matrix4f matrix, double x0, double x1, double y0, double y1, double z, float u0, float u1, float v0, float v1) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        buffer.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        buffer.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        buffer.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }


    /**
     * <p>Renders a texture</p>
     * <p>Make sure to link your texture using {@link RenderSystem#setShaderTexture(int, Identifier)} before using this</p>
     *
     * @param matrices The context MatrixStack
     * @param x        The X coordinate
     * @param y        The Y coordinate
     * @param width    The width of the texture
     * @param height   The height of the texture
     */
    public static void renderTexture(MatrixStack matrices, double x, double y, double width, double height) {
        renderTexture(matrices, x, y, width, height, 0, 0, width, height, width, height);
    }

    /**
     * <p>Renders a texture</p>
     * <p>Does the binding for you, call this instead of {@link #renderTexture(MatrixStack, double, double, double, double)} or {@link #renderTexture(MatrixStack, double, double, double, double, float, float, double, double, double, double)} for ease of use</p>
     *
     * @param matrices The context MatrixStack
     * @param texture  The texture to render
     * @param x        The X coordinate
     * @param y        The Y coordinate
     * @param width    The width of the texture
     * @param height   The height of the texture
     */
    public static void renderTexture(MatrixStack matrices, Identifier texture, double x, double y, double width, double height) {
        RenderSystem.setShaderTexture(0, texture);
        renderTexture(matrices, x, y, width, height);
    }
}

