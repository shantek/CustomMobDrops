[![License: GPL](https://img.shields.io/badge/license-GPL-blue.svg)](LICENSE)
[![Discord](https://img.shields.io/discord/628396916639793152.svg?color=%237289da&label=discord)](https://shantek.co/discord)
[![CodeFactor](https://www.codefactor.io/repository/github/shantek/custommobdrops/badge)](https://www.codefactor.io/repository/github/shantek/custommobdrops)

# 🧟 Custom Mob Drops

**Create and manage custom mob drops with ease.**  
This plugin lets you fully customize what mobs drop on death, allowing for server-specific loot tables, rare items, or even custom event drops.

> ⚙️ Fully configurable via `config.yml`  
> 🔄 Reloadable without restarting the server  
> ✅ Compatible with Bukkit, Spigot, Paper, and Purpur

---

## 🔧 Features

- Easily override vanilla mob drops
- Enable or disable custom drops globally
- View a full list of mobs configured
- Supports any item, enchantments, NBT, and drop rates
- Built with performance and flexibility in mind

---

## ⚙️ Commands

| Command | Description |
|---------|-------------|
| `/drops` | Alias for all plugin commands |
| `/custommobdrops enable` | Enable custom mob drops globally |
| `/custommobdrops disable` | Disable custom drops (revert to vanilla behavior) |
| `/custommobdrops list` | List all configured mobs with custom drops |
| `/custommobdrops reload` | Reloads the plugin config without restarting |

---

## 🔐 Permissions

| Node | Description |
|------|-------------|
| `shantek.custommobdrops.enable` | Allows toggling the plugin on/off |
| `shantek.custommobdrops.reload` | Allows reloading the plugin config |

---

## 🧾 Setup Instructions

1. Download the plugin and place it in your server’s `/plugins` directory.
2. Restart or reload your server.
3. Edit the `config.yml` to define custom drops.
4. Use `/custommobdrops reload` to apply changes live.

> The config supports drop rates, item stacks, custom names, lore, and more!

---

## 🌐 External Links

- 💬 [Join the Discord](https://shantek.co/discord)
- 🐞 [Report Issues / Suggestions](https://github.com/shantek/CustomMobDrops/issues)
- ❤️ [Support via Patreon](https://shantek.co/patreon)

---

## 📄 License

Distributed under the **GNU General Public License v3.0**.  
See [`LICENSE`](LICENSE) for full license details.

---

![Plugin Stats](https://bstats.org/signatures/bukkit/Custom%20Mob%20Drops.svg)
