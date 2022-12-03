package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ApugliEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<HitsOnTargetComponent> HITS_ON_TARGET_COMPONENT = ComponentRegistry.getOrCreate(Apugli.identifier("hits_on_target"), HitsOnTargetComponent.class);
    public static final ComponentKey<KeyPressComponent> KEY_PRESS_COMPONENT = ComponentRegistry.getOrCreate(Apugli.identifier("keys_pressed"), KeyPressComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, HITS_ON_TARGET_COMPONENT, HitsOnTargetComponentImpl::new);
        registry.registerFor(PlayerEntity.class, KEY_PRESS_COMPONENT, KeyPressComponentImpl::new);
    }
}
