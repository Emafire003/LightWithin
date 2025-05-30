package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.util.IGameHudOverlayMessage;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InGameHud.class)
public abstract class InGameHudOverlayMessageMixin implements IGameHudOverlayMessage {

    @Shadow public abstract void setCanShowChatDisabledScreen(boolean canShowChatDisabledScreen);

    @Shadow private @Nullable Text overlayMessage;

    @Shadow private int overlayRemaining;

    @Shadow private boolean overlayTinted;

    @Override
    public void lightwithin$setOverlayMessageWithDuration(Text text, int tickDuration, boolean tinted){
        this.setCanShowChatDisabledScreen(false);
        this.overlayMessage = text;
        this.overlayRemaining = tickDuration;
        this.overlayTinted = tinted;
    }
}
