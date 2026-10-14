package sh.orbis.spawn;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import javax.annotation.Nonnull;

public final class SpawnMobRemovalSystem extends DelayedEntitySystem<EntityStore> {
    private static final Query<EntityStore> QUERY = Query.and(
            NPCEntity.getComponentType(),
            TransformComponent.getComponentType()
    );

    public SpawnMobRemovalSystem() {
        super(0.25f);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    @Override
    public void tick(
            float deltaTime,
            int index,
            @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer
    ) {
        TransformComponent transform =
                archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        World world = store.getExternalData().getWorld();

        if (SpawnProtection.contains(world, transform.getPosition())) {
            Ref<EntityStore> npcRef = archetypeChunk.getReferenceTo(index);
            commandBuffer.tryRemoveEntity(npcRef, RemoveReason.REMOVE);
        }
    }
}
