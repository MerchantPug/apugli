package net.merchantpug.apugli.action.factory.bientity;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.action.factory.CustomEffectCloudBaseAction;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.entity.CustomAreaEffectCloud;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class SpawnCustomEffectCloudAction extends CustomEffectCloudBaseAction implements IActionFactory<Tuple<Entity, Entity>> {

    @Override
    public SerializableData getSerializableData() {
        return super.getSerializableData()
                .add("spawn_target", SerializableDataType.enumValue(PairEntity.class), PairEntity.TARGET);
    }

    @Override
    public void execute(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        Entity positionalEntity = data.get("spawn_target") == PairEntity.TARGET ? pair.getB() : pair.getA();
        CustomAreaEffectCloud cloud = createCloud(data, pair.getA(), pair.getB(), positionalEntity.getX(), positionalEntity.getY(), positionalEntity.getZ());
        pair.getB().level().addFreshEntity(cloud);
    }

    private enum PairEntity {
        ACTOR,
        TARGET
    }

}
