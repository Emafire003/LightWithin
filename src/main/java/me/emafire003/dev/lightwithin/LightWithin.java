package me.emafire003.dev.lightwithin;

import me.emafire003.dev.lightwithin.events.PlayerRightClickInteractEvent;
import me.emafire003.dev.lightwithin.items.components.LightItemComponents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import me.emafire003.dev.lightwithin.blocks.LightBlocks;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.DialogueProgressState;
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
import me.emafire003.dev.lightwithin.events.*;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.items.crafting.BrewRecipes;
import me.emafire003.dev.lightwithin.lights.*;
import me.emafire003.dev.lightwithin.networking.*;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.emafire003.dev.lightwithin.lights.ForestAuraLight.FOREST_AURA_BLOCKS;

public class LightWithin implements ModInitializer, EntityComponentInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "lightwithin";
	public static final String PREFIX_MSG = "[LightWithin] ";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	//TODO wiki/changelog AREA OF SEARCH FOR ENTITIES DOUBLED
	//private static int BOX_EXPANSION_AMOUNT = 6; //NB: this is actually configurable in the config file, look below

	public static boolean AP1 = false;

	public static Path PATH = Path.of(FabricLoader.getInstance().getConfigDir() + "/" + MOD_ID + "/");

    public static boolean overrideTeamColorsPrev = false;

	public static List<UUID> USED_CHARGE_PLAYER_CACHE = new ArrayList<>();
	public static ConcurrentHashMap<UUID, Integer> CURRENTLY_READY_LIGHT_PLAYER_CACHE = new ConcurrentHashMap<>();

	public static final RegistryKey<Registry<InnerLight>> INNER_LIGHT_REGISTRY_KEY = RegistryKey.ofRegistry(getIdentifier("light_types"));

	///  The registry for the InnerLights. Every light type is registered in here
	public static final SimpleRegistry<InnerLight> INNERLIGHT_REGISTRY =
			FabricRegistryBuilder.createSimple(INNER_LIGHT_REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	/// It's used to set the limit of the power level with commands
	public static int MAX_POWER_COMMANDS = 10;

	public static Identifier getIdentifier(String path){
		return Identifier.of(MOD_ID, path);
	}



    public static final ComponentKey<LightComponent> LIGHT_COMPONENT =
            ComponentRegistry.getOrCreate(getIdentifier("light_component"), LightComponent.class);

    public static final ComponentKey<SummonedByComponent> SUMMONED_BY_COMPONENT =
            ComponentRegistry.getOrCreate(getIdentifier("summoned_by_component"), SummonedByComponent.class);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-used state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

		InnerLightTypes.registerLights();
        //Must be run before the packt stuff
        registerPayloadIds();

		LightCreationAndEvent.registerCreationListener();
		LightTriggeringAndEvents.registerListeners();
		registerLightUsedPacket();
		registerLightChargeConsumedPacket();
		registerReadyLightCacheRemover();
		registerSyncOptionsOnJoin();
		registerInteractedPacket();
		registerDialogueStateUpdatePacket();
		registerLuxdreamDialogueC2SPacket();
		LightSounds.registerSounds();
		LightEffects.registerModEffects();
		LightItems.registerItems();
		LightParticles.registerParticles();
		LightBlocks.registerBlocks();
		LootTableModifier.modifyLootTables();
		LightCommands.registerArguments();
		LightEntities.registerEntities();
		registerTags();
		registerLuxCognitaOnFirstJoin();
        LightItemComponents.registerComponents();

        if(FabricLoader.getInstance().isModLoaded("flan")){
            FlanCompat.registerFlan();
        }
        CommandRegistrationCallback.EVENT.register(LightCommands::registerCommands);



		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			BrewRecipes.registerRecipes();
			if(getBoxExpansionAmount() <= 0){
				Config.AREA_OF_SEARCH_FOR_ENTITIES = 12;
                LOGGER.warn("Warning! The inputted area of search for entities is less or equal to zero, restoring to the default value of {}", Config.AREA_OF_SEARCH_FOR_ENTITIES);
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

		//done to get the power command value thingy
		Config.reloadConfig();
		setMaxPowerWithCommands(Config.MAX_POWER_WITH_COMMANDS);
		CommandRegistrationCallback.EVENT.register(LightCommands::registerCommands);

		LocalDate currentDate = LocalDate.now();
		int day = currentDate.getDayOfMonth();
		Month month = currentDate.getMonth();
		if(month.equals(Month.APRIL) && day == 1){
			AP1 = true;
		}

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

			//Not really meant to be here, but it remvoes the effect of luxdream if the player crashed or quit and then logged back in
			if(player.hasStatusEffect(LightEffects.LUXCOGNITA_DREAM)){
				player.removeStatusEffect(LightEffects.LUXCOGNITA_DREAM);
			}
			return ActionResult.PASS;
		});
	}


//TODO testout
	private static void registerLuxCognitaOnFirstJoin(){
		PlayerFirstJoinEvent.EVENT.register((player, server) -> {
			if(player.getWorld().isClient){
				return;
			}
			if(Config.LUXCOGNITA_ON_JOIN){
				player.giveItemStack(new ItemStack(LightItems.LUXCOGNITA_BERRY, 1));
			}
		});
	}



	@SuppressWarnings("unused")
    public static void registerTags(){
		RegistryEntryList.Named<Block> BLAZING_TRIGGER_BLOCKS = Registries.BLOCK.getOrCreateEntryList(BlazingLight.BLAZING_TRIGGER_BLOCKS);
		RegistryEntryList.Named<Item> BLAZING_TRIGGER_ITEMS = Registries.ITEM.getOrCreateEntryList(BlazingLight.BLAZING_TRIGGER_ITEMS);

		RegistryEntryList.Named<Block> FROST_TRIGGER_BLOCKS = Registries.BLOCK.getOrCreateEntryList(FrostLight.FROST_TRIGGER_BLOCKS);
		RegistryEntryList.Named<Item> FROST_TRIGGER_ITEMS = Registries.ITEM.getOrCreateEntryList(FrostLight.FROST_TRIGGER_ITEMS);

		RegistryEntryList.Named<Block> WIND_TRIGGER_BLOCKS = Registries.BLOCK.getOrCreateEntryList(WindLight.WIND_TRIGGER_BLOCKS);
		RegistryEntryList.Named<Item> WIND_TRIGGER_ITEMS = Registries.ITEM.getOrCreateEntryList(WindLight.WIND_TRIGGER_ITEMS);

		RegistryEntryList.Named<Block> AQUA_TRIGGER_BLOCKS = Registries.BLOCK.getOrCreateEntryList(AquaLight.AQUA_TRIGGER_BLOCKS);
		RegistryEntryList.Named<Item> AQUA_TRIGGER_ITEMS = Registries.ITEM.getOrCreateEntryList(AquaLight.AQUA_TRIGGER_ITEMS);

		RegistryEntryList.Named<Block> FOREST_BLOCKS = Registries.BLOCK.getOrCreateEntryList(FOREST_AURA_BLOCKS);

		RegistryEntryList.Named<Item> THUNDER_AURA_TRIGGER_ITEMS = Registries.ITEM.getOrCreateEntryList(ThunderAuraLight.THUNDER_AURA_TRIGGER_ITEMS);

	}

	/**Returns the current BoxExpansionAmount/Area of search for entities, which is used to search
	 * for entities in that radius*/
	public static int getBoxExpansionAmount(){
		return Config.AREA_OF_SEARCH_FOR_ENTITIES;
	}

	/** Sets the new value for the maximum power settable using commands
	 * If below 1, it will be set to 1*/
	public static void setMaxPowerWithCommands(int max){
		MAX_POWER_COMMANDS = Math.max(max, 1);
	}

	/** Returns the maximum power settable using commands */
	public static int getMaxPowerCommands(){
		return Math.max(MAX_POWER_COMMANDS, 1);
	}


	/**Sends a packet with updated config options to the client
     * such as the auto light activation permission*/
    public static void syncCustomConfigOptions(ServerPlayerEntity player){
        Map<String, Boolean> booleanMap = new HashMap<>();
        booleanMap.put(ConfigPacketConstants.AUTO_LIGHT_ACTIVATION, Config.AUTO_LIGHT_ACTIVATION);
        //TODO if needed i'll add other settings
        ConfigOptionSyncPayloadS2C payload = new ConfigOptionSyncPayloadS2C(booleanMap);
        ServerPlayNetworking.send(player, payload);
    }

    private static void registerPayloadIds(){
        LOGGER.debug("Registering packets and paylaod ids in the common module...");

        //Client to Server (serverbound packets)
        PayloadTypeRegistry.playC2S().register(LightUsedPayloadC2S.ID, LightUsedPayloadC2S.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(LightChargeConsumedPayloadC2S.ID, LightChargeConsumedPayloadC2S.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(InteractedPayloadC2S.ID, InteractedPayloadC2S.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(DialogueProgressUpdatePayloadC2S.ID, DialogueProgressUpdatePayloadC2S.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(LuxdreamClientPayloadC2S.ID, LuxdreamClientPayloadC2S.PACKET_CODEC);

		//Server to Client (clientbound packets)
        PayloadTypeRegistry.playS2C().register(LightReadyPayloadS2C.ID, LightReadyPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigOptionSyncPayloadS2C.ID, ConfigOptionSyncPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(PlayRenderEffectPayloadS2C.ID, PlayRenderEffectPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(WindLightVelocityPayloadS2C.ID, WindLightVelocityPayloadS2C.PACKET_CODEC);
		PayloadTypeRegistry.playS2C().register(GlowEntitiesPayloadS2C.ID, GlowEntitiesPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(LuxdreamServerPayloadS2C.ID, LuxdreamServerPayloadS2C.PACKET_CODEC);

    }

    private static void registerLightUsedPacket(){
        ServerPlayNetworking.registerGlobalReceiver(LightUsedPayloadC2S.ID, ((payload, context) -> {
            ServerPlayerEntity player = context.player();
            if(player.getWorld().isClient){
                return;
            }
            addToReadyList(player);
            if(player.getServer() == null){
                LOGGER.error("Error while reciving LightChargeConsumedPacket, server is null!");
                return;
            }
            player.getServer().execute( () -> {
                try{
                    //Handles the LightCharge being used. If it used, results will be true.
                    if(payload.used()){

                        if(!CheckUtils.canActivateHere(player)){
                            player.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.translatableWithFallback("light.charge.cant_use_here", "You are not allowed to use you InnerLight here!").formatted(Formatting.RED)));
                            return;
                        }

                        //This could be laggy? Maybe?
                        List<ServerPlayerEntity> players = player.getServerWorld().getPlayers();
                        for(ServerPlayerEntity p : players){
                            ServerPlayNetworking.send(p, new PlayRenderEffectPayloadS2C(RenderEffect.FORCED_LIGHT_RAYS, player.getId()));
                        }

                        //TODO maybe also increase the max cooldown light-stat?
                        //Currently just increases the cooldown. But the actual charges are fairly hard to get.
                        USED_CHARGE_PLAYER_CACHE.add(player.getUuid());

                        player.getWorld().playSound(player.getX(), player.getY(), player.getZ(), LightSounds.LIGHT_CHARGED, SoundCategory.PLAYERS, 1, 0.7f, true);

                        if(Config.TARGET_FEEDBACK && Config.USED_CHARGE_COOLDOWN_MULTIPLIER > 1){
                            player.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.translatable("light.charge.cooldown_message").formatted(Formatting.YELLOW)));
                        }
                    }else{
                        List<ServerPlayerEntity> players = player.getServerWorld().getPlayers();
                        for(ServerPlayerEntity p : players){
							ServerPlayNetworking.send(p, new PlayRenderEffectPayloadS2C(RenderEffect.LIGHT_RAYS, player.getId()));

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

            //var results = LightUsedPacketC2S.read(buf);
        }));
    }
    /**Fires when the player sees a dialogue which ahs to update a certain dialogue state*/
    private static void registerDialogueStateUpdatePacket(){
        ServerPlayNetworking.registerGlobalReceiver(DialogueProgressUpdatePayloadC2S.ID, ((payload, context) -> {
            ServerPlayerEntity player = context.player();
            if(player.getWorld().isClient){
                return;
            }
            DialogueProgressState state = payload.state();
            boolean shouldRemove = payload.shouldRemove();

            player.getServer().execute( () -> {
                try{
					if(state != null && state.equals(DialogueProgressState.PISSED_OFF)){
						player.addStatusEffect(new StatusEffectInstance(LightEffects.LUXCOGNITA_OFFENDED, 60*20));
						return;
					}

					if(shouldRemove){
						LIGHT_COMPONENT.get(player).removeDialogueProgressState(state);
					}else{
						LIGHT_COMPONENT.get(player).addDialogueProgressState(state);
					}
                }catch (NoSuchElementException e){
                    LOGGER.warn("No value in the packet!");
                }catch (Exception e){
                    LOGGER.error("There was an error while getting the packet!");
                    e.printStackTrace();
                }
            });
        }));
    }

	/**Triggered when a player has finished their luxdialogue / dream*/
	private static void registerLuxdreamDialogueC2SPacket(){
		ServerPlayNetworking.registerGlobalReceiver(LuxdreamClientPayloadC2S.ID, ((payload, context) -> {
			ServerPlayerEntity player = context.player();
			if(player.getWorld().isClient){
				return;
			}
			LuxDialogueActions action = payload.action();
			player.getServer().execute( () -> {
				try{
					//StopDream packet thing
					if(action.equals(LuxDialogueActions.STOP_DREAM)){
						if(!player.hasStatusEffect(LightEffects.LUXCOGNITA_DREAM)){
							LOGGER.warn("Luxdream termination packet sent but there was no Luxcognita Dream effect to be removed!");
							return;
						}
						player.removeStatusEffect(LightEffects.LUXCOGNITA_DREAM);
					}
				}catch (NoSuchElementException e){
					LOGGER.warn("No value in the packet!");
				}catch (Exception e){
					LOGGER.error("There was an error while getting the packet!");
					e.printStackTrace();
				}
			});
		}));
	}


    private static void registerLightChargeConsumedPacket(){
        ServerPlayNetworking.registerGlobalReceiver(LightChargeConsumedPayloadC2S.ID, ((payload, context) -> {
            ServerPlayerEntity player = context.player();
            if(player.getWorld().isClient){
                return;
            }
            addToReadyList(player);
            if(player.getServer() == null){
                LOGGER.error("Error while reciving LightChargeConsumedPacket, server is null!");
                return;
            }
            player.getServer().execute( () -> {
                try{

                    ///particle lightwithin:shine_particle ~ ~1 ~ 0.1 0.1 0.1 0.15 25 force
                    if(!CheckUtils.canActivateHere(player)){
                        return;
                    }
                    player.sendMessage(Text.translatable("light.charge.used").formatted(Formatting.YELLOW), true);

                    ((ServerWorld) player.getWorld()).spawnParticles(
                            LightParticles.SHINE_PARTICLE, player.getX(), player.getY()+player.getDimensions(player.getPose()).height()/2, player.getZ(),
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

            //var results = LightUsedPacketC2S.read(buf);
        }));
    }

	private static void registerInteractedPacket(){
		ServerPlayNetworking.registerGlobalReceiver(InteractedPayloadC2S.ID, ((payload, context) -> {
			ServerPlayerEntity player = context.player();
			if(player.getWorld().isClient){
				return;
			}
			player.getServer().execute( () -> {
				try{
					PlayerRightClickInteractEvent.EVENT.invoker().interact(player);
				}catch (NoSuchElementException e){
					LOGGER.warn("No value in the packet!");
				}catch (Exception e){
					LOGGER.error("There was an error while getting the packet!");
					e.printStackTrace();
				}
			});
		}));
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

		component.getType().startActivation(component, player);

        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            component.setPrevColor(CGLCompat.getLib().getColor(player));
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
	public static List<LivingEntity> getEnemies(PlayerEntity player){
		List<LivingEntity> targets = new ArrayList<>();
		List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(getBoxExpansionAmount()), (entity1 -> true));
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
		List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(getBoxExpansionAmount()), (entity1 -> true));
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

	public static void sendRenderRunePacket(ServerPlayerEntity player){
		try{
			ServerPlayNetworking.send(player, new PlayRenderEffectPayloadS2C(RenderEffect.RUNES, player.getId()));
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
