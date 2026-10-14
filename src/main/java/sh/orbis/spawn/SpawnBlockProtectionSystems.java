package sh.orbis.spawn;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.CancellableEcsEvent;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.joml.Vector3ic;

import javax.annotation.Nonnull;
import java.util.function.Function;

public final class SpawnBlockProtectionSystems {
    private SpawnBlockProtectionSystems() {
    }

    public static final class Break extends BlockGuardSystem<BreakBlockEvent> {
        public Break() {
            super(BreakBlockEvent.class, BreakBlockEvent::getTargetBlock);
        }
    }

    public static final class Damage extends BlockGuardSystem<DamageBlockEvent> {
        public Damage() {
            super(DamageBlockEvent.class, DamageBlockEvent::getTargetBlock);
        }
    }

    private abstract static class BlockGuardSystem<E extends CancellableEcsEvent>
            extends EntityEventSystem<EntityStore, E> {
        private final Function<E, Vector3ic> targetPosition;

        protected BlockGuardSystem(Class<E> eventType, Function<E, Vector3ic> targetPosition) {
            super(eventType);
            this.targetPosition = targetPosition;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return Archetype.empty();
        }

        @Override
        public void handle(
                int index,
                @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                @Nonnull Store<EntityStore> store,
                @Nonnull CommandBuffer<EntityStore> commandBuffer,
                @Nonnull E event
        ) {
            World world = store.getExternalData().getWorld();
            if (SpawnProtection.contains(world, targetPosition.apply(event))) {
                event.setCancelled(true);
            }
        }
    }
}
