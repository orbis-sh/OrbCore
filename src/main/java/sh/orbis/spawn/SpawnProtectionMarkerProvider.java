package sh.orbis.spawn;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerBuilder;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MarkersCollector;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public final class SpawnProtectionMarkerProvider implements WorldMapManager.MarkerProvider {
    public static final String ID = "OrbCore:SpawnProtection";
    public static final SpawnProtectionMarkerProvider INSTANCE = new SpawnProtectionMarkerProvider();

    private final Map<Player, UUID> playerIds =
            Collections.synchronizedMap(new IdentityHashMap<>());

    private SpawnProtectionMarkerProvider() {
    }

    @Override
    public void update(@Nonnull World world, @Nonnull Player player, @Nonnull MarkersCollector collector) {
        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
        if (spawnProvider == null) {
            return;
        }

        UUID playerId = playerIds.get(player);
        if (playerId == null) {
            return;
        }

        Transform spawnPoint = spawnProvider.getSpawnPoint(world, playerId);
        if (spawnPoint == null) {
            return;
        }

        MapMarker marker = new MapMarkerBuilder(ID, "Spawn.png", spawnPoint)
                .withCustomName("Protected Spawn (64 block radius)")
                .build();
        collector.addIgnoreViewDistance(marker);
    }

    public void trackPlayer(Player player, UUID playerId) {
        playerIds.put(player, playerId);
    }

    public void untrackPlayer(Player player) {
        playerIds.remove(player);
    }

    public void clearPlayers() {
        playerIds.clear();
    }
}
