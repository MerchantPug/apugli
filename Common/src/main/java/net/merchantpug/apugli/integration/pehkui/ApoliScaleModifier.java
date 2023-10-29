package net.merchantpug.apugli.integration.pehkui;

import net.merchantpug.apugli.platform.Services;
import net.minecraft.resources.ResourceLocation;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;

import java.util.List;

public class ApoliScaleModifier extends ScaleModifier {

    private final List<?> modifiers;
    private final ResourceLocation id;

    public ApoliScaleModifier(List<?> modifiers, ResourceLocation id) {
        super();
        this.modifiers = modifiers;
        this.id = id;
    }

    @Override
    public int compareTo(ScaleModifier o)
    {
        if (!(o instanceof ApoliScaleModifier ao)) {
            return -1;
        }

        final int c = Float.compare(o.getPriority(), getPriority());

        return c != 0 ? c :
                this.id.compareTo(ao.id);
    }

    public ResourceLocation getId() {
        return id;
    }

    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        return (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale);
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        return (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale);
    }
}
