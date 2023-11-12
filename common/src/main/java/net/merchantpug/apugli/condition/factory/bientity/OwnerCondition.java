package net.merchantpug.apugli.condition.factory.bientity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Projectile;

public class OwnerCondition implements IConditionFactory<Tuple<Entity, Entity>> {

    public boolean check(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        if (pair.getA() instanceof LivingEntity living) {
            if (pair.getB() instanceof TamableAnimal tamable) {
                return tamable.getOwner() == living;
            }

            if (pair.getB() instanceof AreaEffectCloud areaEffectCloud) {
                return areaEffectCloud.getOwner() == living;
            }
        }

        if (pair.getB() instanceof Projectile projectile) {
            return projectile.getOwner() == pair.getA();
        }


        return false;
    }

}