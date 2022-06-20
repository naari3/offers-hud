# OffersHUD

OffersHUD is a client-side mod which display a list of villager's trading offers as a HUD, without a right-click.

![show offers](./docs/2022-01-10_08.00.13.png)

## Installation

### Download

The latest releases of OffersHUD are published to these services.

- [Modrinth](https://modrinth.com/mod/offershud)
- [GitHub Release](https://github.com/naari3/offers-hud/releases)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/offershud)

### Required other mods

Install the latest version for suitable Minecraft versions.

- [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
- [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu)
  - This is optional but recommended for configuration features.

## Features

### Main

When you focus on a villager with profession (or wandering trader), the offers will be displayed in the upper left corner of the screen.

This can be very useful, for example, for carefully selecting the details of a villager's trading offers.
You don't have to right-click on the villager every time you reposition the job site block.

This uses `InGameHud#renderStatusEffectOverlay` and will not work correctly if there is a mod that cancels this method.

### Configurations (required modmenu)

You will need to add **modmenu** for the configurations.

#### Enabled

- Toggle enabled / disabled
- default: **true**

#### Ignore villagers who do not have a profession

- Do not react to focus on villagers without profession.
- If false, try to interact with villagers who do not have a profession as well.
  - Mainly for special villagers on spigot servers, etc.
- default: **true**

#### Suppress villager head rolling

- Villagers will no longer do head rolling when interacting, even if no profession villagers.
  - Mainly for `Ignore villagers who do not have a profession: false`
- default: **false**

## Special Thanks

- @javascriptjp has done a great job with version 1.19!
