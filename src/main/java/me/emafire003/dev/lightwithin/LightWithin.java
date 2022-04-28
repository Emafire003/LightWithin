package me.emafire003.dev.lightwithin;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.lightwithin.commands.LWCommandRegister;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.lights.DefenseLight;
import me.emafire003.dev.lightwithin.lights.HealLight;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.lights.StrenghtLight;
import me.emafire003.dev.lightwithin.networking.LightReadyPacketS2C;
import me.emafire003.dev.lightwithin.networking.LightUsedPacketC2S;
import me.emafire003.dev.lightwithin.networking.RenderRunePacketS2C;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class LightWithin implements ModInitializer, EntityComponentInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "lightwithin";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static int box_expansion_amout = 6;

	public static final ComponentKey<LightComponent> LIGHT_COMPONENT =
			ComponentRegistry.getOrCreate(new Identifier(MOD_ID, "light_component"), LightComponent.class);

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
		LightSounds.registerSounds();
		LWCommandRegister.registerCommands();
		LightEffects.registerModEffects();
		LightItems.registerItems();

	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(LIGHT_COMPONENT, LightComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}

	private static void registerLightUsedPacket(){
		ServerPlayNetworking.registerGlobalReceiver(LightUsedPacketC2S.ID, (((server, player, handler, buf, responseSender) -> {
			if(player.getWorld().isClient){
				return;
			}
			var results = LightUsedPacketC2S.read(buf);
			server.execute(() -> {
				try{
					if(results){
						activateLight(player);
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

	public static void activateLight(ServerPlayerEntity player){
		if(!(player.hasStatusEffect(LightEffects.LIGHT_FATIGUE) || player.hasStatusEffect(LightEffects.LIGHT_ACTIVE))){
			player.sendMessage(new LiteralText("Ok not in cooldown, starting the ticking"), false);
			player.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_ACTIVE, 20*LIGHT_COMPONENT.get(player).getDuration()));
		}else{
			return;
		}
		LightComponent component = LIGHT_COMPONENT.get(player);
		InnerLightType type = component.getType();
		if(type.equals(InnerLightType.NONE)){
			return;
		}
		if(type.equals(InnerLightType.HEAL)){
			activateHeal(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
			player.playSound(LightSounds.HEAL_LIGHT, 1f, 0.9f);
			player.sendMessage(new LiteralText("Tried to play the heal sound LightWitihin.activateLight..."), false);
		}else if(type.equals(InnerLightType.DEFENCE)){
			activateDefense(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}else if(type.equals(InnerLightType.STRENGTH)){
			activateStrength(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}
		//for now defaults here
		else{
			activateHeal(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}
		//TODO config toggable
		player.setGlowing(true);
		sendRenderRunePacket(player, type);
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
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true));
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
			targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true)));
		}
		player.sendMessage(new LiteralText("Ok light triggered"), false);
		new HealLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Defense Light=======================
	public static void activateDefense(LightComponent component, ServerPlayerEntity player){
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
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true));
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
			targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true)));
		}
		player.sendMessage(new LiteralText("Ok light triggered"), false);
		new DefenseLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Defense Light=======================
	public static void activateStrength(LightComponent component, ServerPlayerEntity player){
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
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true));
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
			targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true)));
		}
		player.sendMessage(new LiteralText("Ok light triggered"), false);
		new StrenghtLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	public static void sendRenderRunePacket(ServerPlayerEntity player, InnerLightType type){
		try{
			ServerPlayNetworking.send(player, RenderRunePacketS2C.ID, new RenderRunePacketS2C(type));
		}catch(Exception e){
			LOGGER.error("FAILED to send data packets to the client!");
			e.printStackTrace();
			return;
		}
	}

}
