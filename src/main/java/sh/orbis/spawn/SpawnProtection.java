package sh.orbis.spawn;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import org.joml.Vector3d;
import org.joml.Vector3ic;

final class SpawnProtection {
    static final double RADIUS = 64.0;

    private SpawnProtection() {
    }

    static boolean contains(World world, Vector3d position) {
        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
        return spawnProvider != null && spawnProvider.isWithinSpawnDistance(position, RADIUS);
    }

    static boolean contains(World world, Vector3ic position) {
        return contains(world, new Vector3d(position.x(), position.y(), position.z()));
    }
}
