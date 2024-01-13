package me.emafire003.dev.lightwithin.entities.goals;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.SummonedByComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;

public class AttackWithSummonerGoal extends TrackTargetGoal {
    private final MobEntity summonable;
    private LivingEntity attacking;
    private int lastAttackTime;

    public AttackWithSummonerGoal(MobEntity summonable) {
        super(summonable, false);
        this.summonable = summonable;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public boolean canStart() {
        SummonedByComponent component = LightWithin.SUMMONED_BY_COMPONENT.get(summonable);
        if (component.getIsSummoned()) {
            LivingEntity livingEntity = summonable.getServer().getPlayerManager().getPlayer(component.getSummonerUUID());
            if (livingEntity == null) {
                return false;
            } else {
                this.attacking = livingEntity.getAttacking();
                int i = livingEntity.getLastAttackTime();
                return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.attacking);
        SummonedByComponent component = LightWithin.SUMMONED_BY_COMPONENT.get(summonable);
        if (component.getIsSummoned()) {
            LivingEntity livingEntity = summonable.getServer().getPlayerManager().getPlayer(component.getSummonerUUID());
            if (livingEntity != null) {
                this.lastAttackTime = livingEntity.getLastAttackTime();
            }

            super.start();
        }
    }
}
