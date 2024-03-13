package me.emafire003.dev.lightwithin.client;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.config.Config;
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
import net.minecraft.text.Text;
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
            if (lightActivationKey.wasPressed() || Config.AUTO_LIGHT_ACTIVATION) {
                if(LightWithinClient.isLightReady()){
                    ClientPlayNetworking.send(LightUsedPacketC2S.ID, new LightUsedPacketC2S(LightWithinClient.hasUsedCharge()));
                    LightWithinClient.setLightReady(false);
                    LightWithinClient.setWaitForNext(true);
                    //Reverts back the status of the used charge
                    LightWithinClient.setUsedCharge(false);
                }else if(!LightWithin.isPlayerInCooldown(client.player) && LIGHT_COMPONENT.get(client.player).getCurrentLightCharges() != 0){
                    ClientPlayNetworking.send(LightChargeConsumedPacketC2S.ID, new LightUsedPacketC2S(true));

                    client.player.playSound(LightSounds.LIGHT_READY, 1f, 0.63f);
                    client.player.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), 0.37f, 1.3f);
                    LightWithinClient.setLightReady(true);
                    //Used to tell the server that the light has been activated by using a light charge.
                    LightWithinClient.setUsedCharge(true);
                    client.player.sendMessage(Text.translatable("light.charge.used"), true);
                    LIGHT_COMPONENT.get(client.player).setLightCharges(LIGHT_COMPONENT.get(client.player).getCurrentLightCharges()-1);
                }else{
                    RendererEventHandler.setFailedToUseCharge();
                    //TODO change the sound maybe
                    client.player.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.5f, 1.76f);
                }
                //TODO set in the config the option to display or not the Light Charges number
                //TODO set in the config the option for always requiring at least one natural trigger before activating the light.
                //TODO or maybe just put the check above in the item and commands thing so the light can't be charged before you triggered it normally.
            }
        }
    }

}
