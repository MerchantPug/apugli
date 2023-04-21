package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableDataType;
import net.minecraft.world.entity.Entity;

import java.util.List;

@SuppressWarnings("unchecked")
@AutoService(IModifierHelper.class)
public class FabricModifierHelper {

    @Override
    public SerializableDataType<Modifier> getModifierDataType() {
        return Modifier.DATA_TYPE;
    }

    @Override
    public SerializableDataType<List<Modifier>> getModifiersDataType() {
        return Modifier.LIST_TYPE;
    }

    @Override
    public double modify(Entity entity, List<?> modifiers, double value) {
        if (modifiers.stream().anyMatch(o -> o instanceof Modifier))
            return ModifierUtil.applyModifiers(entity, (List<Modifier>) modifiers, value);
        return value;
    }

}
