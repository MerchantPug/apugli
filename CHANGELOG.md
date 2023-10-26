### Breaking Change Notes
- `apugli:sprinting` now has a new field called `requires_input`, which defaults to false. This doesn't match the behaviour in previous versions, so set this to true if you wish to have the previous logic.

### Power Types
- Added `requires_input` boolean field to `sprinting` power type (Defaults to false).
- Added `self_target_bientity_action`, `self_nearby_bientity_action`, and `target_nearby_bientity_action` to `damage_nearby_on_hit` power type. (All optional).
- Added `attacker_target_bientity_action`, `attacker_nearby_bientity_action`, and `self_nearby_bientity_action` to `damage_nearby_when_hit` power type. (All optional).

### Entity Condition Types
- Added `crawling` entity condition type.

### Bugfixes
- Fixed crash due to wrong locations of fields when creating damage sources inside `damage_nearby_x_hit` power types.
- Fixed `damage_nearby_on_hit` not running at all.
- Fixed `damage_nearby_x_hit` not considering for the `damage_condition` field.
- Fixed `hover` power type's `step_assist` field not functioning for correcting upper bounds.
- [FABRIC] Fixed `damage_nearby_when_hit` using `damage_nearby_on_hit`'s serializable data.
- [FABRIC] Fixed fabric.mod.json not using the new `fabric-api` mod id. #56
- [FORGE] Fixed incorrect behaviour with stopping from `sprinting` power type.
- [FORGE] Update pack formats.
- [FORGE] Fixed Forge specific damage source creation method referencing an invalid method.