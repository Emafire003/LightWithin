package me.emafire003.dev.lightwithin.client;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.blocks.LightBlocks;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.LuxDialogue;
import me.emafire003.dev.lightwithin.client.screens.LuxdialogueScreens;
import me.emafire003.dev.lightwithin.client.shaders.LightShaders;
import me.emafire003.dev.lightwithin.commands.client.ClientLightCommands;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.compat.yacl.YaclScreenMaker;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.entities.LightEntities;
import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntityModel;
import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntityRenderer;
import me.emafire003.dev.lightwithin.lights.ForestAuraLight;
import me.emafire003.dev.lightwithin.networking.*;
import me.emafire003.dev.lightwithin.particles.LightParticle;
import me.emafire003.dev.lightwithin.particles.LightTypeParticleV3;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightningParticle;
import me.emafire003.dev.lightwithin.particles.coloredpuff.ColoredPuffParticle;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.ConfigPacketConstants;
import me.emafire003.dev.lightwithin.util.ForestAuraRelation;
import me.emafire003.dev.lightwithin.util.IRenderEffectsEntity;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
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
import net.minecraft.entity.Entity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.Map;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

@Environment(EnvType.CLIENT)
public class LightWithinClient implements ClientModInitializer {

    private static boolean lightReady = false;
    private static boolean waitForNext = false;
    private static boolean usedCharge = false;
    private static boolean allowAutoActivation = false;
    int seconds = 10;
    int tickCounter = 0;
    static RendererEventHandler event_handler = new RendererEventHandler();

    /// Says if the BGM music for the luxcognita dialogue is playing or not
    private static boolean isLuxcognitaBGMPlaying = false;
    /// The tick counter for the luxcognita BGM music
    private static int luxcognitaBGMTicker = -1;

    private static boolean shouldDrawChargesCount = true;

    public static final EntityModelLayer MODEL_EARTH_GOLEM_LAYER = new EntityModelLayer(new Identifier(LightWithin.MOD_ID, "earth_golem"), "main");

    //The first one is the player the second one is the entity
    private static final List<UUID> entitiesGlowingForPlayer = new ArrayList<>();

    //If the player has the forest aura effect they will see the things nearby.
    // The entities get added to the list only when the player has said effect. So they must have the forest light and stuff.
    // The only player that will see the entities glowing is the client player

    @Override
    public void onInitializeClient() {
        ActivationKey.register();
        registerLightReadyPacket();
        registerPlayRenderEffectPacket();
        registerWindLightVelocityPacket();
        registerConfigOptionsSyncPacket();
        registerGlowingEntitiesPacket();
        registerLuxdreamAttackedScreenPacket();

        registerParticlesRenderer();
        LightShaders.registerShaders();


        event_handler.registerRenderEvent();
        event_handler.registerRunesRenderer();

        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.FROZEN_PLAYER_TOP_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.FROZEN_PLAYER_BOTTOM_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.FROZEN_MOB_TOP_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.FROZEN_MOB_BOTTOM_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.ICE_WALL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(LightBlocks.CLEAR_ICE, RenderLayer.getTranslucent());

        EntityRendererRegistry.register(LightEntities.EARTH_GOLEM, EarthGolemEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_EARTH_GOLEM_LAYER, EarthGolemEntityModel::getTexturedModelData);

        ClientConfig.reloadConfig();
        ClientCommandRegistrationCallback.EVENT.register(ClientLightCommands::registerCommands);
        //TODO remove once finished
        LuxDialogue def = new LuxDialogue();
        def.serialize();

        ClientLifecycleEvents.CLIENT_STARTED.register( minecraftClient -> {
            LuxdialogueScreens.registerDialogueScreens();
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraftClient -> {
            LuxdialogueScreens.LUXDIALOGUE_SCREENS.clear();
        });

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

    public static List<UUID> getEntitiesGlowingForPlayer() {
        return entitiesGlowingForPlayer;
    }

    public void registerParticlesRenderer(){

        LightWithin.INNERLIGHT_REGISTRY.forEach( innerLight -> ParticleFactoryRegistry.getInstance().register(LightParticles.TYPES_PARTICLES.get(innerLight.getLightId()), LightTypeParticleV3.Factory::new));

        ParticleFactoryRegistry.getInstance().register(LightParticles.LIGHT_PARTICLE, LightParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LightParticles.SHINE_PARTICLE, LightParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(LightParticles.LIGHTNING_PARTICLE, LightningParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(LightParticles.COLORED_PUFF_PARTICLE, ColoredPuffParticle.Factory::new);
    }

    /**How much should a player have the opportunity to press the button in seconds
     * <p>
     * Currently fixed to 10s ish*/
    public void setDelay(int delay){
        seconds = delay;
    }

    public static RendererEventHandler getRendererEventHandler(){
        return event_handler;
    }

    private void registerLightReadyPacket(){
        ClientPlayNetworking.registerGlobalReceiver(LightReadyPacketS2C.ID, ((client, handler, buf, responseSender) -> {
            var results = LightReadyPacketS2C.read(buf);

            client.execute(() -> {
                try{
                    if(!results){
                        setLightReady(false);
                        tickCounter = 0;
                        return;
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


    private void registerPlayRenderEffectPacket(){
        LOGGER.debug("Registering play render effect packet receiver on client...");
        ClientPlayNetworking.registerGlobalReceiver(PlayRenderEffectPacketS2C.ID, ((client, handler, buf, responseSender) -> {
            Pair<RenderEffect, Integer> effectAndTarget = PlayRenderEffectPacketS2C.readTarget(buf);
            if(effectAndTarget == null){
                LOGGER.error("Error! The PlayRenderEffectPacket had a null pair payload!");
                return;
            }
            RenderEffect effect = effectAndTarget.getFirst();
            int targetId = effectAndTarget.getSecond();

            client.execute(() -> {
                try{
                    if(client.player != null && effect != null){

                        if(effect.equals(RenderEffect.LIGHT_RAYS)){
                            if(targetId == -1){
                                IRenderEffectsEntity player = (IRenderEffectsEntity) client.player;
                                player.lightWithin$renderEffect(effect, (int) (4.5*20));
                            }else{
                                Entity target = client.player.getWorld().getEntityById(targetId);
                                if(target == null){
                                    LOGGER.error("Error! The entity got from the ID in the PlayRenderEffectPacket is null!");
                                    return;
                                }
                                ((IRenderEffectsEntity)target).lightWithin$renderEffect(effect, (int) (4.5*20));
                            }
                        }else if(effect.equals(RenderEffect.FORCED_LIGHT_RAYS)){
                            if(targetId == -1){
                                IRenderEffectsEntity player = (IRenderEffectsEntity) client.player;
                                player.lightWithin$renderEffect(effect, (int) (4.5*20));
                                //TODO add a better "error/fatigue"ish sound instead
                                //client.player.playSound(LightSounds.LIGHT_CHARGED, 0.7f, 0.7f);
                            }else{
                                Entity target = client.player.getWorld().getEntityById(targetId);
                                if(target == null){
                                    LOGGER.error("Error! The entity got from the ID in the PlayRenderEffectPacket is null!");
                                    return;
                                }
                                ((IRenderEffectsEntity)target).lightWithin$renderEffect(effect, (int) (4.5*20));
                            }

                        }

                        else if(effect.equals(RenderEffect.LUXCOGNITA_SCREEN)){
                            MinecraftClient.getInstance().setScreen(LuxdialogueScreens.LUXDIALOGUE_SCREENS.get("intro/intro"));
                            //MinecraftClient.getInstance().setScreen(new LuxcognitaScreenV1(Text.literal("LightWithin - Luxcognita Dialogue")));
                        }

                        else if(effect.equals(RenderEffect.RUNES)){
                            event_handler.renderRunes();
                            //event_handler.playLightSound(LightWithin.LIGHT_COMPONENT.get(client.player).getType());
                        }


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

    private void registerLuxdreamAttackedScreenPacket(){
        ClientPlayNetworking.registerGlobalReceiver(LuxdreamAttackScreenPacketS2C.ID, ((client, handler, buf, responseSender) -> {

            client.execute(() -> {
                MinecraftClient.getInstance().setScreen(LuxdialogueScreens.LUXDIALOGUE_SCREENS.get("attacked"));
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

    /** Sets some entities glowing for the player when the packet is received*/
    private void registerGlowingEntitiesPacket(){
        LOGGER.debug("Registering glowing entities packet...");
        ClientPlayNetworking.registerGlobalReceiver(GlowEntitiesPacketS2C.ID, ((client, handler, buf, responseSender) -> {
            List<Pair<UUID, ForestAuraRelation>> results = GlowEntitiesPacketS2C.read(buf);

            client.execute(() -> {
                try{
                    if(results == null){
                        LOGGER.error("The glowing entities list received is empty!");
                        return;
                    }if(client.player == null){
                        LOGGER.error("The client player is null!");
                        return;
                    }else if(results.size() == 1 && results.get(0).getFirst().equals(new UUID(0,0))){
                        //Clears CGL exclusive colors on client side.
                        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
                            entitiesGlowingForPlayer.forEach(uuid -> {
                                Entity entity = null;
                                for(Entity entity1 : client.player.clientWorld.getEntities()){
                                    if(entity1.getUuid().equals(uuid)){
                                        entity = entity1;
                                    }
                                }
                                if(entity != null){
                                    CGLCompat.getLib().clearExclusiveColorFor(entity, client.player, false);
                                }
                            });
                        }

                        entitiesGlowingForPlayer.clear();
                        return;
                    }
                    //If nothing else, it means it's ok to make them glow:

                    results.forEach(uuidRelationPair -> {
                        entitiesGlowingForPlayer.add(uuidRelationPair.getFirst());
                        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){

                            Entity entity = null;
                            for(Entity entity1 : client.player.clientWorld.getEntities()){
                                if(entity1.getUuid().equals(uuidRelationPair.getFirst())){
                                    entity = entity1;
                                }
                            }
                            if(entity == null){
                                LOGGER.error("Error! Can't find entity with uuid: {}", uuidRelationPair.getFirst());
                                return;
                            }

                            if(uuidRelationPair.getSecond().equals(ForestAuraRelation.ALLY)){
                                CGLCompat.getLib().setExclusiveColorFor(entity, ClientConfig.FORESTAURA_ALLY_COLOR, client.player);
                            }else if(uuidRelationPair.getSecond().equals(ForestAuraRelation.ENEMY)){
                                CGLCompat.getLib().setExclusiveColorFor(entity, ClientConfig.FORESTAURA_ENEMY_COLOR, client.player);
                            }else{
                                CGLCompat.getLib().setExclusiveColorFor(entity, ForestAuraLight.COLOR, client.player);
                            }
                        }
                    } );

                }catch (NoSuchElementException e){
                    LOGGER.warn("No value in the packet, probably not a big problem");
                }catch (Exception e){
                    LOGGER.error("There was an error while getting the packet!");
                    e.printStackTrace();
                }
            });
        }));
    }

    public static boolean isIsLuxcognitaBGMPlaying() {
        return isLuxcognitaBGMPlaying;
    }

    public static void setIsLuxcognitaBGMPlaying(boolean isLuxcognitaBGMPlaying) {
        LightWithinClient.isLuxcognitaBGMPlaying = isLuxcognitaBGMPlaying;
    }

    public static int getLuxcognitaBGMTicker() {
        return luxcognitaBGMTicker;
    }

    public static void setLuxcognitaBGMTicker(int luxcognitaBGMTicker) {
        LightWithinClient.luxcognitaBGMTicker = luxcognitaBGMTicker;
    }

}
