# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Minecraft Fabric mod called "Atemporal Settlement" targeting Minecraft 1.20.1. The mod adds custom items (currently copper mace implementations) and uses the Fabric modding framework.

## Build Commands

- **Build the mod**: `./gradlew build`
- **Run client for testing**: `./gradlew runClient`
- **Run server for testing**: `./gradlew runServer`
- **Generate data**: `./gradlew runDatagen`
- **Clean build**: `./gradlew clean`

## Development Setup

- Java 17 is required (configured in build.gradle)
- Uses Fabric Loom for Minecraft mod development
- Yarn mappings for deobfuscation: `1.20.1+build.10`
- Fabric Loader version: `0.16.14`
- Fabric API version: `0.92.6+1.20.1`

## Code Architecture

### Core Structure
- **Main mod class**: `com.bydaffi.atemp.AtemporalSettlement` - Main mod initializer
- **Data generation**: `com.bydaffi.atemp.AtemporalSettlementDataGenerator` - Handles data generation (currently empty)
- **Items package**: `com.bydaffi.atemp.item.*` - Contains all custom items
- **Mixins package**: `com.bydaffi.atemp.mixin.*` - Contains Mixin modifications

### Item System
- `AtempItems.java` - Central item registry and item group management
- `CampingMace.java` - Base mace class (currently empty implementation)
- `CopperMace.java` - Copper variant with block interaction logging
- Items are registered in the Tools creative tab

### Mixin System
- Configured in `atemp.mixins.json` with Java 17 compatibility
- `ExampleMixin.java` - Example mixin injecting into MinecraftServer.loadWorld()

## Key Configuration Files

- `fabric.mod.json` - Mod metadata and entrypoints
- `atemp.mixins.json` - Mixin configuration
- `gradle.properties` - Version definitions and Gradle settings
- `en_us.json` - English localization file

## Testing

The project includes a `run/` directory with a test world and configuration for development testing. Use the Gradle run tasks to test in the development environment.

## Mod ID and Namespace

- Mod ID: `atemp`
- Package: `com.bydaffi.atemp`
- Maven group: `com.bydaffi.atemp`