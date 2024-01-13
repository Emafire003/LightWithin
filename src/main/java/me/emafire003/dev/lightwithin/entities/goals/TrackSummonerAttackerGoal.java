package me.emafire003.dev.lightwithin.entities.goals;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.SummonedByComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;

public class TrackSummonerAttackerGoal extends TrackTargetGoal {
    private final MobEntity summonable;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public TrackSummonerAttackerGoal(MobEntity summonable) {
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
                this.attacker = livingEntity.getAttacker();
                int i = livingEntity.getLastAttackedTime();
                return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        SummonedByComponent component = LightWithin.SUMMONED_BY_COMPONENT.get(summonable);
        if (component.getIsSummoned()) {
            LivingEntity livingEntity = summonable.getServer().getPlayerManager().getPlayer(component.getSummonerUUID());
            this.lastAttackedTime = livingEntity.getLastAttackedTime();
        }

        super.start();
    }
}
