## Apugli v1.8.2 (Fabric/Quilt 1.19)
**Power Types**
- Changed `apugli:redirect_lightning` to prioritise higher chances over lower chances first.

**Bugfixes**
- Fixed mixin incompatibility with GeckoLib to do with `apugli:modify_equipped_item_render`
- Fixed `apugli:prevent_sound` not functioning properly.
- Fixed `apugli:key_pressed` logic to now work in contexts that happen within one tick on the first instance of them happening (e.g. `apoli:if_else_list`).

**Miscellaneous**
- Apugli will have its version check disabled if the mod is inside another mod. (This does not work on Quilt and will perform the check anyway).