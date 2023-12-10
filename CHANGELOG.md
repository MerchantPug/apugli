# Known Issues
Modify Scale is broken, I know about it. I got frustrated trying to fix it, so I released this version early so TooManyOrigins players don't have to wait.

## Power Types
- Added `stop_after` field to `action_on_projectile_hit` power type.

## Entity Condition Types
- Added `space` field to `velocity` entity condition type.

## Bugfixes
- Fixed `compare_scales` crashing when `scale_type` is specified.
- Fixed `compare_scales`s `scale_types` field not being counted.
- Fixed mixin conflict with Horse Buff.