package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.items.components.LightItemComponents;
import me.emafire003.dev.lightwithin.items.crafting.BrewRecipes;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BottledLightItem extends Item {

    public BottledLightItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(LightWithin.isPlayerInCooldown(user)){
            user.sendMessage(Text.translatable("item.lightwithin.bottled_light.in_cooldown").formatted(Formatting.RED), true);
            user.playSound(LightSounds.LIGHT_ERROR, 0.5f, 1.2f);
            return TypedActionResult.pass(stack);
        }
        //"Fails" silently if the player has the light used but not active
        if(LightWithin.CURRENTLY_READY_LIGHT_PLAYER_CACHE.containsKey(user.getUuid())){
            return TypedActionResult.pass(stack);
        }
        LightComponent component = LightWithin.LIGHT_COMPONENT.get(user);
        //Checks if the player has triggered the light naturally before, if not the bottle won't activate.
        //TO.DO make configurable, and optional. Kinda. 19.04.2024 Nah, it's cool to keep it this way to preserve the feeling of the mod
        if(!component.hasTriggeredNaturally()){
            if(!world.isClient()){
                user.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.translatable("light.needs_natural_trigger")).formatted(Formatting.YELLOW));
            }
            return TypedActionResult.pass(stack);
        }

        //Checks to see if the bottle is artificially brewed. If it is, the UUID of the player will be 0000000 etc
        if(BottledLightItem.getCreatedBy(stack).equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))){
            //NbtCompound nbt = stack.getNbt();

            if(stack.getComponents().contains(LightItemComponents.BOTTLED_LIGHT_TYPE_INGREDIENT)){
                InnerLight bottled_type = LightWithin.INNERLIGHT_REGISTRY.get(Identifier.tryParse(Objects.requireNonNull(stack.getComponents().get(LightItemComponents.BOTTLED_LIGHT_TYPE_INGREDIENT))));
                if(component.getType().equals(bottled_type)){
                    if(stack.getComponents().contains(LightItemComponents.BOTTLED_LIGHT_TARGET_INGREDIENT)){
                        TargetType bottled_target = TargetType.valueOf(stack.getComponents().get(LightItemComponents.BOTTLED_LIGHT_TARGET_INGREDIENT));
                        if(component.getTargets().equals(bottled_target)){
                            if(addCharge(user, stack, component)){
                                return TypedActionResult.consume(stack);
                            }else{
                                return TypedActionResult.pass(stack);
                            }
                        }else{
                            user.getWorld().addBlockBreakParticles(user.getBlockPos().up(), Blocks.GLASS.getDefaultState());
                            user.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.43f, 1.3f);
                            user.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.6f, 1.7f);
                            stack.decrement(1);
                            return TypedActionResult.consume(stack);
                        }
                    }else{
                        user.getWorld().addBlockBreakParticles(user.getBlockPos().up(), Blocks.GLASS.getDefaultState());
                        user.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.43f, 1.3f);
                        user.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.6f, 1.7f);
                        stack.decrement(1);
                        return TypedActionResult.consume(stack);
                    }
                }else{
                    if(!user.getWorld().isClient()){
                        ((ServerWorld)user.getWorld()).spawnParticles(ParticleTypes.FLASH, user.getX(), user.getY(), user.getZ(), 1,0,0, 0, 0.1);
                        user.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.translatable("item.lightwithin.bottled_light.exploded").formatted(Formatting.YELLOW)));
                    }
                    stack.decrement(stack.getCount());
                    ItemEntity item = new ItemEntity(world, user.getX(), user.getY()+1, user.getZ(), stack);
                    world.createExplosion(item, user.getX(), user.getY()+1, user.getZ(), 1.5f, false, World.ExplosionSourceType.MOB);
                    return TypedActionResult.consume(stack);
                }
                //Checks if the item is created in older versions, so it may have NBT data instead of custom data
            }else if(stack.contains(DataComponentTypes.CUSTOM_DATA)) {
                NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
                if(nbtComponent == null){
                    return TypedActionResult.pass(stack);
                }
                if(nbtComponent.contains(BrewRecipes.TYPE_INGREDIENT_KEY)){
                    InnerLight bottled_type = LightWithin.INNERLIGHT_REGISTRY.get(Identifier.tryParse(nbtComponent.copyNbt().getString(BrewRecipes.TYPE_INGREDIENT_KEY)));
                    if(component.getType().equals(bottled_type)){
                        if(nbtComponent.contains(BrewRecipes.TARGET_INGREDIENT_KEY)){
                            TargetType bottled_target = TargetType.valueOf(nbtComponent.copyNbt().getString(BrewRecipes.TARGET_INGREDIENT_KEY));
                            if(component.getTargets().equals(bottled_target)){
                                if(addCharge(user, stack, component)){
                                    return TypedActionResult.consume(stack);
                                }else{
                                    return TypedActionResult.pass(stack);
                                }
                            }else{
                                user.getWorld().addBlockBreakParticles(user.getBlockPos().up(), Blocks.GLASS.getDefaultState());
                                user.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.43f, 1.3f);
                                user.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.6f, 1.7f);
                                stack.decrement(1);
                                return TypedActionResult.consume(stack);
                            }
                        }else{
                            user.getWorld().addBlockBreakParticles(user.getBlockPos().up(), Blocks.GLASS.getDefaultState());
                            user.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.43f, 1.3f);
                            user.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.6f, 1.7f);
                            stack.decrement(1);
                            return TypedActionResult.consume(stack);
                        }
                    }else{
                        if(!user.getWorld().isClient()){
                            ((ServerWorld)user.getWorld()).spawnParticles(ParticleTypes.FLASH, user.getX(), user.getY(), user.getZ(), 1,0,0, 0, 0.1);
                            user.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.translatable("item.lightwithin.bottled_light.exploded").formatted(Formatting.YELLOW)));
                        }
                        stack.decrement(stack.getCount());
                        ItemEntity item = new ItemEntity(world, user.getX(), user.getY()+1, user.getZ(), stack);
                        world.createExplosion(item, user.getX(), user.getY()+1, user.getZ(), 1.5f, false, World.ExplosionSourceType.MOB);
                        return TypedActionResult.consume(stack);
                    }
                }

            }
            return TypedActionResult.pass(stack);
        }

        //Checks to see if the player and the bottle have the same light. Aka the player that created the bottle is the one using it.
        if(!BottledLightItem.getCreatedBy(stack).equals(user.getUuid())){
            if(!world.isClient()){
                user.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.translatable("light.not_your_light").formatted(Formatting.YELLOW)));
            }
            return TypedActionResult.pass(stack);
        }
        if(addCharge(user, stack, component)){
            return TypedActionResult.consume(stack);
        }else{
            return TypedActionResult.pass(stack);
        }
    }

    public boolean addCharge(PlayerEntity user, ItemStack stack, LightComponent component){

        int charges = component.getCurrentLightCharges()+1;
        if(charges > component.getMaxLightCharges()){
            //Or another error-sound. Nah i think this fits
            if(!user.getWorld().isClient()){
                user.sendMessage(Text.translatable("light.max_charges").formatted(Formatting.RED), true);
            }
            user.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.5f, 1.76f);
            return false;
        }

        component.setLightCharges(charges);

        if(!user.getWorld().isClient()){
            LightParticlesUtil.spawnChargedParticles((ServerPlayerEntity) user);
        }

        user.getWorld().addBlockBreakParticles(user.getBlockPos().up(), Blocks.GLASS.getDefaultState());
        user.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.3f, 1.3f);
        //user.playSound(LightSounds.LIGHT_READY, 1f, 1.5f);
        user.playSound(LightSounds.LIGHT_CHARGED, 1f, 1f);
        user.addStatusEffect(new StatusEffectInstance(LightEffects.LIGHT_FATIGUE, (int) (Config.COOLDOWN_MULTIPLIER*20*LightWithin.LIGHT_COMPONENT.get(user).getMaxCooldown())));
        stack.decrement(1);
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.lightwithin.bottled_light.tooltip"));
        if(Screen.hasShiftDown()) {
            if(stack.contains(DataComponentTypes.CUSTOM_DATA) && stack.get(DataComponentTypes.CUSTOM_DATA) != null){
                NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
                if(nbtComponent.contains(BrewRecipes.TYPE_INGREDIENT_KEY) && nbtComponent.copyNbt().getUuid(BrewRecipes.PLAYER_NBT_KEY).equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))){
                    tooltip.add(Text.translatable("item.lightwithin.bottled_light.tooltip.warning").formatted(Formatting.AQUA).formatted(Formatting.ITALIC));
                    if(nbtComponent.contains(BrewRecipes.TYPE_INGREDIENT_KEY)){
                        tooltip.add(Text.translatable("item.lightwithin.bottled_light.tooltip.type").formatted(Formatting.GREEN).append(Text.literal(Objects.requireNonNull(nbtComponent.copyNbt().getString(BrewRecipes.TYPE_INGREDIENT_KEY))).formatted(Formatting.LIGHT_PURPLE)));
                    }
                    if(nbtComponent.contains(BrewRecipes.TARGET_INGREDIENT_KEY)){
                        tooltip.add(Text.translatable("item.lightwithin.bottled_light.tooltip.target").formatted(Formatting.GREEN).append(Text.literal(Objects.requireNonNull(nbtComponent.copyNbt().getString(BrewRecipes.TARGET_INGREDIENT_KEY))).formatted(Formatting.LIGHT_PURPLE)));
                    }
                }
            }
            else if(stack.getComponents().contains(LightItemComponents.BOTTLED_LIGHT_PLAYER_UUID) && stack.getComponents().get(LightItemComponents.BOTTLED_LIGHT_PLAYER_UUID).equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))){
                tooltip.add(Text.translatable("item.lightwithin.bottled_light.tooltip.warning").formatted(Formatting.AQUA).formatted(Formatting.ITALIC));
                if(stack.contains(LightItemComponents.BOTTLED_LIGHT_TYPE_INGREDIENT)){
                    tooltip.add(Text.translatable("item.lightwithin.bottled_light.tooltip.type").formatted(Formatting.GREEN).append(Text.literal(Objects.requireNonNull(stack.get(LightItemComponents.BOTTLED_LIGHT_TYPE_INGREDIENT))).formatted(Formatting.LIGHT_PURPLE)));
                }
                if(stack.contains(LightItemComponents.BOTTLED_LIGHT_TARGET_INGREDIENT)){
                    tooltip.add(Text.translatable("item.lightwithin.bottled_light.tooltip.target").formatted(Formatting.GREEN).append(Text.literal(Objects.requireNonNull(stack.get(LightItemComponents.BOTTLED_LIGHT_TARGET_INGREDIENT))).formatted(Formatting.LIGHT_PURPLE)));
                }
            }
            else{
                tooltip.add(Text.literal("§bPlayer UUID: §a"+getCreatedBy(stack).toString()));
            }

        }
    }

    public static void setCreatedBy(PlayerEntity player, ItemStack bottle_item){
        bottle_item.set(LightItemComponents.BOTTLED_LIGHT_PLAYER_UUID, player.getUuid());
    }

    public static UUID getCreatedBy(ItemStack bottle_item){
        if(!bottle_item.getComponents().contains(LightItemComponents.BOTTLED_LIGHT_PLAYER_UUID)){
            //Support for backwards compatibility
            if(bottle_item.contains(DataComponentTypes.CUSTOM_DATA)){
                NbtComponent nbtComponent = bottle_item.get(DataComponentTypes.CUSTOM_DATA);
                if(nbtComponent != null && nbtComponent.contains(BrewRecipes.PLAYER_NBT_KEY)){
                    return nbtComponent.copyNbt().getUuid(BrewRecipes.PLAYER_NBT_KEY);
                }
            }

            //If there is no nbt component
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
        return bottle_item.get(LightItemComponents.BOTTLED_LIGHT_PLAYER_UUID);
    }


}
