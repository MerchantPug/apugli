### Power Types
- Added `action_on_jump`, `custom_projectile`, `modify_durability_change`, `modify_fov`, `prevent_movement_check`, `sprinting`, and `step_assist` power types.

### Action Types
- Added `custom_projectile` and `spawn_custom_effect_cloud` entity action types.
- Added `spawn_custom_effect_cloud` bi-entity action type.
- Added `schedule_tick` block action type.
- Backported `apoli:area_of_effect` block action type.

### Condition Types
- Added `custom_entity_id` and `owner` bi-entity condtiion types.
- Added `in_rain`, `raining` and `thundering` block condition types.
- Added `grounded`, `raining`, `thundering` entity condition types.

### Bugfixes
- Fixed errors involving the `step_assist` field's functionality in the `hover` power, these fixes also carry over to the new `step_assist` power.
- Removed code that may have caused `action_on_durability_change` to run more times than what's necessary.
- Fixed certain content that references an entity from an ItemStack not functioning. e.g. `modify_enchantment_level`