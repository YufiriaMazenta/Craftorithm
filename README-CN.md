<p align="center">
  <img src="https://github.com/YufiriaMazenta/picx-images-hosting/raw/master/1000084925.4cllvazaxq.png" width="100%" alt="Craftorithm">
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
  <b>高级 Minecraft 配方管理插件</b><br>
  <sub>广泛配方支持 · 触发器系统 · 脚本引擎 · GUI 界面 · 物品插件集成</sub>
</p>

<p align="center">
  <a href="README.md"><img src="https://img.shields.io/badge/🇬🇧_English-blue?style=flat-square" alt="English"></a>
</p>

---

## 为什么选择 Craftorithm

- 基于其他插件提供的物品 ID 进行匹配，物品改名、增加附魔，甚至更改物品类型，都不会影响合成
- 通过注入原版合成系统的方式实现功能，性能远超基于监听器实现配方功能的插件，且支持所有的原版功能
- 可禁用原版或其他插件的配方
- 结果处理器、触发器、自定义箱子菜单等附加强大自定义功能

## 功能特性

<!-- 功能卡片 -->
<p align="center">
  <img src="https://github.com/YufiriaMazenta/picx-images-hosting/raw/master/feature-cards-cn.32iok5mhti.png" width="100%" alt="功能特性">
</p>

## 快速开始

### 前置条件

- **Java**: 21 或更高版本
- **服务端**: Paper / Spigot 1.20+（支持 Folia）

### 安装

1.
从 [SpigotMC](https://www.spigotmc.org/resources/craftorithm-customized-crafting-management-plugin-1-13-1-20-folia-supported.108429/)、[Modrinth](https://modrinth.com/plugin/craftorithm)
或 [GitHub Releases](https://github.com/YufiriaMazenta/Craftorithm/releases) 下载插件
2. 将 JAR 文件放入 `plugins/` 目录
3. 重启服务器

### 创建第一份配方

```
/cra create vanilla_shaped
```

在 GUI 中放入材料和产出物品，点击确认即可保存。配方自动保存到 `plugins/Craftorithm/recipes/` 目录。

## 强大的配置能力

<!-- 代码展示 -->
<p align="center">
  <img src="https://github.com/YufiriaMazenta/picx-images-hosting/raw/master/code-showcase-cn.1763rja27o.png" width="100%" alt="代码示例">
</p>

### 结果处理器

结果处理器是用于对合成结果进行额外修改的功能，例如：

```yaml
result_processors:
  enchantments:
    type: copy_from_source
  lore:
    type: add
    data:
      value:
        - "&7传世神器"
        - "&a由下界之星锻造"
  attributes:
    type: merge_source
```

支持 `copy_from_source` / `add` / `merge_source` / `remove` 四种策略，可对附魔、属性、Lore、物品标志等多种组件进行操作。

### 合成剩余材料

`remainders` 用于控制工作台配方中每个输入槽在合成后留下的物品，支持扣除工具耐久、返回指定物品或原样保留：

```yaml
remainders:
  - ingredient: minecraft:flint_and_steel
    type: damage
    damage: 1
  - ingredient: my_items:magic_water_bucket
    type: replace
    item: minecraft:iron_ingot 2
  - ingredient: my_items:reusable_catalyst
    type: keep
```

- `damage`：保留被消耗的物品并扣除指定耐久，耐久耗尽时物品损坏。
- `replace`：返回 `item` 指定的物品，可用 `count` 设置数量。
- `keep`：原样保留被消耗的单件物品。
- 材料写法与配方材料一致，支持原版物品 ID、自定义物品 ID、`tag:` 和 `item_pack:`。
- 未配置规则的槽位继续使用原版剩余物；同一物品匹配多条规则时，第一条生效。

### 世界隔离

`enable_worlds` 用于限制配方只在指定世界中可用，未配置时该配方在所有世界都可用：

```yaml
# 限制在单个世界
enable_worlds: world

# 限制在多个世界
enable_worlds:
  - world
  - world_nether
```

- 世界名使用 Bukkit 的世界名，不是命名空间 ID。
- 世界不存在时会在加载时输出错误，无效的世界名会被忽略。
- 未配置 `enable_worlds` 的配方不受世界限制。
- 目前对铁砧、酿造与切石机配方生效。

## 文档

**[完整文档](https://yufiriamazenta.github.io/Craftorithm-Docs/)**

| 模块                                                                     | 说明                     |
|--------------------------------------------------------------------------|--------------------------|
| [快速开始](https://yufiriamazenta.github.io/Craftorithm-Docs/guide/)     | 安装、命令、权限         |
| [配方系统](https://yufiriamazenta.github.io/Craftorithm-Docs/recipe/)    | 多种配方类型详解         |
| [触发器系统](https://yufiriamazenta.github.io/Craftorithm-Docs/trigger/) | 事件监听与条件动作       |
| [脚本引擎](https://yufiriamazenta.github.io/Craftorithm-Docs/script/)    | 语法与内置函数           |
| [物品系统](https://yufiriamazenta.github.io/Craftorithm-Docs/item/)      | 自定义物品与外部插件集成 |
| [配置文件](https://yufiriamazenta.github.io/Craftorithm-Docs/config/)    | 配置项说明               |
| [UI 系统](https://yufiriamazenta.github.io/Craftorithm-Docs/ui/)         | GUI 与自定义菜单         |

## bStats

![bStats](https://bstats.org/signatures/bukkit/Craftorithm.svg)
