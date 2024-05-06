package me.emafire003.dev.lightwithin.client;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.networking.LightChargeConsumedPacketC2S;
import me.emafire003.dev.lightwithin.networking.LightUsedPacketC2S;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class ActivationKey {

    public static final KeyBinding lightActivationKey = new KeyBinding(
            "key.lightwithin.activate_light", InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V, "key.categories.gameplay");

    public static void register() {
        LOGGER.info("Registering keybinds...");
        KeyBindingHelper.registerKeyBinding(lightActivationKey);
        ClientTickEvents.END_CLIENT_TICK.register(ActivationKey::update);
    }

    public static void update(MinecraftClient client) {
        if (client.player != null) {
            if(LightWithinClient.isLightReady()){
                if(lightActivationKey.wasPressed() || (LightWithinClient.isAutoActivationAllowed() && ClientConfig.AUTO_LIGHT_ACTIVATION)){
                    ClientPlayNetworking.send(LightUsedPacketC2S.ID, new LightUsedPacketC2S(LightWithinClient.hasUsedCharge()));
                    LightWithinClient.setLightReady(false);
                    LightWithinClient.setWaitForNext(true);
                    //Reverts back the status of the used charge
                    LightWithinClient.setUsedCharge(false);
                }
            }

            LightComponent component = LIGHT_COMPONENT.get(client.player);
            if (lightActivationKey.wasPressed() && component.hasTriggeredNaturally()) {
                if(!LightWithin.isPlayerInCooldown(client.player) && component.getCurrentLightCharges() != 0){
                    ClientPlayNetworking.send(LightChargeConsumedPacketC2S.ID, new LightChargeConsumedPacketC2S(true));

                    //TODO improvement Maybe I should also wait for the server to see if i can actually trigger a light here. Like only send the packet and wait for the response. But meh.
                    client.player.playSound(LightSounds.LIGHT_READY, 1f, 0.63f);
                    client.player.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), 0.37f, 1.3f);
                    LightWithinClient.setLightReady(true);
                    //Used to tell the server that the light has been activated by using a light charge.
                    LightWithinClient.setUsedCharge(true);
                }else{
                    RendererEventHandler.setFailedToUseCharge();
                    client.player.playSound(LightSounds.LIGHT_ERROR, 0.5f, 1f);
                }
            }
        }
    }

}
