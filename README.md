# Alchemistry - Redox

Alchemistry is a tech mod, inspired by the classic [Minechem](https://www.curseforge.com/minecraft/mc-mods/minechem-archive), that allows you to decompose items into their constituent elements, then recombine them to create new items. This is a fork of the 1.12.2 branch of the [original mod](https://www.curseforge.com/minecraft/mc-mods/alchemistry) and aims to be the definitive 1.12.2 version of the mod. This is achieved by updating dependencies, fixing bugs, and improving the current features.

<a href="https://www.akliz.net/enderman"><img src="https://github.com/Ender-Development/PatchouliBooks/raw/master/banner.png" align="center"/></a>

## Current Changes

- switched build system to [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle)
- switched to the latest kotlin version utilizing [Forgelin-Continuous](https://www.curseforge.com/minecraft/mc-mods/forgelin-continuous)
- rewrote the config system to allow editing the config in-game
- added a config option that allows Fission and Fusion Reactors' multiblocks to share their respective casings
- fixed custom compounds and elements not being translatable
- backported reactor and machine textures from the modern version of the mod
- backported the interactive periodic table
- added fission and fusion glass to decorate the reactors
- reactor cores now emit a small amount of light
- reators now have their own upgrade system
- redid all GUI textures, while adding additional functionality to all machines
- removed AlchemyLib dependency

## Dependencies

![badge](https://img.shields.io/badge/required-Forgelin--Continuous-gray?style=flat-square&labelColor=red&link=https://www.curseforge.com/minecraft/mc-mods/forgelin-continuous)

Alchemistry is written in Kotlin, which requires the Forgelin library to be present to run.

![badge](https://img.shields.io/badge/optional-Patchouli-gray?style=flat-square&labelColor=green&link=https://www.curseforge.com/minecraft/mc-mods/patchouli-rofl-edition)

Alchemistry comes with a Patchouli book that explains the basics of the mod.

![badge](https://img.shields.io/badge/optional-HadEnoughItems-gray?style=flat-square&labelColor=green&link=https://www.curseforge.com/minecraft/mc-mods/had-enough-items)

All recipes are integrated into JEI.

![badge](https://img.shields.io/badge/optional-JSON_Paintings-gray?style=flat-square&labelColor=green&link=https://modrinth.com/mod/json-paintings)

Alchemistry adds a painting that displays the periodic table, given JSON Paintings is present.

![badge](https://img.shields.io/badge/optional-CraftTweaker-gray?style=flat-square&labelColor=green&link=https://www.curseforge.com/minecraft/mc-mods/crafttweaker)

Alchemistry provides CraftTweaker support for all recipes as well as adding custom elements and compounds.

![badge](https://img.shields.io/badge/optional-GroovyScript-gray?style=flat-square&labelColor=green&link=https://www.curseforge.com/minecraft/mc-mods/groovyscript)

The groovy-equivalent of CraftTweaker is also supported.

![badge](https://img.shields.io/badge/optional-Game_Stages-gray?style=flat-square&labelColor=green&link=https://www.curseforge.com/minecraft/mc-mods/game-stages)

The Chemical Combiner recipes can be locked behind Game Stages.

![badge](https://img.shields.io/badge/optional-The_One_Probe-gray?style=flat-square&labelColor=green&link=https://www.curseforge.com/minecraft/mc-mods/the-one-probe-community-edition)

Reactors and the Chemical Combiner display extra information in The One Probe.

## License

Switched license from [MIT](https://mit-license.org) to [GPL-2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.html), which allows me to use some assets from the modern version of the mod.

## [Ender-Development](https://github.com/Ender-Development)

Our Team currently includes:
- `_MasterEnderman_` - Project-Manager, Developer
- `Klebestreifen` - Developer

You can contact us on our [Discord](https://discord.gg/JF7x2vG).

## Contributing

Feel free to contribute to the project. We are always happy about pull requests.
If you want to help us, you can find potential tasks in the [issue tracker](https://github.com/Ender-Development/Alchemistry/issues).
Of course, you can also create new issues if you find a bug or have a suggestion for a new feature.
Should you have any questions, feel free to ask us on [Discord](https://discord.gg/JF7x2vG).

An enormous thank you to [rozbrajaczpoziomow](https://github.com/rozbrajaczpoziomow) for helping us with the project. Without you, this project would not be possible. <3

## Partnership with Akliz

> It's a pleasure to be partnered with Akliz. Besides being a fantastic server provider, which makes it incredibly easy to set up a server of your choice, they help me to push myself and the quality of my projects to the next level. Furthermore, you can click on the banner below to get a discount. :')

<a href="https://www.akliz.net/enderman"><img src="https://github.com/MasterEnderman/Zerblands-Remastered/raw/master/Akliz_Partner.png" align="center"/></a>

If you aren't located in the [US](https://www.akliz.net/enderman), Akliz now offers servers in:

- [Europe](https://www.akliz.net/enderman-eu)
- [Oceania](https://www.akliz.net/enderman-oce)
