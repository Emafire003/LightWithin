package me.emafire003.dev.lightwithin;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents;
import me.emafire003.dev.lightwithin.lights.HealLight;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.networking.LightUsedPacketC2S;
import me.emafire003.dev.lightwithin.util.CacheSystem;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents.sendReadyPacket;

public class LightWithin implements ModInitializer, EntityComponentInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "lightwithin";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ComponentKey<LightComponent> LIGHT_COMPONENT =
			ComponentRegistry.getOrCreate(new Identifier(MOD_ID, "light_component"), LightComponent.class);

	public static List<UUID> light_used = new ArrayList<>();

	//per selectare la inner light usare un enum
	//LightWithin -> mod, InnerLights the powers
	//TODO vocal sayout loud of lightname when activating
	//The trigger allows the player to activate the light, but the light is triggered only on a key press

	//TODO On player join it calculates the type and variables and sets them into its nbt data, then i check that nbt data
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LightTriggeringAndEvents.registerListeners();
		registerLightUsedPacket();

	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(LIGHT_COMPONENT, LightComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}


	private static void registerLightUsedPacket(){
		ServerPlayNetworking.registerGlobalReceiver(LightUsedPacketC2S.ID, (((server, player, handler, buf, responseSender) -> {
			var results = LightUsedPacketC2S.read(buf);
			player.sendMessage(new LiteralText("yep it runs. uhm"), false);

			server.execute(() -> {
				try{
					if(results){
						light_used.add(player.getUuid());

						LightComponent component = LIGHT_COMPONENT.get(player);
						if(component.getType().equals(InnerLightType.NONE)){
							return;
						}
						if(component.getTargets().equals(InnerLightType.HEAL)){
							activateHeal(component, player);
						}
						//for now defaults here
						else{
							activateHeal(component, player);
						}
					}
				}catch (NoSuchElementException e){
					LOGGER.warn("No value in the packet, probably not a big problem");
				}catch (Exception e){
					LOGGER.error("There was an error while getting the packet!");
					e.printStackTrace();
				}
			});
		})));
	}

	//=======================HEAL LIGHT=======================
	public static void activateHeal(LightComponent component, ServerPlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		//TODO add config option for setting the amout before it triggers

		if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
		}
		//There could be a bug where the player stands near only 1 ally that is 50% life or lower and
		// then enderpearls to other companions and cures them. But it's ok because of lore,
		// like the light saw a an ally struggling and activated. Then it heals whoever is near.
		// It's not a bug, it's a feature now.
		//Yay.
		else if(component.getTargets().equals(TargetType.ALLIES)){
			//TODO set box dimensions configable
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(6), (entity1 -> true));
			for(LivingEntity ent : entities){
				//TODO integration with other mods that implement allies stuff
				//TODO may need this to prevent bugs
				if(/*!entity.equals(ent) && */ent.getScoreboardTeam() != null && ent.getScoreboardTeam().isEqual(player.getScoreboardTeam())){
					targets.add(ent);
				}
			}
		}

		//Same here
		else if(component.getTargets().equals(TargetType.OTHER)){
			if(player.getHealth() <= (player.getMaxHealth())*50/100){
				targets.add(player);
			}
			targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(6), (entity1 -> true)));
		}
		player.sendMessage(new LiteralText("Ok light triggered"), false);
		new HealLight(targets, component.getCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

}
