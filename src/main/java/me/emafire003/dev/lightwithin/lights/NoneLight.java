package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.component.LightComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class NoneLight extends InnerLight{

    /**
     * Creates an instance of this InnerLight. Remember to register it!
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     */
    public NoneLight(TypeCreationRegex regex) {
        super(regex);
    }

    public NoneLight() {
        super( (s) -> false);
    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        player.sendMessage(Text.literal("FATAL ERROR: This light is a NONE light. Please report this issues along with the log file!"));
    }
}
