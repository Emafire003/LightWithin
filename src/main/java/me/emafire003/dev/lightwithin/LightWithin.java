package me.emafire003.dev.lightwithin;

import com.google.common.cache.Cache;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.events.LightEvents;
import me.emafire003.dev.lightwithin.util.CacheSystem;
import me.emafire003.dev.lightwithin.util.TargetTypes;
import me.emafire003.dev.lightwithin.events.PlayerJoinEvent;
import me.emafire003.dev.lightwithin.lights.HealLight;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.InnerLightTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.SleepManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LightWithin implements ModInitializer, EntityComponentInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "lightwithin";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
		LOGGER.info("Hello Fabric world!");

		LightEvents.registerListeners();

	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(LIGHT_COMPONENT, LightComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}

}
