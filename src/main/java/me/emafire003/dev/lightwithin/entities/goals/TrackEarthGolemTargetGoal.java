package me.emafire003.dev.lightwithin.entities.goals;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntity;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;
import static me.emafire003.dev.lightwithin.LightWithin.box_expansion_amount;

public class TrackEarthGolemTargetGoal extends TrackTargetGoal {
    private final EarthGolemEntity golem;
    @Nullable
    private LivingEntity target;
    private final TargetPredicate targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);

    public TrackEarthGolemTargetGoal (EarthGolemEntity golem) {
        super(golem, false, true);
        this.golem = golem;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if(golem.getSummoner() != null){
            PlayerEntity summoner = golem.getSummoner();
            if(summoner.getRecentDamageSource() != null){
                Entity attacker = golem.getSummoner().getRecentDamageSource().getAttacker();
                if(attacker instanceof LivingEntity && !attacker.equals(golem) && !attacker.equals(summoner)){
                    this.target = (LivingEntity) golem.getSummoner().getRecentDamageSource().getAttacker();
                }
                return true;
            }else{
                List<LivingEntity> entities = golem.getSummoner().getWorld().getEntitiesByClass(LivingEntity.class, new Box(summoner.getBlockPos()).expand(box_expansion_amount),
                        (entity -> {
                            if(CheckUtils.CheckAllies.checkEnemies(summoner, entity)){
                                if(entity instanceof PlayerEntity && (entity.isSpectator() || ((PlayerEntity) entity).isCreative())){
                                    return false;
                                }
                                return true;
                                //Sould accoutn for pets i think
                            }else if(entity instanceof HostileEntity && !CheckUtils.CheckAllies.checkAlly(summoner, entity)){
                                return false;
                            }
                            return false;
                        }));
                if(!entities.isEmpty()){
                    this.target = entities.get(golem.getRandom().nextBetween(0, entities.size()));
                    return true;
                }
            }

        }
        if (this.target == null) {
            return false;
        }
        return !(this.target instanceof PlayerEntity) || !this.target.isSpectator() && !((PlayerEntity)this.target).isCreative();
    }

    @Override
    public void start() {
        this.golem.setTarget(this.target);
        super.start();
    }
}
