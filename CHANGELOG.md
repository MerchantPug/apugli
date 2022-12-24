**Power Types**
- Added `apugli:action_on_projectile_hit` power type.
- Added `apugli:projectile_action_over_time` power type.
- Added `targetable_bientity_condition` and `damage_bientity_condition` fields to `apugli:rocket_jump`.
- Additional optimisations to `apugli:rocket_jump`.

**Action Types**
- Added `apugli:spawn_particles` action type.

**Condition Types**
- Added `apugli:can_take_damage` entity condition type. Accepts a `source` field.
- Added `apugli:trident_enchantment`entity condition type. Allows checking of enchantments on a trident that the condition is testing.

**Bugfixes**
- Fixed the Hits On Target component trying to sync entities that aren't loaded.