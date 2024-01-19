package me.emafire003.dev.lightwithin.util;

import me.emafire003.dev.lightwithin.lights.InnerLightType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

import static me.emafire003.dev.lightwithin.events.LightCreationAndEvent.*;

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

    public static void testDetermineType(int run_times, PlayerEntity p){
        int heal = 0;
        int defence = 0;
        int strength = 0;
        int blazing = 0;
        int frost = 0;
        int earthen = 0;
        int aqua = 0;
        int wind = 0;
        int frog = 0;
        int error = 0;
        UUID id = UUID.randomUUID();
        for(int i = 0; i<run_times; i++){
            String[] id_bits = id.toString().split("-");
            InnerLightType type = determineTypeAndTarget(id_bits, TYPE_BIT, TARGET_BIT).getFirst();
            if(type.equals(InnerLightType.HEAL)){
                heal++;
            }else if(type.equals(InnerLightType.DEFENCE)){
                defence++;
            }else if(type.equals(InnerLightType.STRENGTH)){
                strength++;
            }else if(type.equals(InnerLightType.BLAZING)){
                blazing++;
            }else if(type.equals(InnerLightType.FROST)){
                frost++;
            }else if(type.equals(InnerLightType.EARTHEN)){
                earthen++;
            }else if(type.equals(InnerLightType.AQUA)){
                aqua++;
            }else if(type.equals(InnerLightType.WIND)){
                wind++;
            }else if(type.equals(InnerLightType.FROG)){
                frog++;
            }else{
                error++;
            }
            id = UUID.randomUUID();
        }

        p.sendMessage(Text.literal("Heal: §a" + heal + " §rpercent: §b" + (double) heal*100/run_times));
        p.sendMessage(Text.literal(("Defence: §a" + defence + " §rpercent: §b" + (double) defence*100/run_times)));
        p.sendMessage(Text.literal(("Strength: §a" + strength + " §rpercent: §b" + (double) strength*100/run_times)));
        p.sendMessage(Text.literal(("Blazing: §a" + blazing + " §rpercent: §b" + (double) blazing*100/run_times)));
        p.sendMessage(Text.literal(("Frost: §a" + frost + " §rpercent: §b" + (double) frost*100/run_times)));
        p.sendMessage(Text.literal(("Earthen: §a" + earthen + " §rpercent: §b" + (double) earthen*100/run_times)));
        p.sendMessage(Text.literal(("Aqua: §a" + aqua + " §rpercent: §b" + (double) aqua*100/run_times)));
        p.sendMessage(Text.literal(("Wind: §a" + wind + " §rpercent: §b" + (double) wind*100/run_times)));
        p.sendMessage(Text.literal(("Frog: §a" + frog + " §rpercent: §b" + (double) frog*100/run_times)));
        p.sendMessage(Text.literal(("Error: §c" + error)));
    }
}
