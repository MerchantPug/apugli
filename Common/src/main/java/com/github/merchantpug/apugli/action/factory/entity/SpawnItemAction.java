package com.github.merchantpug.apugli.action.factory.entity;

import com.github.merchantpug.apugli.action.factory.IActionFactory;
import com.mojang.math.Vector3f;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Space;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SpawnItemAction implements IActionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("stack", SerializableDataTypes.ITEM_STACK, null)
            .add("stacks", SerializableDataTypes.ITEM_STACKS, null)
            .add("pick_up_delay", SerializableDataTypes.INT, 10)
            .add("retain_ownership", SerializableDataTypes.BOOLEAN, false)
            .add("throw_randomly", SerializableDataTypes.BOOLEAN, false)
            .add("space", ApoliDataTypes.SPACE, Space.LOCAL)
            .add("velocity", SerializableDataTypes.VECTOR, null);
    }
    
    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
        List<ItemStack> stacks = new ArrayList<>();
        if(data.isPresent("stack")) stacks.add(data.get("stack"));
        if(data.isPresent("stacks")) stacks.addAll(data.get("stacks"));
        if(stacks.isEmpty()) return;
        
        int pickupDelay = data.getInt("pick_up_delay");
        boolean retainOwnership = data.getBoolean("retain_ownership");
        boolean throwRandomly = data.getBoolean("throw_randomly");
        Level level = entity.level;
        Supplier<Vec3> nextVelocity;
        if(throwRandomly) {
            RandomSource random = entity instanceof LivingEntity living ? living.getRandom() : level.random;
            nextVelocity = () -> {
                float hVelocity = random.nextFloat() * 0.5F;
                float hRot = random.nextFloat() * 6.2831855F;
                return new Vec3(-Mth.sin(hRot) * hVelocity, 0.2, Mth.cos(hRot));
            };
        } else {
            Vector3f vector = data.isPresent("velocity") && !throwRandomly
                ? new Vector3f((Vec3) data.get("velocity"))
                : Vector3f.ZERO;
            if(!throwRandomly) ((Space)data.get("space")).toGlobal(vector, entity);
            Vec3 velocity = new Vec3(vector);
            nextVelocity = () -> velocity;
        }
        
        stacks.stream()
            .filter(ItemStack::isEmpty)
            .map(stack -> createItemEntity(entity, stack, pickupDelay, retainOwnership, nextVelocity.get()))
            .forEach(level::addFreshEntity);
    }
    
    private ItemEntity createItemEntity(Entity entity, ItemStack stack, int pickupDelay, boolean retainOwnership, Vec3 velocity) {
        ItemEntity itemEntity = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), stack);
        itemEntity.setPickUpDelay(pickupDelay);
        itemEntity.setThrower(entity.getUUID());
        if(retainOwnership) itemEntity.setOwner(entity.getUUID());
        itemEntity.setDeltaMovement(velocity);
        return itemEntity;
    }

}
