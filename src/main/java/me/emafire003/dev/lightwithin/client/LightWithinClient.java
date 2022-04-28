package me.emafire003.dev.lightwithin.client;

import me.emafire003.dev.lightwithin.networking.LightReadyPacketS2C;
import me.emafire003.dev.lightwithin.networking.RenderRunePacketS2C;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.x150.renderer.event.Events;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.LiteralText;

import java.util.NoSuchElementException;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

@Environment(EnvType.CLIENT)
public class LightWithinClient implements ClientModInitializer {

    private static boolean lightReady = false;
    int seconds = 10;
    int tickCounter = 0;
    EventHandler event_handler = new EventHandler();

    @Override
    public void onInitializeClient() {
       ActivationKey.register();
       registerLightReadyPacket();
       registerRenderRunesPacket();
       Events.registerEventHandlerClass(event_handler);
       event_handler.registerRunesRenderer();



       ClientTickEvents.END_CLIENT_TICK.register((minecraftClient -> {
            if(lightReady){
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

    /**How much should a player have the opportunity to press the button in seconds*/
    public void setDelay(int delay){
        seconds = delay;
    }

    private void registerLightReadyPacket(){
        ClientPlayNetworking.registerGlobalReceiver(LightReadyPacketS2C.ID, ((client, handler, buf, responseSender) -> {
            var results = LightReadyPacketS2C.read(buf);

            client.execute(() -> {
                try{
                    LOGGER.info("Ready Packet recived on client! Value: " + results);
                    if(!lightReady){
                        lightReady = results;
                        client.player.playSound(LightSounds.LIGHT_READY, 1f, 0.63f);
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

    private void registerRenderRunesPacket(){
        LOGGER.info("Registering runes render packet reciver on client...");
        ClientPlayNetworking.registerGlobalReceiver(RenderRunePacketS2C.ID, ((client, handler, buf, responseSender) -> {
            var results = RenderRunePacketS2C.read(buf);
            client.player.sendMessage(new LiteralText("render Pakcet recived on client"), false);

            client.execute(() -> {
                try{
                    client.player.sendMessage(new LiteralText("render Pakcet recived on client"), false);
                    event_handler.renderRunes(results);
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
