package io.github.merchantpug.apugli.access;

import io.github.merchantpug.apugli.behavior.MobBehavior;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Pair;

import java.util.List;

public interface MobEntityAccess {
    List<Pair<MobBehavior, Goal>> getModifiedTargetSelectorGoals();
    List<Pair<MobBehavior, Goal>> getModifiedGoalSelectorGoals();
}
