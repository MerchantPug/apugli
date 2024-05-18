## Depending on alpha versions of Apugli
For the time being, to depend on these alphas while Origins itself is being released as alphas, you will have to use the snapshot branch of the Greenhouse maven.

To do so, swap out `releases` in the link with `snapshots`.

```diff
maven {
    name = "Greenhouse"
-    url = "https://maven.greenhouseteam.dev/releases/"
+    url = "https://maven.greenhouseteam.dev/snapshots/"
}
```

### Changes
- Barebones port to 1.20.4