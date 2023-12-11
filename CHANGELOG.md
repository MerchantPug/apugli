# Known Issues
Modify Scale is broken, I know about it. I got frustrated trying to fix it, so I released this version early so TooManyOrigins players don't have to wait.

## Power Types
- Added `stop_after` field to the `action_on_projectile_hit` power type.
- `action_when_projectile_hit` now functions when a non living entity is the owner of the projectile.

## Entity Condition Types
- Added `space` field to `velocity` entity condition type.

## Bugfixes
- Fixed `compare_scales` crashing when `scale_type` is specified.
- Fixed `compare_scales`s `scale_types` field not being counted.
- Fixed Modify Enchantment Level not functioning as expected.
- Fixed mixin conflict with Horse Buff.
- [FORGE] Fixed item capabilities being broken.

## Miscellaneous
- Allow Forge versions above Forge 47.1.3 now that it has backwards compatibility.
- Update Mixin Extras to 0.3.1