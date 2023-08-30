# Apugli
![https://github.com/MerchantPug/apugli/issues](https://img.shields.io/github/issues/MerchantPug/apugli?color=yellow&style=for-the-badge) ![](https://img.shields.io/github/issues-pr/MerchantPug/apugli?color=lime&style=for-the-badge) ![https://github.com/MerchantPug/apugli/blob/master/LICENSE](https://img.shields.io/github/license/MerchantPug/apugli?style=for-the-badge) ![https://apugli.readthedocs.io/en/latest/](https://img.shields.io/readthedocs/apugli?style=for-the-badge) ![https://discord.gg/UBfEjsANNz](https://img.shields.io/discord/832984808984412191?color=blue&style=for-the-badge)

This is the Apugli repository where the source code for the Apugli Apoli addon is hosted. You may more commonly know this as the TooManyOrigins library for its power types, conditions and actions, either way you can download the .jar from the releases page as a standalone file or through [CurseForge](https://www.curseforge.com/minecraft/mc-mods/apugli) or [Modrinth](https://modrinth.com/mod/apugli).

## Documentation
If you'd like to use Apugli in any Apoli or Origins powers you can read up on the [Documentation](https://apugli.readthedocs.io/en/latest/).
It's recommended to have some knowledge about either mod before doing so.

# Implementing the library into your project

## MerchantPug's Maven

Depending on Apugli has changed ever since v2.0.0, as the mod is now built very differently to what it was prior to this update.

Versions 1.9.2+1.19 and later have/will be uploaded to the MerchantPug maven.

### Common Sourcesets 1.19.x+
<details>
<br>
**build.gradle**

```groovy
repositories {
    ...
    maven {
        name = "Pug's Maven"
        url = 'https://maven.merchantpug.net/releases/'
    }
    maven {
        name = "JitPack"
        url = 'https://jitpack.io'
    }
}

dependencies {
    ...
    compileOnly "net.merchantpug:Apugli:${project.apugli_version}-common"
}
```
</details>

### Fabric/Quilt Loom 1.19.x+
<details>
<br>
**build.gradle**

```groovy
repositories {
    ...
    maven {
        name = "Pug's Maven"
        url = 'https://maven.merchantpug.net/releases/'
    }
    maven {
        name = "Ladysnake Libs"
        url = 'https://ladysnake.jfrog.io/artifactory/mods'
    }
    maven {
        name = "JitPack"
        url = 'https://jitpack.io'
    }
    maven {
        url = 'https://maven.cafeteria.dev'
        content {
            includeGroup 'net.adriantodt.fabricmc'
        }
    }
    maven {
        name = "Shedaniel"
        url "https://maven.shedaniel.me/"
    }
    maven {
        name = "TerraformersMC"
        url "https://maven.terraformersmc.com/"
    }
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
        content {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    ...
    modImplementation(include("net.merchantpug:Apugli:${project.apugli_version}-fabric"))
}
```
You are able to remove the `include` block if you don't wish to include Apugli inside your jar.
</details>

### ForgeGradle 1.19.x+
<details>
<br>
**build.gradle**

```groovy
repositories {
    ...
    maven { 
        url 'https://maven.theillusivec4.top'
    }
    maven {
        name = "JitPack"
        url = 'https://jitpack.io'
    }
}

dependencies {
    ...
    implementation(jarJar(fg.deobf("net.merchantpug:Apugli:${project.apugli_version}-forge"))) {
        jarJar.ranged(it, "[${project.apugli_version},)")
    }
}
```
You are able to remove the `jarJar` blocks if you don't wish to include Apugli inside your jar.
</details>

You can find your correct versions by looking [here](https://maven.merchantpug.net/#/releases/net/merchantpug/Apugli).

## JitPack (Old Builds and Commit Hashes Only) (Backup)

If you are trying to get v1.9.0+1.19 of Apugli, a version before then or a specific commit hash, please use JitPack.

Please reference the above for the repositories block setup for your build tools.

**build.gradle**

```groovy
dependencies {
    // Prior to 2.0.0 (Fabric Only)
    modImplementation(include("com.github.MerchantPug:apugli:${project.apugli_version}"))
    
    // Common Sourcesets
    compileOnly "com.github.MerchantPug.apugli:Common:${project.apugli_version}"
    
    // Fabric/Quilt Loom
    modImplementation(include("com.github.MerchantPug.apugli:Fabric:${project.apugli_version}"))
    
    // ForgeGradle
    implementation(jarJar(fg.deobf("com.github.MerchantPug.apugli:Forge:${project.apugli_version}"))) {
        jarJar.ranged(it, "[${project.apugli_version},)")
    }
}
```