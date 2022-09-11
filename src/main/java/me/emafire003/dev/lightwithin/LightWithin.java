package me.emafire003.dev.lightwithin;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.lightwithin.blocks.LightBlocks;
import me.emafire003.dev.lightwithin.commands.LightCommands;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.entities.LightEntities;
import me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.lights.*;
import me.emafire003.dev.lightwithin.networking.LightUsedPacketC2S;
import me.emafire003.dev.lightwithin.networking.RenderRunePacketS2C;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.LootTableModifier;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

import static java.util.Map.entry;

public class LightWithin implements ModInitializer, EntityComponentInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "lightwithin";
	public static final String PREFIX_MSG = "[LightWithin] ";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static int box_expansion_amount = 6;

	private static boolean debug = false;
	public static Path PATH = Path.of(FabricLoader.getInstance().getConfigDir() + "/" + MOD_ID + "/");

	public static final Map<InnerLightType, List<TargetType>> possible_targets = Map.ofEntries(
			entry(InnerLightType.HEAL, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.OTHER)),
			entry(InnerLightType.DEFENCE, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.OTHER)),
			entry(InnerLightType.STRENGTH, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.OTHER)),
			entry(InnerLightType.BLAZING, Arrays.asList(TargetType.ENEMIES, TargetType.ALL)),
			entry(InnerLightType.FROST, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.ENEMIES, TargetType.ALL)),
			entry(InnerLightType.EARTHEN, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.ENEMIES, TargetType.OTHER)),
			entry(InnerLightType.WIND, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.OTHER)),
			entry(InnerLightType.FROG, List.of(TargetType.ALL))
	);


	public static final ComponentKey<LightComponent> LIGHT_COMPONENT =
			ComponentRegistry.getOrCreate(new Identifier(MOD_ID, "light_component"), LightComponent.class);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LightTriggeringAndEvents.registerListeners();
		registerLightUsedPacket();
		LightSounds.registerSounds();
		LightEffects.registerModEffects();
		LightItems.registerItems();
		LightParticles.registerParticles();
		LightBlocks.registerBlocks();
		LootTableModifier.modifyLootTables();
		LightCommands.registerArguments();
		LightEntities.registerEntities();
		CommandRegistrationCallback.EVENT.register(LightCommands::registerCommands);


		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			ColoredGlowLib.setOverrideTeamColors(true);
			box_expansion_amount = Config.AREA_OF_SEARCH_FOR_ENTITIES;
			if(box_expansion_amount == 0){
				box_expansion_amount = 6;
			}
			Config.reloadConfig();
		});

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
					if(results || Config.AUTO_LIGHT_ACTIVATION){
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

	public static boolean isPlayerInCooldown(PlayerEntity user){
		return user.hasStatusEffect(LightEffects.LIGHT_FATIGUE) || user.hasStatusEffect(LightEffects.LIGHT_ACTIVE);
	}

	public static void activateLight(PlayerEntity player){
		if(!(player.hasStatusEffect(LightEffects.LIGHT_FATIGUE) || player.hasStatusEffect(LightEffects.LIGHT_ACTIVE))){

			if(debug){
				player.sendMessage(Text.literal("Ok not in cooldown, starting the ticking"), false);
			}
			player.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_ACTIVE, (int) (Config.DURATION_MULTIPLIER*20*LIGHT_COMPONENT.get(player).getDuration())));
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
		}else if(type.equals(InnerLightType.DEFENCE)){
			activateDefense(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}else if(type.equals(InnerLightType.STRENGTH)){
			activateStrength(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}else if(type.equals(InnerLightType.BLAZING)){
			activateBlazing(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}else if(type.equals(InnerLightType.FROST)){
			activateFrost(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}else if(type.equals(InnerLightType.EARTHEN)){
			activateEarthen(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}else if(type.equals(InnerLightType.WIND)){
			activateWind(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}else if(type.equals(InnerLightType.FROG)){
			activateFrog(component, player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}
		//for now defaults here
		else{
			activateHeal(component, (ServerPlayerEntity) player);
			component.setPrevColor(ColoredGlowLib.getEntityColor(player));
		}

		if(Config.PLAYER_GLOWS){
			player.setGlowing(true);
		}
		if(!player.getWorld().isClient){
			LightParticlesUtil.spawnDefaultLightParticleSequence((ServerPlayerEntity) player);
			sendRenderRunePacket((ServerPlayerEntity) player, type);
		}

	}

	//=======================HEAL LIGHT=======================
	public static void activateHeal(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();

		if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.heal.allies"), true);
		}
		//There could be a bug where the player stands near only 1 ally that is 50% life or lower and
		// then enderpearls to other companions and cures them. But it's ok because of lore,
		// like the light saw an ally struggling and activated. Then it heals whoever is near.
		// It's not a bug, it's a feature now.
		//Yay.
		else if(component.getTargets().equals(TargetType.ALLIES)){
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
			for(LivingEntity ent : entities){
				// may need this to prevent bugs EDIT i don't even remember what "this" referred to eheh
				if(CheckUtils.CheckAllies.checkAlly(player, ent)){
					targets.add(ent);
				}else if(ent instanceof TameableEntity){
					if(player.equals(((TameableEntity) ent).getOwner())){
						targets.add(ent);
					}
				}
			}
			player.sendMessage(Text.translatable("light.description.activation.heal.allies"), true);

		}

		//Same here
		else if(component.getTargets().equals(TargetType.OTHER)){
			if(player.getHealth() <= (player.getMaxHealth())*50/100){
				targets.add(player);
			}
			targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true)));
			player.sendMessage(Text.translatable("light.description.activation.heal.other"), true);
		}
		if(debug){
			player.sendMessage(Text.literal("Ok light triggered"), false);
		}
		new HealLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Defense Light=======================
	public static void activateDefense(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.defense.self"), true);
		}
		else if(component.getTargets().equals(TargetType.ALLIES)){
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
			for(LivingEntity ent : entities){
				//TODO integration with other mods that implement allies stuff
				//TODO may need this to prevent bugs
				if(/*!entity.equals(ent) && */CheckUtils.CheckAllies.checkAlly(player, ent)){
					targets.add(ent);
				}else if(ent instanceof TameableEntity){
					if(player.equals(((TameableEntity) ent).getOwner())){
						targets.add(ent);
					}
				}
			}
			player.sendMessage(Text.translatable("light.description.activation.defense.allies"), true);
		}

		//Same here
		else if(component.getTargets().equals(TargetType.OTHER)){
			if(player.getHealth() <= (player.getMaxHealth())*50/100){
				targets.add(player);
			}
			targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true)));
			player.sendMessage(Text.translatable("light.description.activation.defense.other"), true);
		}
		if(debug){
			player.sendMessage(Text.literal("Ok light triggered"), false);
		}
		new DefenseLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Strength Light=======================
	public static void activateStrength(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();

		if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.strength.self"), true);
		}
		else if(component.getTargets().equals(TargetType.ALLIES)){
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
			for(LivingEntity ent : entities){
				if(/*!entity.equals(ent) && */CheckUtils.CheckAllies.checkAlly(player, ent)){
					targets.add(ent);
				}else if(ent instanceof TameableEntity){
					if(player.equals(((TameableEntity) ent).getOwner())){
						targets.add(ent);
					}
				}
			}
			player.sendMessage(Text.translatable("light.description.activation.strength.allies"), true);

		}

		//Same here
		else if(component.getTargets().equals(TargetType.OTHER)){
			if(player.getHealth() <= (player.getMaxHealth())*50/100){
				targets.add(player);
			}
			targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true)));
			player.sendMessage(Text.translatable("light.description.activation.strength.other"), true);
		}


		if(debug){
			player.sendMessage(Text.literal("Ok light triggered"), false);
		}
		new StrengthLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Blazing Light=======================
	public static void activateBlazing(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		//TODO add config option for setting the amout before it triggers (look up)

		if(component.getTargets().equals(TargetType.ALL)){
			targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true)));
			targets.remove(player);
			player.sendMessage(Text.translatable("light.description.activation.blazing.all"), true);
		}

		else if(component.getTargets().equals(TargetType.ENEMIES)){
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
			for(LivingEntity ent : entities){
				if(ent instanceof HostileEntity && !CheckUtils.CheckAllies.checkAlly(player, ent)){
					targets.add(ent);
				}
			}
			player.sendMessage(Text.translatable("light.description.activation.blazing.enemies"), true);
		}


		if(debug){
			player.sendMessage(Text.literal("Ok light triggered"), false);
		}
		new BlazingLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Frost Light=======================
	public static void activateFrost(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		if(component.getTargets().equals(TargetType.ALL)){
			targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true)));
			targets.remove(player);
			player.sendMessage(Text.translatable("light.description.activation.frost.all"), true);
		}

		else if(component.getTargets().equals(TargetType.ENEMIES)){
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
			for(LivingEntity ent : entities){
				if(ent instanceof HostileEntity && !CheckUtils.CheckAllies.checkAlly(player, ent)){
					targets.add(ent);
				}
			}
			player.sendMessage(Text.translatable("light.description.activation.frost.enemies"), true);
		}else if(component.getTargets().equals(TargetType.ALLIES)){
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
			for(LivingEntity ent : entities){
				if(/*!entity.equals(ent) && */CheckUtils.CheckAllies.checkAlly(player, ent)){
					targets.add(ent);
				}else if(ent instanceof TameableEntity){
					if(player.equals(((TameableEntity) ent).getOwner())){
						targets.add(ent);
					}
				}
			}
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.frost.allies"), true);
		}if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.frost.self"), true);
		}

		if(debug){
			player.sendMessage(Text.literal("Ok light triggered"), false);
		}
		new FrostLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Earthen Light=======================
	public static void activateEarthen(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		if(component.getTargets().equals(TargetType.OTHER)){
			player.sendMessage(Text.translatable("light.description.activation.earthen.other"), true);
		}

		else if(component.getTargets().equals(TargetType.ENEMIES)){
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
			for(LivingEntity ent : entities){
				if(ent instanceof HostileEntity && !CheckUtils.CheckAllies.checkAlly(player, ent)){
					targets.add(ent);
				}
			}
			player.sendMessage(Text.translatable("light.description.activation.earthen.enemies"), true);
		}else if(component.getTargets().equals(TargetType.ALLIES)){
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
			for(LivingEntity ent : entities){
				if(/*!entity.equals(ent) && */CheckUtils.CheckAllies.checkAlly(player, ent)){
					targets.add(ent);
				}else if(ent instanceof TameableEntity){
					if(player.equals(((TameableEntity) ent).getOwner())){
						targets.add(ent);
					}
				}
			}
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.earthen.allies"), true);
		}if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.earthen.self"), true);
		}

		if(debug){
			player.sendMessage(Text.literal("Ok light triggered"), false);
		}
		new EarthenLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Wind Light=======================
	public static void activateWind(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		if(component.getTargets().equals(TargetType.ALLIES)){
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
			for(LivingEntity ent : entities){
				if(/*!entity.equals(ent) && */CheckUtils.CheckAllies.checkAlly(player, ent)){
					targets.add(ent);
				}else if(ent instanceof TameableEntity){
					if(player.equals(((TameableEntity) ent).getOwner())){
						targets.add(ent);
					}
				}
			}
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.wind.allies"), true);
		}else if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.wind.self"), true);
		}else if(component.getTargets().equals(TargetType.OTHER)){
			targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true)));
			targets.remove(player);
			player.sendMessage(Text.translatable("light.description.activation.wind.other"), true);
		}

		if(debug){
			player.sendMessage(Text.literal("Ok light triggered"), false);
		}
		new WindLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Frog Light=======================
	public static void activateFrog(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();

		targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true)));
		player.sendMessage(Text.translatable("light.description.activation.frog"), true);

		new FrogLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	public static void sendRenderRunePacket(ServerPlayerEntity player, InnerLightType type){
		try{
			ServerPlayNetworking.send(player, RenderRunePacketS2C.ID, new RenderRunePacketS2C(type));
		}catch(Exception e){
			LOGGER.error("FAILED to send data packets to the client!");
			e.printStackTrace();
		}
	}

}
