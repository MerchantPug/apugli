**Power Types**
- Added new `apugli:action_on_harm` and `apugli:action_when_harmed` power types.
- Added new `render_player_outer_layer` field to `apugli:entity_texture_overlay` power type.

**Entity Action Types**
- Added new `bientity_action` field to `apugli:fire_projectile`. This acts with the entity that used the action as the actor and the projectile as the target.

**Entity Condition Types**
- Added new `apugli:moving` entity condition, this checks if the entity has moved in any direction this tick.

**Bugfixes**
- Fixed `apugli:keys_pressed` breaking upon reloading the player (such as entering a new dimension).
- Fixed `apugli:hits_on_target` component attempting to retrieve based on entity as opposed to id when adding new values.

**Internal**
- Interally rewrote packet code to better prepare for eventual multiloader rewrite.