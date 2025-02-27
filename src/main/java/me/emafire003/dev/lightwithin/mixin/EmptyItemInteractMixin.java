package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.networking.InteractedPacketC2S;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class EmptyItemInteractMixin {

    @Inject(at = @At(value = "HEAD"), method = "doItemUse")
    private void injectInteract(CallbackInfo ci) {
        ClientPlayNetworking.send(InteractedPacketC2S.ID, new InteractedPacketC2S());
    }
}
