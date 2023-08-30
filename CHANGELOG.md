### Key Pressed
- Optimised `key_pressed` entity condition logic further. 
- Updated log warnings for key pressing syncs. 

### Bugfixes
- Fixed `key_pressed` entity condition not working with serverside exclusive power types.
- [FORGE] Fixed `custom_projectile` power type crashing upon usage.
- [FORGE] Fixed start-up crash caused by a mixin compilation error.

### Developer Notes
Depending on Apugli in a Common/Xplat module has become a lot easier as I have now removed the extra dependencies from the Fabric version of Origins (of which Common used to depend on).