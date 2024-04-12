package me.emafire003.dev.lightwithin.util;

public interface IRenderEffectsEntity {
    void lightWithin$renderEffect(RenderEffect effect);
    boolean lightWithin$shouldRender(RenderEffect effect);
    void lightWithin$stopEffect(RenderEffect effect);
}
