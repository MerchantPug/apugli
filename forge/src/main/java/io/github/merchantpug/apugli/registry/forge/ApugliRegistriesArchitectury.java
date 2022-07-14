/*
MIT License

Copyright (c) 2020 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
/*
MIT License

Copyright (c) 2021 EdwinMindcraft

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package io.github.merchantpug.apugli.registry.forge;

import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.ArchitecturyWrappedRegistry;
import io.github.merchantpug.apugli.Apugli;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class ApugliRegistriesArchitectury {
    public static final Lazy<Registries> REGISTRIES = new Lazy<>(() -> Registries.get(Apugli.MODID));

    public static final Registry<PowerFactory<?>> POWER_FACTORY;
    public static final Registry<ConditionFactory<LivingEntity>> ENTITY_CONDITION;
    public static final Registry<ConditionFactory<CachedBlockPosition>> BLOCK_CONDITION;
    public static final Registry<ConditionFactory<Pair<DamageSource, Float>>> DAMAGE_CONDITION;
    public static final Registry<ActionFactory<Entity>> ENTITY_ACTION;
    public static final Registry<ActionFactory<ItemStack>> ITEM_ACTION;
    public static final Registry<ActionFactory<Triple<World, BlockPos, Direction>>> BLOCK_ACTION;

    static {
        Registries registries = REGISTRIES.get();
        Registry<ModRegistriesArchitectury.CFEntity> entityCondition = registries.get(RegistryKey.ofRegistry(new Identifier("origins", "entity_condition")));
        Registry<ModRegistriesArchitectury.CFBlock> blockCondition = registries.get(RegistryKey.ofRegistry(new Identifier("origins", "block_condition")));
        Registry<ModRegistriesArchitectury.CFDamage> damageCondition = registries.get(RegistryKey.ofRegistry(new Identifier("origins", "damage_condition")));
        Registry<ModRegistriesArchitectury.AFEntity> entityAction = registries.get(RegistryKey.ofRegistry(new Identifier("origins", "entity_action")));
        Registry<ModRegistriesArchitectury.AFItem> itemAction = registries.get(RegistryKey.ofRegistry(new Identifier("origins", "item_action")));
        Registry<ModRegistriesArchitectury.AFBlock> blockAction = registries.get(RegistryKey.ofRegistry(new Identifier("origins", "block_action")));

        POWER_FACTORY = registries.get(RegistryKey.ofRegistry(new Identifier("origins", "power_factory")));
        ENTITY_CONDITION = new ArchitecturyWrappedRegistry<>(entityCondition, ModRegistriesArchitectury.CFEntity::new, ModRegistriesArchitectury.CFEntity::get);
        BLOCK_CONDITION = new ArchitecturyWrappedRegistry<>(blockCondition, ModRegistriesArchitectury.CFBlock::new, ModRegistriesArchitectury.CFBlock::get);
        DAMAGE_CONDITION = new ArchitecturyWrappedRegistry<>(damageCondition, ModRegistriesArchitectury.CFDamage::new, ModRegistriesArchitectury.CFDamage::get);
        ENTITY_ACTION = new ArchitecturyWrappedRegistry<>(entityAction, ModRegistriesArchitectury.AFEntity::new, ModRegistriesArchitectury.AFEntity::get);
        ITEM_ACTION = new ArchitecturyWrappedRegistry<>(itemAction, ModRegistriesArchitectury.AFItem::new, ModRegistriesArchitectury.AFItem::get);
        BLOCK_ACTION = new ArchitecturyWrappedRegistry<>(blockAction, ModRegistriesArchitectury.AFBlock::new, ModRegistriesArchitectury.AFBlock::get);
    }
}
