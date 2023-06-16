package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.RocketJumpPowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.LinkedList;
import java.util.List;

@Deprecated
@AutoService(RocketJumpPowerFactory.class)
public class RocketJumpPower extends AbstractActiveCooldownPower<RocketJumpPower.Instance> implements RocketJumpPowerFactory<RocketJumpPower.Instance> {

    public RocketJumpPower() {
        super("rocket_jump", RocketJumpPowerFactory.getSerializableData(),
                data -> (type, entity) -> new RocketJumpPower.Instance(type, entity, data));
        allowCondition();
    }

    @Override
    public SerializableData.Instance getDataFromPower(RocketJumpPower.Instance power) {
        return power.data;
    }

    @Override
    public Class<Instance> getPowerClass() {
        return Instance.class;
    }

    @Override
    public void sync(LivingEntity entity, RocketJumpPower.Instance power) {
        PowerHolderComponent.syncPower(entity, power.getType());
    }

    @Override
    public List<AttributeModifier> chargedModifiers(RocketJumpPower.Instance power, Entity entity) {
        return power.getChargedModifiers();
    }

    @Override
    public List<AttributeModifier> waterModifiers(RocketJumpPower.Instance power, Entity entity) {
        return power.getWaterModifiers();
    }

    @Override
    public List<AttributeModifier> damageModifiers(RocketJumpPower.Instance power, Entity entity) {
        return power.getDamageModifiers();
    }

    @Override
    public void execute(RocketJumpPower.Instance power, Entity entity) {
        power.onUse();
    }

    public static class Instance extends AbstractActiveCooldownPower.Instance {
        private final List<AttributeModifier> chargedModifiers = new LinkedList<>();
        private final List<AttributeModifier> waterModifiers = new LinkedList<>();
        private final List<AttributeModifier> damageModifiers = new LinkedList<>();

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
            data.ifPresent("charged_modifier", this::addChargedJumpModifier);
            data.<List<AttributeModifier>>ifPresent("charged_modifiers", modifiers -> modifiers.forEach(this::addChargedJumpModifier));
            data.ifPresent("water_modifier", this::addWaterJumpModifier);
            data.<List<AttributeModifier>>ifPresent("water_modifiers", modifiers -> modifiers.forEach(this::addWaterJumpModifier));
            data.ifPresent("damage_modifier", this::addDamageModifier);
            data.<List<AttributeModifier>>ifPresent("damage_modifiers", modifiers -> modifiers.forEach(this::addDamageModifier));
        }

        @Override
        public void onUse() {
            if (canUse() && !entity.level.isClientSide()) {
                ApugliPowers.ROCKET_JUMP.get().executeJump(this, entity);
            }
        }

        public void addChargedJumpModifier(AttributeModifier modifier) {
            this.chargedModifiers.add(modifier);
        }

        public List<AttributeModifier> getChargedModifiers() {
            return chargedModifiers;
        }

        public void addWaterJumpModifier(AttributeModifier modifier) {
            this.waterModifiers.add(modifier);
        }

        public List<AttributeModifier> getWaterModifiers() {
            return waterModifiers;
        }

        public void addDamageModifier(AttributeModifier modifier) {
            this.damageModifiers.add(modifier);
        }

        public List<AttributeModifier> getDamageModifiers() {
            return damageModifiers;
        }

    }

}