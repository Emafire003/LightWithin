package me.emafire003.dev.lightwithin.util;

public interface IRenderEffectsEntity {
    void lightWithin$renderEffect(RenderEffect effect, int ticks);
    boolean lightWithin$shouldRender(RenderEffect effect);
    void lightWithin$stopEffect(RenderEffect effect);
    int lightWithin$getRenderTicks(RenderEffect effect);
}
