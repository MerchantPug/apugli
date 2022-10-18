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

package com.github.merchantpug.apugli.condition.factory.entity;

import com.github.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;

//TODO: implement key checking
public class KeyPressedCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData().add("key", ApoliDataTypes.KEY);
    }
    
    public boolean check(SerializableData.Instance data, Entity entity) {
        if(!Apugli.keysToCheck.containsKey(entity.getUUID())) {
            Apugli.keysToCheck.put(entity.getUUID(), new HashSet<>());
        }
        if(Apugli.keysToCheck.get(entity.getUUID()).stream().noneMatch(key -> key.equals(data.get("key")))) {
            Apugli.keysToCheck.get(entity.getUUID()).add(data.get("key"));
        }
        if(entity instanceof Player) {
            if(entity.level.isClientSide) {
               ApugliClient.handleActiveKeys((Player)entity);
            }
            if(Apugli.currentlyUsedKeys.containsKey(entity.getUUID())) {
                return Apugli.currentlyUsedKeys.get(entity.getUUID()).stream().anyMatch(key -> key.equals(data.get("key")));
            }
        }
        return false;
    }

}
