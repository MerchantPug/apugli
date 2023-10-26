### Breaking Change Notes
- `apugli:sprinting` now has a new field called `requires_input`, which defaults to false. This doesn't match the behaviour in previous versions, so set this to true if you wish to have the previous logic.

### Power Types
- Added `requires_input` boolean field to `sprinting` power type (Defaults to false).

### Bugfixes
- [FORGE] Fixed incorrect behaviour with stopping from `sprinting` power type.