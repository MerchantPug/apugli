/*
MIT License

Copyright (c) 2021 apace100

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

package net.merchantpug.apugli.condition.entity;

import net.merchantpug.apugli.ApugliClient;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;

public class KeyPressedCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (!Apugli.keysToCheck.containsKey(entity.getUuid())) {
            Apugli.keysToCheck.put(entity.getUuid(), new HashSet<>());
        }
        if (Apugli.keysToCheck.get(entity.getUuid()).stream().noneMatch(key -> key.equals(data.get("key")))) {
            Apugli.keysToCheck.get(entity.getUuid()).add(data.get("key"));
        }
        if (entity instanceof PlayerEntity) {
            if (entity.world.isClient) {
               ApugliClient.handleActiveKeys((PlayerEntity)entity);
            }
            if (Apugli.currentlyUsedKeys.containsKey(entity.getUuid())) {
                return Apugli.currentlyUsedKeys.get(entity.getUuid()).stream().anyMatch(key -> key.equals(data.get("key")));
            }
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("key_pressed"), new SerializableData()
                .add("key", ApoliDataTypes.KEY),
                KeyPressedCondition::condition
        );
    }
}
