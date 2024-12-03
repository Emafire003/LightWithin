package me.emafire003.dev.lightwithin.client.shaders;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class LightShaders {

    public static final ManagedShaderEffect PHOSPHOR_DRUNK_SHADER = ShaderEffectManager.getInstance()
            .manage(new Identifier("shaders/post/phosphor.json"));

    public static final ManagedShaderEffect DECONVERGE_SHADER = ShaderEffectManager.getInstance()
            .manage(new Identifier("shaders/post/deconverge.json"));

    public static final ManagedShaderEffect WOBBLE_SHADER = ShaderEffectManager.getInstance()
            .manage(new Identifier("shaders/post/wobble.json"));

    public static void registerShaders(){
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
            if(ClientConfig.FORESTAURA_INTOXICATION_SHADER){
                registerIntoxicationShader(tickDelta);
            }

        });
    }

    public static void registerIntoxicationShader(float tickDelta){
        if(MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.hasStatusEffect(LightEffects.INTOXICATION)){

            /*if(ClientConfig.INTOXICATION_SHADER_WARNING){
                MinecraftClient.getInstance().player.sendMessage(Text.translatable("lightwithin.warning.general").formatted(Formatting.RED, Formatting.BOLD)
                        .append(Text.literal(" ").append(Text.translatable("lightwithin.warning.intoxication_shader.1").formatted(Formatting.WHITE))));

            }*/
            if(Objects.requireNonNull(MinecraftClient.getInstance().player.getStatusEffect(LightEffects.INTOXICATION)).getAmplifier() > 5){
                WOBBLE_SHADER.render(tickDelta);
            }else{
                DECONVERGE_SHADER.render(tickDelta);
                PHOSPHOR_DRUNK_SHADER.render(tickDelta);
            }
        }
    }
}
