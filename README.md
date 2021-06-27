# Apugli README

This is the Apugli repository where the source code for the Apoli addon is hosted. You may more commonly know this as the TooManyOrigins library for its power types and conditions, either way you can download the .jar from the releases page as a standalone file.

## Documentation
If you'd like to use Apugli in any Apoli or Origins powers you can read up on the [Documentation](https://apugli.readthedocs.io/en/latest/).
It's recommended to have some knowledge about either mod before doing so.

## Implementing the mod into your project

Assuming you're using a 1.17 default Fabric workspace, you are able to add Apugli as a dependency to your workspace through adding these to your project.

**build.gradle**
```gradle
repositories {
    maven {
		url "https://jitpack.io"
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
