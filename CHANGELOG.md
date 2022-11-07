**Changes**
- Added `render_original_model` field to `apugli:entity_texture_overlay` power type. Defaults to `true`.

**Internal**
- Internally rewrote `apugli:edible_item` power type, `tick_rate` is no longer a used field for the power.
- Rewrote `apugli:keys_pressed` entity condition packets to catch use cases where players aren't loaded.

**Bugfixes**
- Fixed incompatibility with Switchy.