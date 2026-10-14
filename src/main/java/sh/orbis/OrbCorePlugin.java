package sh.orbis;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.RemovedPlayerFromWorldEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import sh.orbis.spawn.SpawnMobRemovalSystem;
import sh.orbis.spawn.SpawnBlockProtectionSystems;
import sh.orbis.spawn.SpawnProtectionMarkerProvider;
import sh.orbis.spawn.SpawnPvpProtectionSystem;

import javax.annotation.Nonnull;

public final class OrbCorePlugin extends JavaPlugin {
    public OrbCorePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getEntityStoreRegistry().registerSystem(new SpawnPvpProtectionSystem());
        getEntityStoreRegistry().registerSystem(new SpawnMobRemovalSystem());
        getEntityStoreRegistry().registerSystem(new SpawnBlockProtectionSystems.Break());
        getEntityStoreRegistry().registerSystem(new SpawnBlockProtectionSystems.Damage());
        getEventRegistry().registerGlobal(StartWorldEvent.class, event -> addMapMarker(event.getWorld()));
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::trackPlayer);
        getEventRegistry().registerGlobal(RemovedPlayerFromWorldEvent.class, event -> {
            var player = event.getHolder().getComponent(
                    com.hypixel.hytale.server.core.entity.entities.Player.getComponentType()
            );
            if (player != null) {
                SpawnProtectionMarkerProvider.INSTANCE.untrackPlayer(player);
            }
        });
    }

    @Override
    protected void start() {
        Universe.get().getWorlds().values().forEach(this::addMapMarker);
    }

    @Override
    protected void shutdown() {
        SpawnProtectionMarkerProvider.INSTANCE.clearPlayers();
        Universe.get().getWorlds().values().forEach(world ->
                world.getWorldMapManager().getMarkerProviders().remove(SpawnProtectionMarkerProvider.ID));
    }

    private void trackPlayer(PlayerReadyEvent event) {
        Ref<EntityStore> playerEntity = event.getPlayerRef();
        Store<EntityStore> store = playerEntity.getStore();
        PlayerRef playerRef = store.getComponent(playerEntity, PlayerRef.getComponentType());
        if (playerRef != null) {
            SpawnProtectionMarkerProvider.INSTANCE.trackPlayer(event.getPlayer(), playerRef.getUuid());
        }
    }

    private void addMapMarker(World world) {
        world.getWorldMapManager().addMarkerProvider(
                SpawnProtectionMarkerProvider.ID,
                SpawnProtectionMarkerProvider.INSTANCE
        );
    }
}
