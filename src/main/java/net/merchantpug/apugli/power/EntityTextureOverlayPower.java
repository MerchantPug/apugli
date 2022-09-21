package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class EntityTextureOverlayPower extends Power {
    public final Identifier textureLocation;
    public final String textureUrl;
    public final boolean showFirstPerson;
    public final boolean usesRenderingPowers;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("entity_texture_overlay"),
                new SerializableData()
                        .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                        .add("texture_url", SerializableDataTypes.STRING, null)
                        .add("show_first_person", SerializableDataTypes.BOOLEAN, false)
                        .add("use_rendering_powers", SerializableDataTypes.BOOLEAN,  false),
                data ->
                        (type, player) ->
                                new EntityTextureOverlayPower(type, player, data.getId("texture_location"), data.getString("texture_url"), data.getBoolean("show_first_person"), data.getBoolean("use_rendering_powers")))
                .allowCondition();
    }

    public EntityTextureOverlayPower(PowerType<?> type, LivingEntity entity, Identifier textureLocation, String textureUrl, boolean showFirstPerson, boolean usesRenderingPowers) {
        super(type, entity);
        if (textureLocation == null && textureUrl == null) {
            Apugli.LOGGER.warn("EntityTextureOverlayPower '" + this.getType().getIdentifier() + "' does not have a valid `texture_location` or `texture_url` field. This power will not render.");
        }
        this.textureLocation = textureLocation;
        this.textureUrl = textureUrl;
        this.showFirstPerson = showFirstPerson;
        this.usesRenderingPowers = usesRenderingPowers;
    }

    public Identifier getUrlTextureIdentifier() {
        return new Identifier(Apugli.MODID, "entitytextureoverlaypower/" + this.getType().getIdentifier().getNamespace() + "/" + this.getType().getIdentifier().getPath());
    }
}
