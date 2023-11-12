## Breaking Change Notes
- `apugli:sprinting` now has a new field called `requires_input`, which defaults to false. This doesn't match the behaviour in previous versions, so set this to true if you wish to have the previous logic.

## Known Issues
- [FORGE] Modify Scale does not properly sync with resource values on the client. This is an Origins Forge bug that I'll go and fix next update.

### Power Types
- Added `modify_scale` power type. Requires Pehkui to function. If Pehkui isn't loaded, any powers with this power type will fail to load.
- Added `crawling`, and `prevent_entity_selection` power types.
- Added `requires_input` boolean field to `sprinting` power type (Defaults to false).
- Added `function` field to `modify_durability_change` power type. Can be `floor`, `round`, or `ceiling`. Defaults to `floor`.
- Added `self_target_bientity_action`, `self_nearby_bientity_action`, and `target_nearby_bientity_action` to `damage_nearby_on_hit` power type. (All optional).
- Added `attacker_target_bientity_action`, `attacker_nearby_bientity_action`, and `self_nearby_bientity_action` to `damage_nearby_when_hit` power type. (All optional).

### Bi-entity Condition Types
- Added `compare_dimensions` bi-entity condition type.
- Added `compare_scale` bi-entity condition type. If Pehkui isn't loaded, this condition will return true.

### Entity Condition Types
- Added `crawling`, and `dimensions` entity condition types.
- Added `scale` entity condition type. If Pehkui isn't loaded, this condition will always compare to 1.0.

### Bugfixes
- Fixed crash due to wrong locations of fields when creating damage sources inside `damage_nearby_*_hit` power types.
- Fixed `damage_nearby_on_hit` not running at all.
- Fixed `damage_nearby_*_hit` not considering for the `damage_condition` field.
- Fixed `hover` power type's `step_assist` field not functioning for correcting upper bounds.
- Fixed `allow_anvil_enchant`'s `comparison` field not defaulting to `>=`.
- Fixed `modify_enchantment_level` not syncing its initial values to clients. #58
- [FABRIC] Fixed `damage_nearby_when_hit` using `damage_nearby_on_hit`'s serializable data.
- [FABRIC] Fixed fabric.mod.json not using the new `fabric-api` mod id. #56
- [FORGE] Fixed incorrect behaviour with stopping from `sprinting` power type.
- [FORGE] Update pack formats.
- [FORGE] Fixed Forge specific damage source creation method referencing an invalid method.

### Internal
- Prefixed every mixin method. #59
- [FABRIC] Changed `fabric` dependency in fabric.mod.json to `fabric-api`. #56