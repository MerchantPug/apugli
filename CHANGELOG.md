### Power Types
- Added `action_on_jump`, `modify_durability_change`, `modify_fov`, `prevent_movement_check`, `sprinting`, and `step_assist` power types.

### Entity Conditions
- Added `grounded` entity condition type.

### Bugfixes
- Fixed errors involving the `step_assist` field's functionality in the `hover` power, these fixes also carry over to the new `step_assist` power.
- Removed code that may have caused `action_on_durability_change` to run more times than what's necessary.