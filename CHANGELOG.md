**Bi-entity Actions**

`apugli:change_hits_on_target`.
- Now includes `timer_change` and `timer_operation` fields, these modify the timer. `timer_change` defaults to 0.
- Due to `timer_change` defaulting to zero, the timer now resets upon using this power.

**Bugfixes**
- Fixed packet overload issues.
- Fixed `apugli:edible_item` overwriting the food item with the `return_stack` when the stack is not empty after consumption.
- Fixed `apugli:redirect_lightning`'s `chance` field having a default value of *null*, it now has no default value.
- Fixed potential string issues within `File Size Limit` config option.
- Fixed other entites not rendering when `apugli:entity_texture_overlay` has `render_original_model` set to true.

**Config**
- Moved server config values to common config. These values do not appear in the ingame config screen.

**Internal**
- Internally rewrote `apugli:keys_pressed` to be less packet heavy and more secure.
- Internally rewrote `apugli:hits_on_target` bi-entity condition to have less conflict and packet heavy.
- Prefixed all unique fields/access methods in mixins that weren't prefixed.