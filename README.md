# Apugli
![https://jitpack.io/#MerchantPug/apugli](https://img.shields.io/jitpack/v/github/MerchantPug/apugli?style=for-the-badge) ![https://github.com/MerchantPug/apugli/issues](https://img.shields.io/github/issues/MerchantPug/apugli?color=yellow&style=for-the-badge) ![](https://img.shields.io/github/issues-pr/MerchantPug/apugli?color=lime&style=for-the-badge) ![https://github.com/MerchantPug/apugli/blob/master/LICENSE](https://img.shields.io/github/license/MerchantPug/apugli?style=for-the-badge) ![https://apugli.readthedocs.io/en/latest/](https://img.shields.io/readthedocs/apugli?style=for-the-badge) ![https://discord.gg/UBfEjsANNz](https://img.shields.io/discord/832984808984412191?color=blue&style=for-the-badge)

This is the Apugli repository where the source code for the Apugli Apoli addon is hosted. You may more commonly know this as the TooManyOrigins/CursedOrigins library for its power types and conditions, either way you can download the .jar from the releases page as a standalone file or through [CurseForge](https://www.curseforge.com/minecraft/mc-mods/apugli) or [Modrinth](https://modrinth.com/mod/apugli).

## Documentation
If you'd like to use Apugli in any Apoli or Origins powers you can read up on the [Documentation](https://apugli.readthedocs.io/en/latest/).
It's recommended to have some knowledge about either mod before doing so.

## Implementing the library into your project

Assuming you're using a 1.17 or above default Fabric workspace, you are able to add Apugli as a dependency to your workspace through adding these to your project.

**build.gradle**
```gradle
repositories {
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
        url "https://maven.shedaniel.me/"
    }
    maven {
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
    modImplementation "com.github.MerchantPug:apugli:${project.apugli_version}"
    include "com.github.MerchantPug:apugli:${project.apugli_version}"
}
```

**gradle.properties**
```properties
apugli_version=[INSERT VERSION HERE]
```
You can find the version number by looking at the releases of the mod and looking at the tag.
