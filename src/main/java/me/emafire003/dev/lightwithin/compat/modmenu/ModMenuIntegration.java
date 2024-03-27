package me.emafire003.dev.lightwithin.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.emafire003.dev.lightwithin.client.LightWithinClient;


public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return LightWithinClient::createConfigScreen;
    }
}
