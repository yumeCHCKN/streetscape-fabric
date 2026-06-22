package net.zphyghtning.streetscape.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.zphyghtning.streetscape.Streetscape;

public class ModScreenHandlers {
    public static final ScreenHandlerType<RoadworksScreenHandler> ROADWORKS_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(Streetscape.MOD_ID, "roadworks_table"),
            new ScreenHandlerType<>(RoadworksScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );

    public static void registerScreenHandlers() {
        Streetscape.LOGGER.info("Registering Screen Handlers for " + Streetscape.MOD_ID);
    }
}
