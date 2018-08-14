package io.github.prokopiy.mcplacelimiter;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "mc-place-limiter",
        name = "MC Place Limiter",
        description = "Spongeforge MC Place Limiter",
        url = "https://github.com/prokopiy/",
        authors = {
                "Prokopiy"
        }
)
public class Main {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }
}
