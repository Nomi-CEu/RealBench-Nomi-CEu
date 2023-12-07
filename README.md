<p align="center"><img src="https://github.com/Nomi-CEu/RealBench-Nomi-CEu/assets/103940576/b1abfcfd-a591-4787-960b-4aa081f40fac" alt="Logo"></p>
<h1 align="center">RealBench: Nomi-CEu-Edition</h1>
<p align="center"><b><i>Rewrite of <a href="https://www.curseforge.com/minecraft/mc-mods/realbench"> RealBench</a>, fixing many bugs and glitches!</i></b></p>
<h1 align="center">
    <a href="https://github.com/Nomi-CEu/RealBench-Nomi-CEu/issues"><img src="https://img.shields.io/github/issues/Nomi-CEu/RealBench-Nomi-CEu?style=for-the-badge&color=orange" alt="Issues"></a>
    <a href="https://github.com/Nomi-CEu/RealBench-Nomi-CEu/blob/main/LICENSE"><img src="https://img.shields.io/github/license/Nomi-CEu/RealBench-Nomi-CEu?style=for-the-badge" alt="License"></a>
    <a href="https://discord.com/invite/zwQzqP8b6q"><img src="https://img.shields.io/discord/927050775073534012?color=5464ec&label=Discord&style=for-the-badge" alt="Discord"></a>
    <br>
<a href="https://www.curseforge.com/minecraft/mc-mods/realbench-nomi-ceu"><img src="https://cf.way2muchnoise.eu/945503.svg?badge_style=for_the_badge" alt="CurseForge"></a>
    <a href="https://www.curseforge.com/minecraft/mc-mods/realbench-nomi-ceu"><img src="https://cf.way2muchnoise.eu/versions/For%20MC_945503_all.svg?badge_style=for_the_badge" alt="MC Versions"></a>
    <a href="https://github.com/Nomi-CEu/RealBench-Nomi-CEu/releases"><img src="https://img.shields.io/github/downloads/Nomi-CEu/RealBench-Nomi-CEu/total?sort=semver&logo=github&label=&style=for-the-badge&color=2d2d2d&labelColor=545454&logoColor=FFFFFF" alt="GitHub"></a>
</h1>

## Dependencies:
- Requires [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixin-booter).

## Features:
- All features of RealBench, including:
  - Items stay in the Crafting Table
  - Items in the Crafting Table are rendered on top
  - Other players can collaborate on crafts and see the progress
  - Items dropped when Crafting Table is broken
- RealBench re-built from the ground up, with:
  - Mixins instead of ASM, allowing for better compatability and easier readability
  - Focus on performance and mod compatability
  - RFG Buildscripts
  - A GitHub Repo that isn't dead!
- Calculates the light on the render based on the block above, instead of having to make the Crafting Table a transparent block
- Fixes a server dupe glitch, as well as more edge case server bugs and glitches
- Fixed the issue where the recipe result would not show up when entering crafting table
- Compatability with [FastWorkbench](https://www.curseforge.com/minecraft/mc-mods/fastworkbench), works well with [Universal Tweaks](https://www.curseforge.com/minecraft/mc-mods/universal-tweaks)
- Items in the Crafting Table in worlds with RealBench will transfer correctly
- Tested heavily in standalone environments, environments with FastBench or Universal Tweaks, and in modpack environments ([Nomi-CEu](https://www.curseforge.com/minecraft/modpacks/nomi-ceu) and [Nomifactory](https://www.curseforge.com/minecraft/modpacks/nomifactory))

## Internal Workings:
### Light Calculation
The light calculation in order to render items based on the correct lighting environment is from [GregTech CEu](https://www.curseforge.com/minecraft/mc-mods/gregtech-ce-unofficial).

### Syncing Result
This mod very carefully synchronizes the result between different clients with the same Crafting Table open.

The Tile Entity stores a list of the items in the crafting matrix. Each player container of the workbench stores the current matrix for them, which is what the result is based on. 
Each tick, if the tile has been marked dirty, each container is checked to see if the matrix has changed for them, and if it has, the recipe result is updated. 
Whenever a player exits the crafting bench, that container is removed from the list. (For some reason, MC creates a new container each time the player enters the inventory)
This has very high performance, especially with Fast Workbench or Universal Tweaks' Crafting Cache Feature.

The result is also calculated each time the player enters the Crafting Table.

When a craft occurs, the Tile Entity tells each container to clear the result slot.

## Credits:
- [GTCEu Buildscripts](https://github.com/GregTechCEu/Buildscripts) for the buildscripts
- [RealBench](https://www.curseforge.com/minecraft/mc-mods/realbench) the original idea, the basis for this mod, and the code for the rendering engine
- [GregTech CEu](https://github.com/GregTechCEu/GregTech) for light calculation code
