package net.merchantpug.apugli.power.integration.pehkui;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.power.AbstractValueModifyingPower;
import net.merchantpug.apugli.power.factory.ModifyScalePowerFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

@AutoService(ModifyScalePowerFactory.class)
public class ModifyScalePower extends AbstractValueModifyingPower<ModifyScalePower.Instance> implements ModifyScalePowerFactory<ModifyScalePower.Instance> {
    private static final Set<ResourceLocation> EMPTY_SET = new HashSet<>();

    public ModifyScalePower() {
        super("modify_scale", ModifyScalePowerFactory.getSerializableData(),
            data -> (type, entity) -> new Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    public Class<Instance> getPowerClass() {
        return Instance.class;
    }

    @Override
    public ResourceLocation getPowerId(Instance power) {
        return power.getType().getIdentifier();
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
            setTicking(true);
        }

        @Override
        public void tick() {
            PehkuiUtil.tickScalePower(this, this.entity);
        }

        @Override
        public void onAdded() {
            PehkuiUtil.onAddedScalePower(this, this.entity);
        }

        @Override
        public void onRespawn() {
            PehkuiUtil.onAddedScalePower(this, this.entity);
        }

        @Override
        public Tag toTag() {
            CompoundTag tag = new CompoundTag();
            PehkuiUtil.scalePowerToTag(this, this.entity, tag);
            return tag;
        }

        @Override
        public void fromTag(Tag tag) {
            if (!(tag instanceof CompoundTag compoundTag)) return;
            PehkuiUtil.scalePowerFromTag(this, this.entity, compoundTag);
        }

        @Override
        public void onRemoved() {
            PehkuiUtil.onRemovedScalePower(this, this.entity);
        }
    }
}
