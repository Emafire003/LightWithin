package me.emafire003.dev.lightwithin.entities.goals;



import java.util.EnumSet;
import java.util.List;

import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntity;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

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
            if(golem.getSummoner().getDamageTracker().getMostRecentDamage() == null){
                return false;
            }
            if(golem.getSummoner().getDamageTracker().getMostRecentDamage().getAttacker() instanceof LivingEntity){
                this.target = (LivingEntity) golem.getSummoner().getDamageTracker().getMostRecentDamage().getAttacker();
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
