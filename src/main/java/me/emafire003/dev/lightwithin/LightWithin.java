package me.emafire003.dev.lightwithin;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.emafire003.dev.lightwithin.blocks.LightBlocks;
import me.emafire003.dev.lightwithin.commands.LightCommands;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.compat.flan.FlanCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.component.SummonedByComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.config.TriggerConfig;
import me.emafire003.dev.lightwithin.entities.LightEntities;
import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntity;
import me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents;
import me.emafire003.dev.lightwithin.events.PlayerJoinEvent;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.items.crafting.BrewRecipes;
import me.emafire003.dev.lightwithin.lights.*;
import me.emafire003.dev.lightwithin.networking.*;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.events.LightCreationAndEvent;
import me.emafire003.dev.lightwithin.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Map.entry;
import static me.emafire003.dev.lightwithin.lights.ForestAuraLight.FOREST_AURA_BLOCKS;

public class LightWithin implements ModInitializer, EntityComponentInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "lightwithin";
	public static final String PREFIX_MSG = "[LightWithin] ";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static int BOX_EXPANSION_AMOUNT = 6;

	private static final boolean debug = false;
	public static Path PATH = Path.of(FabricLoader.getInstance().getConfigDir() + "/" + MOD_ID + "/");

	public static boolean overrideTeamColorsPrev = false;

	public static List<UUID> USED_CHARGE_PLAYER_CACHE = new ArrayList<>();
	public static ConcurrentHashMap<UUID, Integer> CURRENTLY_READY_LIGHT_PLAYER_CACHE = new ConcurrentHashMap<>();

	/**
	 * This is a map of the possible targets for each target type
	 * <p>
	 * It is also used in determining the likelihood of each target type being generated,
	 * from left to right is more likely. The most probable is the one on the left*/
	public static final Map<InnerLightType, List<TargetType>> POSSIBLE_TARGETS = Map.ofEntries(
			entry(InnerLightType.HEAL, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT)),
			entry(InnerLightType.DEFENCE, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT)),
			entry(InnerLightType.STRENGTH, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT)),
			entry(InnerLightType.BLAZING, Arrays.asList(TargetType.ENEMIES, TargetType.ALL, TargetType.VARIANT)),
			entry(InnerLightType.FROST, Arrays.asList(TargetType.ENEMIES, TargetType.ALLIES, TargetType.ALL, TargetType.SELF)),
			entry(InnerLightType.EARTHEN, Arrays.asList(TargetType.SELF, TargetType.ENEMIES, TargetType.ALLIES, TargetType.VARIANT)),
			entry(InnerLightType.WIND, Arrays.asList(TargetType.SELF, TargetType.ALL, TargetType.ALLIES)),
			entry(InnerLightType.AQUA, Arrays.asList(TargetType.SELF, TargetType.ENEMIES, TargetType.ALLIES,  TargetType.ALL)),
			entry(InnerLightType.FOREST_AURA, Arrays.asList(TargetType.ALL, TargetType.SELF)),
			entry(InnerLightType.FROG, List.of(TargetType.ALL))
	);

	public static final ComponentKey<LightComponent> LIGHT_COMPONENT =
			ComponentRegistry.getOrCreate(new Identifier(MOD_ID, "light_component"), LightComponent.class);

	public static final ComponentKey<SummonedByComponent> SUMMONED_BY_COMPONENT =
			ComponentRegistry.getOrCreate(new Identifier(MOD_ID, "summoned_by_component"), SummonedByComponent.class);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LightCreationAndEvent.registerCreationListener();
		LightTriggeringAndEvents.registerListeners();
		registerLightUsedPacket();
		registerLightChargeConsumedPacket();
		registerReadyLightCacheRemover();
		registerSyncOptionsOnJoin();
		LightSounds.registerSounds();
		LightEffects.registerModEffects();
		LightItems.registerItems();
		LightParticles.registerParticles();
		LightBlocks.registerBlocks();
		LootTableModifier.modifyLootTables();
		LightCommands.registerArguments();
		LightEntities.registerEntities();
		registerTags();

		if(FabricLoader.getInstance().isModLoaded("flan")){
			FlanCompat.registerFlan();
		}
		CommandRegistrationCallback.EVENT.register(LightCommands::registerCommands);


		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			BrewRecipes.registerRecipes();
			BOX_EXPANSION_AMOUNT = Config.AREA_OF_SEARCH_FOR_ENTITIES;
			if(BOX_EXPANSION_AMOUNT == 0){
				BOX_EXPANSION_AMOUNT = 6;
			}
			try{
				Config.reloadConfig();
				BalanceConfig.reloadConfig();
				TriggerConfig.reloadConfig();
			}catch (Exception e){
				LOGGER.error("There was an error while loading the config files!");
				e.printStackTrace();
			}

			LightTriggerChecks.MIN_TRIGGER = TriggerConfig.TRIGGER_THRESHOLD;
		});

	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(LIGHT_COMPONENT, LightComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerFor(DrownedEntity.class, SUMMONED_BY_COMPONENT, SummonedByComponent::new);
		registry.registerFor(EarthGolemEntity.class, SUMMONED_BY_COMPONENT, SummonedByComponent::new);
	}

	private static void registerSyncOptionsOnJoin(){
		PlayerJoinEvent.EVENT.register((player, server) -> {
			if(player.getWorld().isClient){
				return ActionResult.PASS;
			}
			syncCustomConfigOptions(player);
			return ActionResult.PASS;
		});
	}

	public static void registerTags(){
		RegistryEntryList.Named<Block> forest_blocks = Registries.BLOCK.getOrCreateEntryList(FOREST_AURA_BLOCKS);
	}

	/**Sends a packet with updated config options to the client
	 * such as the auto light activation permission*/
	public static void syncCustomConfigOptions(ServerPlayerEntity player){
		Map<String, Boolean> booleanMap = new HashMap<>();
		booleanMap.put(ConfigPacketConstants.AUTO_LIGHT_ACTIVATION, Config.AUTO_LIGHT_ACTIVATION);
		//TODO if needed i'll add other settings ecct
		ConfigOptionsSyncPacketS2C optionsPacket = new ConfigOptionsSyncPacketS2C(booleanMap);
		ServerPlayNetworking.send(player, ConfigOptionsSyncPacketS2C.ID, optionsPacket);
	}

	private static void registerLightUsedPacket(){
		ServerPlayNetworking.registerGlobalReceiver(LightUsedPacketC2S.ID, (((server, player, handler, buf, responseSender) -> {
			if(player.getWorld().isClient){
				return;
			}
			var results = LightUsedPacketC2S.read(buf);
			server.execute(() -> {
				try{
					//Handles the LightCharge being used. If it used, results will be true.
					if(results){

						if(!CheckUtils.canActivateHere(player)){
							player.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.translatableWithFallback("light.charge.cant_use_here", "You are not allowed to use you InnerLight here!").formatted(Formatting.RED)));
							return;
						}

						//This could be laggy? Maybe?
						List<ServerPlayerEntity> players = player.getServerWorld().getPlayers();
						for(ServerPlayerEntity p : players){
							ServerPlayNetworking.send(p, PlayRenderEffectPacketS2C.ID, new PlayRenderEffectPacketS2C(RenderEffect.LIGHT_RAYS, player));
						}

						//TODO maybe also increase the max cooldown light-stat?
						//Currently just increases the cooldown. But the actual charges are fairly hard to get.
						USED_CHARGE_PLAYER_CACHE.add(player.getUuid());

						player.getWorld().playSound(player.getX(), player.getY(), player.getZ(), LightSounds.LIGHT_CHARGED, SoundCategory.PLAYERS, 1, 0.7f, true);

						if(Config.TARGET_FEEDBACK && Config.USED_CHARGE_COOLDOWN_MULTIPLIER > 1){
							player.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.translatable("light.charge.cooldown_message").formatted(Formatting.YELLOW)));
						}
					}
					activateLight(player);
				}catch (NoSuchElementException e){
					LOGGER.warn("No value in the packet!");
				}catch (Exception e){
					LOGGER.error("There was an error while getting the packet!");
					e.printStackTrace();
				}
			});
		})));
	}

	private static void registerLightChargeConsumedPacket(){
		ServerPlayNetworking.registerGlobalReceiver(LightChargeConsumedPacketC2S.ID, (((server, player, handler, buf, responseSender) -> {
			if(player.getWorld().isClient){
				return;
			}
			addToReadyList(player);
			//var results = LightUsedPacketC2S.read(buf);
			server.execute(() -> {
				try{

					///particle lightwithin:shine_particle ~ ~1 ~ 0.1 0.1 0.1 0.15 25 force
					if(!CheckUtils.canActivateHere(player)){
						return;
					}
					player.sendMessage(Text.translatable("light.charge.used").formatted(Formatting.YELLOW), true);

					((ServerWorld) player.getWorld()).spawnParticles(
							LightParticles.SHINE_PARTICLE, player.getX(), player.getY()+player.getDimensions(player.getPose()).height/2, player.getZ(),
							50, 0.1, 0.1, 0.1, 0.15
					);

					LIGHT_COMPONENT.get(player).removeLightCharges();
				}catch (NoSuchElementException e){
					LOGGER.warn("No value in the packet!");
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

		if(player.getWorld().isClient() || player.hasStatusEffect(LightEffects.LIGHT_FATIGUE)
				|| player.hasStatusEffect(LightEffects.LIGHT_ACTIVE)
		 		|| !CheckUtils.canActivateHere((ServerPlayerEntity) player)) {
			return;
		}

		CURRENTLY_READY_LIGHT_PLAYER_CACHE.remove(player.getUuid());

		player.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_ACTIVE, (20*LIGHT_COMPONENT.get(player).getDuration())));

		if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
			//A bit janky but should do the job. I hope.
			overrideTeamColorsPrev = CGLCompat.getLib().getOverrideTeamColors();
			CGLCompat.getLib().setOverrideTeamColors(true);
		}
		LightComponent component = LIGHT_COMPONENT.get(player);
		InnerLightType type = component.getType();
		if(type.equals(InnerLightType.NONE)){
			return;
		}

		if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
			component.setPrevColor(CGLCompat.getLib().getColor(player));
		}

		if(type.equals(InnerLightType.HEAL)){
			activateHeal(component, player);
		}else if(type.equals(InnerLightType.DEFENCE)){
			activateDefense(component, player);
		}else if(type.equals(InnerLightType.STRENGTH)){
			activateStrength(component, player);
		}else if(type.equals(InnerLightType.BLAZING)){
			activateBlazing(component, player);
		}else if(type.equals(InnerLightType.FROST)){
			activateFrost(component, player);
		}else if(type.equals(InnerLightType.EARTHEN)){
			activateEarthen(component, player);
		}else if(type.equals(InnerLightType.WIND)){
			activateWind(component, player);
		}else if(type.equals(InnerLightType.AQUA)){
			activateAqua(component, player);
		} else if(type.equals(InnerLightType.FOREST_AURA)){
			activateForestAura(component, player);
		}
		else if(type.equals(InnerLightType.FROG)){
			activateFrog(component, player);
		}
		//for now defaults here
		else{
			activateHeal(component, player);
		}

		if(Config.PLAYER_GLOWS){
			player.setGlowing(true);
		}
		if(!player.getWorld().isClient){
			LightParticlesUtil.spawnDefaultLightParticleSequence((ServerPlayerEntity) player);
			sendRenderRunePacket((ServerPlayerEntity) player);
		}

	}

	/** Gets all the enemies near a player and returns them in a list
	 * Hostile-non allies (aka not summoned or pets) and Enemy players
	 * count as Enemies
	 *
	 * @param player The player in question
	 * @return A list of the player's enemies in the box_expansion_amount
	 * */
	private static List<LivingEntity> getEnemies(PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
		for(LivingEntity ent : entities){
			if(ent instanceof HostileEntity && !CheckUtils.CheckAllies.checkAlly(player, ent)){
				targets.add(ent);
			}
			if(ent instanceof PlayerEntity && CheckUtils.CheckAllies.checkEnemies(player, ent)){
				targets.add(ent);
			}
		}
		return targets;
	}

	/** Gets all the allies near a player and returns them in a list
	 * Allied players and pets and summoned mobs count as allies.
	 * The player itself is counted as well
	 *<p>
	 * They also need to be under a certain HP percentage (configurable in the config)
	 *
	 * @param player The player in question
	 * @return A list of the player's enemies in the box_expansion_amount
	 * */
	public static List<LivingEntity> getAllies(PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
		targets.add(player);
		for(LivingEntity ent : entities){
			if(CheckUtils.CheckAllies.checkAlly(player, ent)){
				if(Config.ALWAYS_AFFECT_ALLIES || CheckUtils.checkSelfDanger(ent, Config.HP_PERCENTAGE_ALLIES)){
					targets.add(ent);
				}
			}else if(ent instanceof TameableEntity){
				if(player.equals(((TameableEntity) ent).getOwner())){
					if(Config.ALWAYS_AFFECT_ALLIES || CheckUtils.checkSelfDanger(ent, Config.HP_PERCENTAGE_VARIANT)){
						targets.add(ent);
					}
				}
			}
		}
		return targets;
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
			targets.addAll(getAllies(player));
			player.sendMessage(Text.translatable("light.description.activation.heal.allies"), true);
		}

		//Finds peaceful creatures and allies, also the player
		else if(component.getTargets().equals(TargetType.VARIANT)){
			targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true)));
			List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
			targets.add(player);
			for(LivingEntity ent : entities){
				// may need this to prevent bugs EDIT i don't even remember what "this" referred to eheh
				if(CheckUtils.CheckAllies.checkAlly(player, ent)){
					if(Config.ALWAYS_AFFECT_ALLIES || CheckUtils.checkSelfDanger(ent, Config.HP_PERCENTAGE_ALLIES)){
						targets.add(ent);
					}
				}else if(ent instanceof TameableEntity){
					if(player.equals(((TameableEntity) ent).getOwner())){
						if(Config.ALWAYS_AFFECT_ALLIES || CheckUtils.checkSelfDanger(ent, Config.HP_PERCENTAGE_VARIANT)){
							targets.add(ent);
						}
					}
				}
			}
			player.sendMessage(Text.translatable("light.description.activation.heal.variant"), true);
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
			targets.addAll(getAllies(player));
			player.sendMessage(Text.translatable("light.description.activation.defense.allies"), true);
		}

		//Same here
		else if(component.getTargets().equals(TargetType.VARIANT)){
			if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
				targets.add(player);
			}
			targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true)));
			player.sendMessage(Text.translatable("light.description.activation.defense.variant"), true);
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
		}else if(component.getTargets().equals(TargetType.VARIANT)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.strength.variant"), true);
		}
		else if(component.getTargets().equals(TargetType.ALLIES)){
			targets.addAll(getAllies(player));
			player.sendMessage(Text.translatable("light.description.activation.strength.allies"), true);
		}

		new StrengthLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Blazing Light=======================
	public static void activateBlazing(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();

		if(component.getTargets().equals(TargetType.ALL)){
			targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true)));
			targets.remove(player);
			player.sendMessage(Text.translatable("light.description.activation.blazing.all"), true);
		}

		else if(component.getTargets().equals(TargetType.ENEMIES) || component.getTargets().equals(TargetType.VARIANT)){
			targets.addAll(getEnemies(player));
			player.sendMessage(Text.translatable("light.description.activation.blazing.enemies"), true);
		}

		new BlazingLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Frost Light=======================
	public static void activateFrost(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		if(component.getTargets().equals(TargetType.ALL)){
			targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true)));
			targets.remove(player);
			player.sendMessage(Text.translatable("light.description.activation.frost.all"), true);
		}

		else if(component.getTargets().equals(TargetType.ENEMIES)){
			targets.addAll(getEnemies(player));
			player.sendMessage(Text.translatable("light.description.activation.frost.enemies"), true);
		}else if(component.getTargets().equals(TargetType.ALLIES)){
			targets.addAll(getAllies(player));
			player.sendMessage(Text.translatable("light.description.activation.frost.allies"), true);
		}if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.frost.self"), true);
		}

		new FrostLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Earthen Light=======================
	public static void activateEarthen(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		if(component.getTargets().equals(TargetType.VARIANT)){
			player.sendMessage(Text.translatable("light.description.activation.earthen.variant"), true);
		}

		else if(component.getTargets().equals(TargetType.ENEMIES)){
			targets.addAll(getEnemies(player));
			player.sendMessage(Text.translatable("light.description.activation.earthen.enemies"), true);
		}else if(component.getTargets().equals(TargetType.ALLIES)){
			targets.addAll(getAllies(player));
			player.sendMessage(Text.translatable("light.description.activation.earthen.allies"), true);
		}if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.earthen.self"), true);
		}

		new EarthenLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Wind Light=======================
	public static void activateWind(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		if(component.getTargets().equals(TargetType.ALLIES)){
			targets.addAll(getAllies(player));
			player.sendMessage(Text.translatable("light.description.activation.wind.allies"), true);
		}else if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.wind.self"), true);
		}else if(component.getTargets().equals(TargetType.ALL)){
			targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true)));
			targets.remove(player);
			player.sendMessage(Text.translatable("light.description.activation.wind.all"), true);
		}

		new WindLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Aqua Light=======================
	public static void activateAqua(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		if(component.getTargets().equals(TargetType.ALL)){
			targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true)));
			targets.remove(player);
			player.sendMessage(Text.translatable("light.description.activation.aqua.all"), true);
		}

		else if(component.getTargets().equals(TargetType.ENEMIES)){
			targets.addAll(getEnemies(player));
			player.sendMessage(Text.translatable("light.description.activation.aqua.enemies"), true);
		}else if(component.getTargets().equals(TargetType.ALLIES)){
			targets.addAll(getAllies(player));
			player.sendMessage(Text.translatable("light.description.activation.aqua.allies"), true);
		}if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.aqua.self"), true);
		}

		new AquaLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Forest Aura Light=======================
	public static void activateForestAura(LightComponent component, PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();

		if(component.getTargets().equals(TargetType.ALL)){
			targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true)));
			targets.remove(player);
			player.sendMessage(Text.translatable("light.description.activation.forest_aura.all"), true);
		}else if(component.getTargets().equals(TargetType.SELF)){
			targets.add(player);
			player.sendMessage(Text.translatable("light.description.activation.forest_aura.self"), true);
		}

		new ForestAuraLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	//=======================Frog Light=======================
	public static void activateFrog(LightComponent component, PlayerEntity player){

		List<LivingEntity> targets = new ArrayList<>(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true)));
		player.sendMessage(Text.translatable("light.description.activation.frog"), true);

		new FrogLight(targets, component.getMaxCooldown(), component.getPowerMultiplier(),
				component.getDuration(), player).execute();
	}

	public static void sendRenderRunePacket(ServerPlayerEntity player){
		try{
			ServerPlayNetworking.send(player, PlayRenderEffectPacketS2C.ID, new PlayRenderEffectPacketS2C(RenderEffect.RUNES));
		}catch(Exception e){
			LOGGER.error("FAILED to send data packets to the client!");
			e.printStackTrace();
		}
	}

	/**Adds the player to the list of players that are currently ready to
	 * trigger a light. Automatically removes them after 10 seconds.
	 * */
	public static void addToReadyList(PlayerEntity player){
		//TODO if changig the ready seconds becomes a thing, modify it here too.
		CURRENTLY_READY_LIGHT_PLAYER_CACHE.put(player.getUuid(), 20*10);
	}

	public void registerReadyLightCacheRemover(){
		ServerTickEvents.END_SERVER_TICK.register(server -> {
            if(CURRENTLY_READY_LIGHT_PLAYER_CACHE.isEmpty()){
                return;
            }
            ConcurrentHashMap<UUID, Integer> copy = new ConcurrentHashMap<>(CURRENTLY_READY_LIGHT_PLAYER_CACHE);

			copy.forEach((key, value) -> {
				if(value == 0){
					CURRENTLY_READY_LIGHT_PLAYER_CACHE.remove(key);
				}else{
					//If already removed should just return null i think, so it's ok.
					CURRENTLY_READY_LIGHT_PLAYER_CACHE.replace(key, value -1);
				}
			});
		});
	}

}
