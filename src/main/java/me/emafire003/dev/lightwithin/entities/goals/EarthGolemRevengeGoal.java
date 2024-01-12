package me.emafire003.dev.lightwithin.entities.goals;

import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;

public class EarthGolemRevengeGoal extends RevengeGoal {

    private EarthGolemEntity golem;

    public EarthGolemRevengeGoal(EarthGolemEntity mob, Class<?>... noRevengeTypes) {
        super(mob, noRevengeTypes);
        this.golem = mob;
    }

    @Override
    public boolean canStart() {
        int i = this.mob.getLastAttackedTime();
        LivingEntity livingEntity = this.mob.getAttacker();
        if(livingEntity != null && livingEntity.equals(golem.getSummoner())){
            return false;
        }else{
            return super.canStart();
        }
    }
}

