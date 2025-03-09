package me.emafire003.dev.lightwithin.client.shaders;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class LightShaders {

    public static final ManagedShaderEffect PHOSPHOR_DRUNK_SHADER = ShaderEffectManager.getInstance()
            .manage(new Identifier("shaders/post/phosphor.json"));

    public static final ManagedShaderEffect DECONVERGE_SHADER = ShaderEffectManager.getInstance()
            .manage(new Identifier("shaders/post/deconverge.json"));

    public static final ManagedShaderEffect WOBBLE_SHADER = ShaderEffectManager.getInstance()
            .manage(new Identifier("shaders/post/wobble.json"));

    private static boolean intoxicated_warning_sent = false;

    public static void registerShaders(){
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
            if(ClientConfig.FORESTAURA_INTOXICATION_SHADER){
                registerIntoxicationShader(tickDelta);
            }

        });
    }

    public static void registerIntoxicationShader(float tickDelta){
        if(MinecraftClient.getInstance().player != null
                && MinecraftClient.getInstance().player.hasStatusEffect(LightEffects.INTOXICATION)
        ){
            if(ClientConfig.INTOXICATION_SHADER_WARNING && !intoxicated_warning_sent){
                intoxicated_warning_sent = true;
                MinecraftClient.getInstance().player.sendMessage(Text.translatable("lightwithin.warning.general").formatted(Formatting.RED, Formatting.BOLD)
                        .append(Text.literal(" §r").append(Text.translatable("lightwithin.warning.intoxication_shader.1").formatted(Formatting.WHITE))));

            }
            //If the amplifier il less than 5 it will display the wobble shader which is a bit easier to manage
            if(Objects.requireNonNull(MinecraftClient.getInstance().player.getStatusEffect(LightEffects.INTOXICATION)).getAmplifier() < 5){
                WOBBLE_SHADER.render(tickDelta);
            }else{
                DECONVERGE_SHADER.render(tickDelta);
                PHOSPHOR_DRUNK_SHADER.render(tickDelta);
            }
        }
        if(MinecraftClient.getInstance().player != null && intoxicated_warning_sent
                && !MinecraftClient.getInstance().player.hasStatusEffect(LightEffects.INTOXICATION)
        ){
            intoxicated_warning_sent = false;
        }
    }
}
