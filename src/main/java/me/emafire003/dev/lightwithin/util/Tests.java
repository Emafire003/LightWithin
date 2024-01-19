package me.emafire003.dev.lightwithin.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

import static me.emafire003.dev.lightwithin.events.LightCreationAndEvent.TARGET_BIT;
import static me.emafire003.dev.lightwithin.events.LightCreationAndEvent.determineTarget;

public class Tests {
    public static void testDetermineTarget(int run_times, PlayerEntity p){
        int first = 0;
        int second = 0;
        int third = 0;
        int fourth = 0;
        int fifth = 0;
        int error = 0;
        List<TargetType> list = List.of(TargetType.SELF, TargetType.ALLIES, TargetType.ENEMIES, TargetType.ALL, TargetType.VARIANT);
        UUID id = UUID.randomUUID();
        for(int i = 0; i<run_times; i++){
            String[] id_bits = id.toString().split("-");
            TargetType type = determineTarget(id_bits, TARGET_BIT, list);
            if(type.equals(TargetType.SELF)){
                first++;
            }else if(type.equals(TargetType.ALLIES)){
                second++;
            }else if(type.equals(TargetType.ENEMIES)){
                third++;
            }else if(type.equals(TargetType.ALL)){
                fourth++;
            }else if(type.equals(TargetType.VARIANT)){
                fifth++;
            }else{
                error++;
            }
            id = UUID.randomUUID();
        }

        p.sendMessage(Text.literal("First: §a" + first + " §rpercent: §b" + (double) first*100/run_times));
        p.sendMessage(Text.literal(("Second: §a" + second + " §rpercent: §b" + (double) second*100/run_times)));
        p.sendMessage(Text.literal(("Third: §a" + third + " §rpercent: §b" + (double) third*100/run_times)));
        p.sendMessage(Text.literal(("Fourth: §a" + fourth + " §rpercent: §b" + (double) fourth*100/run_times)));
        p.sendMessage(Text.literal(("Fifth: §a" + fifth + " §rpercent: §b" + (double) fifth*100/run_times)));
        p.sendMessage(Text.literal(("Error: §c" + error)));
    }
}
