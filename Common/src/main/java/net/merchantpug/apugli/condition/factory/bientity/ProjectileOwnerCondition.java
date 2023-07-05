package net.merchantpug.apugli.condition.factory.bientity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;

public class ProjectileOwnerCondition implements IConditionFactory<Tuple<Entity, Entity>> {

    public boolean check(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        return pair.getB() instanceof Projectile projectile && projectile.getOwner() == pair.getA();
    }

}