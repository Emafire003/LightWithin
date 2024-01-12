package me.emafire003.dev.lightwithin.entities.earth_golem;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.entities.goals.EarthGolemRevengeGoal;
import me.emafire003.dev.lightwithin.entities.goals.TrackEarthGolemTargetGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("all")
public class EarthGolemEntity extends IronGolemEntity {

    private UUID summoner_id = LightWithin.SUMMONED_BY_COMPONENT.get(this).getSummonerUUID();

    public EarthGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setSummoner(LivingEntity summoner){
        LightWithin.SUMMONED_BY_COMPONENT.get(this).setSummonerUUID(summoner.getUuid());
        this.summoner_id = summoner.getUuid();
    }


    @Nullable
    public PlayerEntity getSummoner(){
        return this.getServer().getPlayerManager().getPlayer(summoner_id);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9, 32.0f));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal((PathAwareEntity)this, 0.6, false));
        this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new IronGolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackEarthGolemTargetGoal(this));
        this.targetSelector.add(2, new EarthGolemRevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new ActiveTargetGoal<MobEntity>(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
        this.targetSelector.add(4, new UniversalAngerGoal<IronGolemEntity>(this, false));
    }

    public static DefaultAttributeContainer.Builder createEarthGolemAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_SPEED, 5).add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isOf(Items.OBSIDIAN)) {
            return ActionResult.PASS;
        }
        float f = this.getHealth();
        this.heal(10.0f);
        if (this.getHealth() == f) {
            return ActionResult.PASS;
        }
        float g = 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;

        //TODO change the playsounds
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g);
        if (!player.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }
        return ActionResult.success(this.getWorld().isClient);
    }

    //TODO change the playsounds

    @Override
    public boolean damage(DamageSource source, float amount) {
        IronGolemEntity.Crack crack = this.getCrack();
        boolean bl = super.damage(source, amount);
        if (bl && this.getCrack() != crack) {
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.4F);
        }

        return bl;
    }

    //TODO change the playsounds

    //0.44 pitch could work
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

}
