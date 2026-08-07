<p align="center">
  <img src="https://github.com/YufiriaMazenta/picx-images-hosting/raw/master/banner-en.6il0c8w4vn.png" width="100%" alt="Craftorithm">
</p>

<p align="center">
  <a href="https://www.spigotmc.org/resources/craftorithm-customized-crafting-management-plugin-1-13-1-20-folia-supported.108429/"><img src="https://img.shields.io/badge/SpigotMC-Craftorithm-orange?style=flat-square&logo=spigotmc"></a>
  <a href="https://modrinth.com/plugin/craftorithm"><img src="https://img.shields.io/badge/Modrinth-Craftorithm-green?style=flat-square&logo=modrinth"></a>
  <a href="https://github.com/YufiriaMazenta/Craftorithm/releases"><img src="https://img.shields.io/github/v/release/YufiriaMazenta/Craftorithm?style=flat-square&logo=github"></a>
  <img src="https://img.shields.io/badge/Java-21+-blue?style=flat-square&logo=openjdk">
  <img src="https://img.shields.io/badge/MC-1.20+-green?style=flat-square">
  <img src="https://img.shields.io/badge/Folia-Supported-9b59b6?style=flat-square">
  <img src="https://img.shields.io/badge/License-GPL--3.0-red?style=flat-square">
</p>

<p align="center">
  <b>Advanced Minecraft Recipe Management Plugin</b><br>
  <sub>Extensive Recipe Support · Trigger System · Script Engine · GUI Interface · Item Plugin Integrations</sub>
</p>

<p align="center">
  <a href="README-CN.md"><img src="https://img.shields.io/badge/🇨🇳_中文文档-blue?style=flat-square" alt="中文文档"></a>
</p>

---

## Features

<!-- Feature Cards -->
<p align="center">
  <img src="https://github.com/YufiriaMazenta/picx-images-hosting/raw/master/feature-cards-en.54yh87l2uv.png" width="100%" alt="Features">
</p>

## Quick Start

### Requirements

- **Java**: 21 or higher
- **Server**: Paper / Spigot 1.20+ (Folia supported)

### Installation

1. Download from [SpigotMC](https://www.spigotmc.org/resources/craftorithm-customized-crafting-management-plugin-1-13-1-20-folia-supported.108429/), [Modrinth](https://modrinth.com/plugin/craftorithm), or [GitHub Releases](https://github.com/YufiriaMazenta/Craftorithm/releases)
2. Place the JAR file in the `plugins/` directory
3. Restart the server

### Create Your First Recipe

```
/cra create vanilla_shaped
```

Place ingredients and result items in the GUI, then click confirm. Recipes are automatically saved to `plugins/Craftorithm/recipes/`.

## Powerful Configuration

<!-- Code Showcase -->
<p align="center">
  <img src="https://github.com/YufiriaMazenta/picx-images-hosting/raw/master/code-showcase-en.1ow5g4bfsl.png" width="100%" alt="Code Examples">
</p>

## Result Processors

Version 1.13.0.0 introduces the Result Processors system for adding effects to recipe results:

```yaml
result_processors:
  enchantments:
    type: copy_from_source
  lore:
    type: add
    data:
      value:
        - "&7Legendary Weapon"
        - "&aForged in the Nether"
  attributes:
    type: merge_source
```

Supports `copy_from_source` / `add` / `merge_source` / `remove` strategies for various component types including enchantments, attributes, lore, item flags, and more.

## Compatible Plugins List

<p align="center">
  <img src="https://github.com/YufiriaMazenta/picx-images-hosting/raw/master/integration-en.b9mc30drt.png" width="100%" alt="Compatible Plugins">
</p>

## Documentation

**[Full Documentation](https://yufiriamazenta.github.io/Craftorithm-Docs/en/)**

| Module | Description |
|--------|-------------|
| [Quick Start](https://yufiriamazenta.github.io/Craftorithm-Docs/en/guide/) | Installation, commands, permissions |
| [Recipe System](https://yufiriamazenta.github.io/Craftorithm-Docs/en/recipe/) | Multiple recipe types explained |
| [Trigger System](https://yufiriamazenta.github.io/Craftorithm-Docs/en/trigger/) | Event listeners and conditions |
| [Script Engine](https://yufiriamazenta.github.io/Craftorithm-Docs/en/script/) | Syntax and built-in functions |
| [Item System](https://yufiriamazenta.github.io/Craftorithm-Docs/en/item/) | Custom items and external plugins |
| [Configuration](https://yufiriamazenta.github.io/Craftorithm-Docs/en/config/) | Configuration reference |
| [UI System](https://yufiriamazenta.github.io/Craftorithm-Docs/en/ui/) | GUI and custom menus |
| [API Docs](https://yufiriamazenta.github.io/Craftorithm-Docs/en/api/) | Developer API |

## API

Craftorithm provides a Java API for secondary development:

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.YufiriaMazenta</groupId>
  <artifactId>Craftorithm</artifactId>
  <version>Tag</version>
</dependency>
```

```java
import pers.yufiria.craftorithm.api.CraftorithmAPI;

CraftorithmAPI api = Craftorithm.api();
```

## bStats

![bStats](https://bstats.org/signatures/bukkit/Craftorithm.svg)

## Download

[![SpigotMC](https://img.shields.io/badge/SpigotMC-Craftorithm-orange?style=for-the-badge&logo=spigotmc)](https://www.spigotmc.org/resources/craftorithm-customized-crafting-management-plugin-1-13-1-20-folia-supported.108429/)
[![Modrinth](https://img.shields.io/badge/Modrinth-Craftorithm-green?style=for-the-badge&logo=modrinth)](https://modrinth.com/plugin/craftorithm)
[![GitHub](https://img.shields.io/badge/GitHub-Releases-blue?style=for-the-badge&logo=github)](https://github.com/YufiriaMazenta/Craftorithm/releases)
