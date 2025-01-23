package me.emafire003.dev.lightwithin.status_effects;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.networking.GlowEntitiesPacketS2C;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.ForestAuraRelation;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static me.emafire003.dev.lightwithin.LightWithin.BOX_EXPANSION_AMOUNT;


/**
 * Players will be able to pass through natural blocks, the one in the tag list,
 * will not suffocate in these. For more info see {@link me.emafire003.dev.lightwithin.mixin.forest_aura_related.ForestBlocksMixin}
 * and will also see the entities that are nearby them when they get the effect for the first time*/
public class ForestAuraEffect extends StatusEffect {

    public ForestAuraEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x1BC131);
    }

    List<LivingEntity> visibleEntities = new ArrayList<>();

    /**How much time between updating the list of entities which the player will be able to see*/
    private static final int ticksBetweenUpdates = 20*3; //Aka it now updates the entities once every 3 secons
    private int tickCounter = 0;

    //for some reason this does not work, so work-arounds!
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
    }

    AtomicInteger visibleEntityCounter = new AtomicInteger(0);

    /**Updates the entites that are glowing if the player still hasn't filled up all of the "slots",
     * aka the amount of entities they can see which amplifier+3
     * This is a workaround since applyUpdatedEffect is not working
     *
     * @param entity The target entity with the effect
     * @param amplifier The amplifier of the effect
     */
    public void updateVisibleEntities(LivingEntity entity, int amplifier){
        if(visibleEntityCounter.get() == amplifier+3){
            //When the counter is already past max entities return early
            return;
        }
        List<LivingEntity> newVisibleEntities = entity.getWorld().getEntitiesByClass(LivingEntity.class, new Box(entity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT * 1.5), (entity1 -> {
            if (visibleEntityCounter.get() < amplifier + 3) {
                if(!entity.equals(entity1) && !visibleEntities.contains(entity1)){
                    visibleEntityCounter.getAndIncrement();
                    return true;
                }
            }
            return false;
        }));
        visibleEntities.addAll(newVisibleEntities);

        //0 means neutral, 1 means enemy, 2 ally
        List<Pair<UUID, ForestAuraRelation>> uuids_related = new ArrayList<>();

        newVisibleEntities.forEach(entity1 -> {

            if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
                if(amplifier >= 6){
                    boolean colored = false;
                    if(CheckUtils.CheckAllies.checkEnemies(entity, entity1) || entity1 instanceof HostileEntity){
                        uuids_related.add(new Pair<>(entity1.getUuid(), ForestAuraRelation.ENEMY));
                        //CLIENTSIDE: CGLCompat.getLib().setExclusiveColorFor(entity1, ForestAuraLight.ENEMY_COLOR, (PlayerEntity) entity);
                        colored = true;
                    }
                    if(amplifier >= 8){
                        if(CheckUtils.CheckAllies.checkAlly(entity, entity1)){
                            if(!colored){
                                uuids_related.add(new Pair<>(entity1.getUuid(), ForestAuraRelation.ALLY));
                            }else{
                                uuids_related.remove(new Pair<>(entity1.getUuid(), ForestAuraRelation.ALLY));
                                uuids_related.add(new Pair<>(entity1.getUuid(), ForestAuraRelation.ALLY));
                            }
                            colored = true;
                        }
                    }
                    if(!colored){
                        uuids_related.add(new Pair<>(entity1.getUuid(), ForestAuraRelation.NEUTRAL));
                        // CGLCompat.getLib().setExclusiveColorFor(entity1, ForestAuraLight.COLOR, (PlayerEntity) entity);
                    }
                }
                else{
                    uuids_related.add(new Pair<>(entity1.getUuid(), ForestAuraRelation.NEUTRAL));
                    //CGLCompat.getLib().setExclusiveColorFor(entity1, ForestAuraLight.COLOR, (PlayerEntity) entity);
                }

            }else{
                uuids_related.add(new Pair<>(entity1.getUuid(), ForestAuraRelation.NEUTRAL));
                //uuids_related.add(new Pair<>(entity1.getUuid(), 0));
            }
        });

        if(!entity.getWorld().isClient()){
            GlowEntitiesPacketS2C glowingPacket = new GlowEntitiesPacketS2C(uuids_related, false);
            ServerPlayNetworking.send((ServerPlayerEntity) entity, GlowEntitiesPacketS2C.ID, glowingPacket);
        }
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);

        if(!(entity instanceof PlayerEntity)){
            return;
        }

        //TODO write in wiki:
        // You will be able to see a number of entites equal to 3+the power level.
        // At level 7 you will see if they are enemies and
        // at level 9 you will see if they are allies, enemies or neutral.
        // if cgl is installed
        //allows the counter to work again
        tickCounter = 0;
        //Makes the initial entities in the area glow
        updateVisibleEntities(entity, amplifier);

        //This is a required work-around since applyUpdateEffect doesn't work for some weird reason
        if(!entity.getWorld().isClient()){
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                if(tickCounter == -1){
                    return;
                }
                if(tickCounter >= ticksBetweenUpdates && entity.hasStatusEffect(LightEffects.FOREST_AURA)){
                    updateVisibleEntities(entity, amplifier);
                    tickCounter = 0;
                }
                tickCounter++;
            });
        }

    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        super.onRemoved(entity, attributes, amplifier);
        if(!(entity instanceof PlayerEntity)){
            return;
        }
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            visibleEntities.forEach(entity1 -> CGLCompat.getLib().clearExclusiveColorFor(entity1, (PlayerEntity) entity, true));
        }
        if(!entity.getWorld().isClient()){
            GlowEntitiesPacketS2C glowingPacket = new GlowEntitiesPacketS2C(null, true);
            ServerPlayNetworking.send((ServerPlayerEntity) entity, GlowEntitiesPacketS2C.ID, glowingPacket);
        }
        //clears the stuff
        visibleEntities.clear();
        visibleEntityCounter.set(0);
        //Stops the counter until it's necessary again
        tickCounter = -1;
    }

}