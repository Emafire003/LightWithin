package me.emafire003.dev.lightwithin.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class SummonedByComponent implements ComponentV3, AutoSyncedComponent {


    public static UUID NO_SUMMONER_UUID = UUID.fromString("00000000-0000-4000-8000-000000000000");
    protected UUID summoner_UUID = NO_SUMMONER_UUID;
    protected boolean isSummoned = false;
    private final LivingEntity self;

    public SummonedByComponent(LivingEntity livingEntity) {
        this.self = livingEntity;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if(tag.contains("summoner")){
            this.summoner_UUID = tag.getUuid("summoner");
        }else{
            LOGGER.error("Summoner not found in the NBT compound 'summoned_by_component' ");
            this.summoner_UUID = NO_SUMMONER_UUID;
        }

        if(tag.contains("isSummoned")){
            this.isSummoned = tag.getBoolean("isSummoned");
        }else{
            this.isSummoned = false;
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putUuid("summoner", this.summoner_UUID);
        tag.putBoolean("isSummoned", this.isSummoned);
    }

    public UUID getSummonerUUID() {
        return this.summoner_UUID;
    }

    public void setSummonerUUID(UUID uuid) {
        this.summoner_UUID = uuid;
        LightWithin.SUMMONED_BY_COMPONENT.sync(self);
    }

    public boolean getIsSummoned() {
        return this.isSummoned;
    }

    public void setIsSummoned(boolean b) {
        this.isSummoned = b;
        LightWithin.SUMMONED_BY_COMPONENT.sync(self);
    }

    public void clear(){
        this.summoner_UUID = NO_SUMMONER_UUID;
        this.isSummoned = false;
        LightWithin.SUMMONED_BY_COMPONENT.sync(self);
    }

}
