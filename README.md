**Tested on Java LTS versions from <!--property:java-runtime.min-version-->11<!--/property--> to <!--property:java-runtime.max-version-->25<!--/property-->.**

**Tested on Gradle versions from <!--property:gradle-api.min-version-->7.0<!--/property--> to <!--property:gradle-api.max-version-->9.4.0-rc-1<!--/property-->.**

# `name.remal.idea-settings` plugin

[![configuration cache: not supported](https://img.shields.io/static/v1?label=configuration%20cache&message=not%20supported&color=inactive)](https://docs.gradle.org/current/userguide/configuration_cache.html)

Usage:

<!--plugin-usage:name.remal.idea-settings-->
```groovy
plugins {
    id 'name.remal.idea-settings' version '4.0.7'
}
```
<!--/plugin-usage-->

&nbsp;

A Gradle plugin that allows to configure IntelliJ IDEA directly in build script.

This plugin applies [`org.jetbrains.gradle.plugin.idea-ext`](https://github.com/JetBrains/gradle-idea-ext-plugin) plugin and configures its extensions

## Enables delegating Run/Build and Test actions to Gradle

This plugin [delegates Run/Build and Test actions](https://github.com/JetBrains/gradle-idea-ext-plugin/wiki#delegating-runbuild-and-test-actions) to Gradle.

## Configures encoding of `*.propertiers` files based on JVM version and content of `.editorconfig` file

If the build doesn't use any of [`AbstractCompile`](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/compile/AbstractCompile.html) tasks, then encoding from `.editorconfig` file will be used for `*.properties` files. If the project doesn't have `.editorconfig` file, or encoding is not set there, `ISO-8859-1` encoding will be used by default.

For each of [`AbstractCompile`](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/compile/AbstractCompile.html) tasks [`targetCompatibility`](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/compile/AbstractCompile.html#getTargetCompatibility--) property is taken. If minimum `targetCompatibility` is less than Java 9, then `ISO-8859-1` encoding will be used for `*.properties` files.

If minimum `targetCompatibility` is equal or more than Java 9, encoding from `.editorconfig` file will be used for `*.properties` files. If the project doesn't have `.editorconfig` file, or encoding is not set there, `UTF-8` encoding will be used by default.

Transparent native-to-ascii conversion will be enabled for all encodings except `UTF-8`.

## Enables EditorConfig support

This plugin enables [EditorConfig support](https://www.jetbrains.com/help/idea/editorconfig.html).

## Allows to add required plugins

To add a [required plugin](https://www.jetbrains.com/help/idea/managing-plugins.html#required-plugins):

```groovy
ideaSettings {
  requiredPlugins += [
    'idea.plugin.id-1',
    'idea.plugin.id-2',
  ]
}
```

## Configure default nullability annotation

To configure default annotations [IDEA uses for nullity checks](https://www.jetbrains.com/help/idea/inferring-nullity.html), use this configuration:

```groovy
ideaSettings {
  nullability {
    defaultNotNullAnnotation = 'javax.annotation.Nonnull'
    defaultNullableAnnotation = 'javax.annotation.Nullable'
  }
}
```

## Configures Actions on Save

Native IDEA [Actions on Save](https://www.jetbrains.com/help/idea/saving-and-reverting-changes.html#actions-on-save) functionality is used.

By default, nothing is changed. If the functionality is enabled, it stays enabled. If it's disabled, it stays disabled.

To configure reformatting sources on save:

```groovy
ideaSettings {
  runOnSave {
    reformatMode = 'DISABLED' // to disable automatic reformatting
    reformatMode = 'WHOLE_FILE' // to format the whole file
    reformatMode = 'CHANGED_LINES' // to format changed lines only
  }
}
```

To configure imports optimization on save:

```groovy
ideaSettings {
  runOnSave {
    optimizeImports = false // to disable automatic imports optimization
    optimizeImports = true // to enable automatic imports optimization
  }
}
```

IntelliJ IDEA picks up these changes only by restarting.

## Configures CheckStyle-IDEA plugin

If any of the Gradle projects uses [`checkstyle`](https://docs.gradle.org/current/userguide/checkstyle_plugin.html) Gradle plugin, [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) plugin will be configured:

* IDEA Checkstyle version will be set to the version used by Gradle build
* Dependencies with Third-Party Checks will be configured correctly
* Gradle project's Checkstyle XML config will be used as a config for the CheckStyle-IDEA

To configure CheckStyle-IDEA to use specific XML config:

```groovy
ideaSettings {
  checkstyle {
    configFile = "path/to/xml/config/can/be/relative" // to use specific file with checks
    useBundledSunChecks() // to use bundled Sun checks
    useBundledGoogleChecks() // to use bundled Google checks

    treatErrorsAsWarnings = true // Treat errors as warnings
  }
}
```

## Configures IDEA Ultimate default project SQL dialect

For those developers who use IntelliJ IDEA Ultimate, a default project [SQL dialect](https://www.jetbrains.com/help/idea/settings-languages-sql-dialects.html) can be configured:

```groovy
ideaSettings {
  database {
    defaultDialect = 'PostgreSQL'
  }
}
```

## Configures [Run/debug configurations](https://www.jetbrains.com/help/idea/run-debug-configuration.html)

This plugin can add default JVM parameters to IDEA's Java application run configuration and [Spring Boot run configuration](https://www.jetbrains.com/help/idea/run-debug-configuration-spring-boot.html).

```groovy
ideaSettings {
  runConfigurations {
    javaApplication {
      jvmParameters += [
        '-Xmx1G',
        '-Pspring.profiles.active=local'
      ]
    }
  }
}
```

# Migration guide

## Version 3.* to 4.*

Minimum Java version is 11.

## Version 2.* to 3.*

Package name was changed from `name.remal.gradleplugins.ideasettings` to `name.remal.gradle_plugins.idea_settings`.
