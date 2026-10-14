package sh.orbis.spawn;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public final class SpawnPvpProtectionSystem extends DamageEventSystem {
    private static final Query<EntityStore> QUERY = Query.and(
            Player.getComponentType(),
            TransformComponent.getComponentType()
    );

    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    @Override
    public void handle(
            int index,
            @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer,
            @Nonnull Damage damage
    ) {
        if (!(damage.getSource() instanceof Damage.EntitySource entitySource)) {
            return;
        }

        Ref<EntityStore> attackerRef = entitySource.getRef();
        if (!attackerRef.isValid()
                || commandBuffer.getComponent(attackerRef, Player.getComponentType()) == null) {
            return;
        }

        World world = store.getExternalData().getWorld();
        TransformComponent victimTransform =
                archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        TransformComponent attackerTransform =
                commandBuffer.getComponent(attackerRef, TransformComponent.getComponentType());

        if (SpawnProtection.contains(world, victimTransform.getPosition())
                || attackerTransform != null
                && SpawnProtection.contains(world, attackerTransform.getPosition())) {
            damage.setCancelled(true);
        }
    }
}
