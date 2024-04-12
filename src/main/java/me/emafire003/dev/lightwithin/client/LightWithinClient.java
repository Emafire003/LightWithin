package me.emafire003.dev.lightwithin.client;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.blocks.LightBlocks;
import me.emafire003.dev.lightwithin.commands.client.ClientLightCommands;
import me.emafire003.dev.lightwithin.compat.yacl.YaclScreenMaker;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.entities.LightEntities;
import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntityModel;
import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntityRenderer;
import me.emafire003.dev.lightwithin.networking.*;
import me.emafire003.dev.lightwithin.particles.LightParticle;
import me.emafire003.dev.lightwithin.particles.LightTypeParticleV3;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.ConfigPacketConstants;
import me.emafire003.dev.lightwithin.util.IRenderEffectsEntity;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

@Environment(EnvType.CLIENT)
public class LightWithinClient implements ClientModInitializer {

    private static boolean lightReady = false;
    private static boolean waitForNext = false;
    private static boolean usedCharge = false;
    private static boolean allowAutoActivation = false;
    int seconds = 10;
    int tickCounter = 0;
    RendererEventHandler event_handler = new RendererEventHandler();

    private static boolean shouldDrawChargesCount = true;

    public static final EntityModelLayer MODEL_EARTH_GOLEM_LAYER = new EntityModelLayer(new Identifier(LightWithin.MOD_ID, "earth_golem"), "main");


    @Override
    public void onInitializeClient() {
       ActivationKey.register();
       registerLightReadyPacket();
       registerRenderRunesPacket();
       registerWindLightVelocityPacket();
       registerConfigOptionsSyncPacket();
       registerPlayRenderEffectPacket();
       ClientCommandRegistrationCallback.EVENT.register(ClientLightCommands::registerCommands);
       event_handler.registerRenderEvent();
       event_handler.registerRunesRenderer();
       ParticleFactoryRegistry.getInstance().register(LightParticles.HEALLIGHT_PARTICLE, LightTypeParticleV3.Factory::new);
       ParticleFactoryRegistry.getInstance().register(LightParticles.DEFENSELIGHT_PARTICLE, LightTypeParticleV3.Factory::new);
       ParticleFactoryRegistry.getInstance().register(LightParticles.STRENGTHLIGHT_PARTICLE, LightTypeParticleV3.Factory::new);

        ParticleFactoryRegistry.getInstance().register(LightParticles.BLAZINGLIGHT_PARTICLE, LightTypeParticleV3.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LightParticles.FROSTLIGHT_PARTICLE, LightTypeParticleV3.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LightParticles.EARTHENLIGHT_PARTICLE, LightTypeParticleV3.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LightParticles.WINDLIGHT_PARTICLE, LightTypeParticleV3.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LightParticles.AQUALIGHT_PARTICLE, LightTypeParticleV3.Factory::new);

        ParticleFactoryRegistry.getInstance().register(LightParticles.FROGLIGHT_PARTICLE, LightTypeParticleV3.Factory::new);

        ParticleFactoryRegistry.getInstance().register(LightParticles.LIGHT_PARTICLE, LightParticle.Factory::new);

        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.FROZEN_PLAYER_TOP_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.FROZEN_PLAYER_BOTTOM_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.FROZEN_MOB_TOP_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.FROZEN_MOB_BOTTOM_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.ICE_WALL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.CLEAR_ICE, RenderLayer.getTranslucent());

        EntityRendererRegistry.register(LightEntities.EARTH_GOLEM, EarthGolemEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_EARTH_GOLEM_LAYER, EarthGolemEntityModel::getTexturedModelData);

        ClientConfig.reloadConfig();

        ClientTickEvents.END_CLIENT_TICK.register((minecraftClient -> {
            //This is done as to not display another Light Ready icon when it just triggered
            if(waitForNext){
                if(tickCounter == 20*seconds){
                    waitForNext = false;
                    tickCounter = 0;
                }
                tickCounter++;
            }else if(lightReady){
                if(tickCounter == 20*seconds){
                    lightReady = false;
                    tickCounter = 0;
                }
                tickCounter++;
            }


       }));
    }

    public static boolean isLightReady(){
        return lightReady;
    }

    public static void setLightReady(boolean b){
        lightReady = b;
    }

    public static boolean hasUsedCharge(){
        return usedCharge;
    }

    public static void setUsedCharge(boolean b){
        usedCharge = b;
    }
    public static boolean shouldDrawChargesCount(){
        return shouldDrawChargesCount;
    }

    public static void setShouldDrawChargesCount(boolean b){
        shouldDrawChargesCount = b;
    }

    public static void setWaitForNext(boolean b){
        waitForNext = b;
    }

    public static boolean isAutoActivationAllowed(){
        return allowAutoActivation;
    }

    /**How much should a player have the opportunity to press the button in seconds
     *
     * Currently fixed to 10s ish*/
    public void setDelay(int delay){
        seconds = delay;
    }

    private void registerLightReadyPacket(){
        ClientPlayNetworking.registerGlobalReceiver(LightReadyPacketS2C.ID, ((client, handler, buf, responseSender) -> {
            var results = LightReadyPacketS2C.read(buf);

            client.execute(() -> {
                try{
                    if(!results){
                        setLightReady(false);
                    }
                    if(!isLightReady()){
                        setLightReady(true);
                        if(client.player != null){
                            client.player.playSound(LightSounds.LIGHT_READY, 1f, 0.63f);
                        }

                    }
                    tickCounter = 0;
                }catch (NoSuchElementException e){
                    LOGGER.warn("No value in the packet, probably not a big problem");
                }catch (Exception e){
                    LOGGER.error("There was an error while getting the packet!");
                    e.printStackTrace();
                }
            });
        }));
    }

    private void registerConfigOptionsSyncPacket(){
        ClientPlayNetworking.registerGlobalReceiver(ConfigOptionsSyncPacketS2C.ID, ((client, handler, buf, responseSender) -> {
            Map<String, Boolean> results = ConfigOptionsSyncPacketS2C.readBooleans(buf);

            client.execute(() -> {
                try{
                    allowAutoActivation = results.get(ConfigPacketConstants.AUTO_LIGHT_ACTIVATION);
                }catch (NoSuchElementException e){
                    LOGGER.warn("No value in the packet, probably not a big problem");
                }catch (Exception e){
                    LOGGER.error("There was an error while getting the packet!");
                    e.printStackTrace();
                }
            });
        }));
    }

    private void registerRenderRunesPacket(){
        LOGGER.debug("Registering runes render packet receiver on client...");
        ClientPlayNetworking.registerGlobalReceiver(RenderRunePacketS2C.ID, ((client, handler, buf, responseSender) -> {
            var results = RenderRunePacketS2C.read(buf);

            client.execute(() -> {
                try{
                    event_handler.renderRunes(results, client.player);
                }catch (NoSuchElementException e){
                    LOGGER.warn("No value in the packet, probably not a big problem");
                }catch (Exception e){
                    LOGGER.error("There was an error while getting the packet!");
                    e.printStackTrace();
                }
            });
        }));
    }

    private void registerPlayRenderEffectPacket(){
        LOGGER.info("Registering play render effect packet receiver on client...");
        ClientPlayNetworking.registerGlobalReceiver(PlayRenderEffectPacketS2C.ID, ((client, handler, buf, responseSender) -> {
            RenderEffect effect = PlayRenderEffectPacketS2C.read(buf);

            client.execute(() -> {
                try{
                    if(client.player != null && effect != null){
                        IRenderEffectsEntity player = (IRenderEffectsEntity) client.player;
                        player.lightWithin$renderEffect(effect);
                    }else{
                        LOGGER.warn("The client player was null can't play effect animation");
                    }
                }catch (NoSuchElementException e){
                    LOGGER.warn("No value in the packet, probably not a big problem");
                }catch (Exception e){
                    LOGGER.error("There was an error while getting the packet!");
                    e.printStackTrace();
                }
            });
        }));
    }

    /**Create a config screen for ModMenu if YACL is present, or
     * a confirmation screen otherwise to tell you to download yacl*/
    public static Screen createConfigScreen(Screen parent) {
        if (!FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
            return new ConfirmScreen((result) -> {
                if (result) {
                    Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/yacl/versions"));
                }
                MinecraftClient.getInstance().setScreen(parent);
            },
                    Text.literal("You need to install YACL"), Text.literal("To modify the config file with a GUI you need to install YACL. Click on yes to open the modrinth page to download it."), ScreenTexts.YES, ScreenTexts.NO);
        } else {
            return YaclScreenMaker.getScreen(parent);
        }
    }

    private void registerWindLightVelocityPacket(){
        LOGGER.info("Registering windlight velocity packet receiver on client...");
        ClientPlayNetworking.registerGlobalReceiver(WindLightVelocityPacketS2C.ID, ((client, handler, buf, responseSender) -> {
            var results = WindLightVelocityPacketS2C.read(buf);

            client.execute(() -> {
                try{
                    assert client.player != null;
                    client.player.setVelocity(results);
                    //client.player.move(MovementType.SELF, client.player.getVelocity());

                }catch (NoSuchElementException e){
                    LOGGER.warn("No value in the packet, probably not a big problem");
                }catch (Exception e){
                    LOGGER.error("There was an error while getting the packet!");
                    e.printStackTrace();
                }
            });
        }));
    }
}
