package me.emafire003.dev.lightwithin.client;

import me.emafire003.dev.lightwithin.networking.LightUsedPacketC2S;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

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
            if (lightActivationKey.wasPressed()) {
                client.player.sendMessage(new LiteralText("Yep it did work, " + LightWithinClient.isLightReady()), true);
                if(LightWithinClient.isLightReady()){
                    ClientPlayNetworking.send(LightUsedPacketC2S.ID, new LightUsedPacketC2S(true));
                    LightWithinClient.setLightReady(false);
                }

            }
        }
    }

}
