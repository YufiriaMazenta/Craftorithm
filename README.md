<p align="center">
  <img src="https://github.com/YufiriaMazenta/picx-images-hosting/raw/master/1000084924.1zize3lhqj.png" width="100%" alt="Craftorithm">
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

## Why Craftorithm

- Matches items by other plugin-provided item ID — renaming, adding enchantments, or even changing item types won't affect crafting
- Implements functionality by injecting into the vanilla crafting system — performance far surpasses plugins that rely on event listeners, with full support for all vanilla features
- Disable recipes from vanilla or other plugins
- Result processors, triggers, custom chest menus, and other powerful customization features

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

Result processors allow you to apply additional modifications to crafting results, for example:

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

## bStats

![bStats](https://bstats.org/signatures/bukkit/Craftorithm.svg)
