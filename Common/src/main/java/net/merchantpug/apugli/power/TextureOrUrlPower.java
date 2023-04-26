package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Locale;

public abstract class TextureOrUrlPower extends Power {
    protected final ResourceLocation textureLocation;
    protected final String textureUrl;

    public TextureOrUrlPower(PowerType<?> type, LivingEntity entity, ResourceLocation textureLocation, String textureUrl) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.textureUrl = textureUrl;
        if(textureLocation == null && textureUrl == null) {
            Apugli.LOG.warn(getPowerClassString() + " '" + this.getType().getIdentifier() + "' does not have a valid `texture_location` or `texture_url` field. This power will not render.");
        }
    }

    @Nullable
    public ResourceLocation getTextureLocation() {
        return this.textureLocation;
    }

    @Nullable
    public String getTextureUrl() {
        return this.textureUrl;
    }

    public ResourceLocation getUrlTextureIdentifier() {
        return new ResourceLocation(Apugli.ID, this.getPowerClassString().toLowerCase(Locale.ROOT) + "/" + this.getType().getIdentifier().getNamespace() + "/" + this.getType().getIdentifier().getPath());
    }

    public String getPowerClassString() {
        throw new RuntimeException(String.format("%s does not implement TextureOrUrlPower#getPowerClassString", this.getClass()));
    }

    public static SerializableData getSerializableData() {
        return new SerializableData()
                .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                .add("texture_url", SerializableDataTypes.STRING, null);
    }
}