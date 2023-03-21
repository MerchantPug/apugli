**Refactors**
- Updated URL based texture loading.
  - It now checks the SHA256 checksum of a file to make sure that it's not overwriting the exact same texture before loading any texture.
  - It now doesn't create a new texture, instead updating the old texture's image upon reloading.