package me.emafire003.dev.lightwithin;

import me.emafire003.dev.lightwithin.lights.HealLight;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LightWithin implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "lightwithin";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static int tickCounter = 0;
	//How many seconds should pass between updating the data and sending packets to the client?
	private static int seconds = 10;

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


		//From nbt gets type, then gets the variables need for the type. Aka
		//if type == Heal
		//get cool down, get thing ecc
		//Also, set nbt boolean "LightReady" that will also show up in thw HUD
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->{
			List<LivingEntity> targets = new ArrayList<>();
			targets.add(player);
			InnerLight light = new HealLight(targets, 1, 2, 1, player);
			light.execute();
			LOGGER.info("Attacked entity. Did it heal? 3point in theory");
			//can't retunr null
			return ActionResult.PASS;
		} );
	}
}
