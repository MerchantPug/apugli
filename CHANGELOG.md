**Power Types**
- Added `apugli:action_on_projectile_hit` power type.
- Added `apugli:projectile_action_over_time` power type.
- Added `targetable_bientity_condition` and `damage_bientity_condition` fields to `apugli:rocket_jump`.
- Additional optimisations to `apugli:rocket_jump`.

**Action Types**
- Added `apugli:spawn_particles` entity action type. (Use only if you have to as this entity action may be removed when Apoli updated to 1.19.3.)

**Condition Types**
- Added `apugli:attacker_condition` entity condition type. Allows testing a bi-entity condition with the entity's attacker as the actor and the entity as the target.
- Added `apugli:attack_target_condition` entity condition type. Allows testing a bi-entity condition with the entity as the actor and its attack target as the target.
- Added `apugli:can_take_damage` entity condition type. Accepts a `source` field.
- Added `apugli:max_health` entity condition type. Accepts `comparison` and `compare_to` values.
- Added `apugli:trident_enchantment`entity condition type. Allows checking of enchantments on a trident that the condition is testing.

**Bugfixes**
- Fixed the Hits On Target component trying to sync entities that aren't loaded.

**Internal**
- Rewrote `apugli:modify_soul_speed` power type's logic.