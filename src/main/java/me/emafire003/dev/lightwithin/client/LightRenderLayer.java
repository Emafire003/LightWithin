package me.emafire003.dev.lightwithin.client;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LightRenderLayer extends RenderLayer {

    public static final Identifier SKY_TEXTURE = new Identifier("textures/environment/end_sky.png");
    public static final Identifier LIGHT_SCREEN_TEXTURE = LightWithin.getIdentifier("textures/gui/light_screen.png");
    public static final Identifier LIGHT_SCREEN_ATTACKED_TEXTURE = LightWithin.getIdentifier("textures/gui/light_screen_red.png");


    public LightRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
    private static final RenderLayer LIGHT_SCREEN;
    private static final RenderLayer LIGHT_SCREEN_ATTACKED;

    public static RenderLayer getLightScreen() {
        return LIGHT_SCREEN;
    }

    public static RenderLayer getLightScreenAttacked() {
        return LIGHT_SCREEN_ATTACKED;
    }

    static {
        LIGHT_SCREEN = RenderLayer.of("LIGHT_SCREEN", VertexFormats.POSITION, VertexFormat.DrawMode.QUADS, 256, false, false,
                MultiPhaseParameters.builder()
                        .program(END_GATEWAY_PROGRAM)
                        .texture(Textures.create()
                                .add(SKY_TEXTURE, true, false)
                                .add(LIGHT_SCREEN_TEXTURE, true, false)
                                .build())
                        .build(false));
        LIGHT_SCREEN_ATTACKED = RenderLayer.of("LIGHT_SCREEN_ATTACKED ", VertexFormats.POSITION, VertexFormat.DrawMode.QUADS, 256, false, false,
                MultiPhaseParameters.builder()
                        .program(END_GATEWAY_PROGRAM)
                        .texture(Textures.create()
                                .add(SKY_TEXTURE, true, false)
                                .add(LIGHT_SCREEN_ATTACKED_TEXTURE, true, false)
                                .build())
                        .build(false));

    }
}
