Hi! It's been a while (again)! Update 1.2.0 brings you (mainly)...
## Aura lights!
A new category of lights, beside the effect-based ones (well technically almost all of them use effects) and the "elemental ones".
These first ones added are ForestAura and ThunderAura, and you have a roughly 6% chance of getting them. (They are still based on you uuid so it's not chance per se, but more of the "chance you have of having a compatible uuid" with it)

### ForestAura
This light is linked to the essence of the forest, be it the soil or the plants. Whenever you activate it, you will be communing with the forest and thus you will turn green! (photosynthesis ehy?)
It has two target types:
- **SELF**: Once activate, your matter and the matter of the forest will become one thing, and you will be able to pass through forest blocks (defined in a dedicated tag), like dirt logs and leaves. 
It works similarly to powder snow. Be careful tho! You are not a mole and you won't be able to see inside blocks, but you will be able to see other entities through a glow effect.
Based on the power level of your light, you will also be able to tell apart friend from foe!
- **ALL**: This variant will be harnessing the power of the secondary metabolites of forest plants: it will spawn a few different "puffs" of various substances around the caster, and they will affect every other entity around them,
except for other fellow forest-aura light-bringers. The puffs will apply different potion effects, like blindness slowness, poison etc, their strength type and duration depeding on the light's duration and power level. A new potion effect has been added,
Intoxication, that will randomise the movement controls, and make the player see... well peculiar stuff. (configurable in the config for accessibility). It will also make them jump around!

You are immune to suffocation damage while you have your light active, but be aware of its timer!

To trigger this light, you (generally) will need to be: low health, in a forest biome, be surrounded by leaves or hold a sapling.
The relevant checks are performed when: you get attacked, you attack, an ally dies. More info on the wiki.

### ThunderAura
This one comes with the power of bellowing thunder and the strength of its lightnings. Whenever you activated, you will become a bit yellowier than before. And statically charged probably too.
It has three target types:
- **ALLIES**: Upon activation, you and your allies will be shielded by the thunder, with a globe of electrical particles floating around you. This forcefield will
always be centered around you or you ally, and if any entity comes into contact with it will get shocked and bounced back where it came! (it is a very satisfying and fun thing to do trust me). 
A "zap" sound will also be played, and particles displayed. The strength of the repulsion and the damage are scaled on the power level. The size of the forcefield is scaled by the size of the entity.
You and your allies will also be immune to lightning damage while the effect is active
- **ALL**: You will be able to use the power of the storm to strike wherever you like with a thunder bolt! Just point, right click, and... BAM HERE COMES LIGHTNING!!! *(mcqueen?)*. You can do so even if you don't have a free hand.
The number of lightnings spawned (by default, but it's configurable), is determined by the light's power level. The max distance is several blocks away (30ish iirc).
- **VARIANT**: This is the most peculiar target type, and maybe the most close to thunder itself. Once you activate, you will call a storm where you stand.
But it will be supercharged with A LOT of extra lightnings. They will randomly spawn in (configurable) area around you, and they will be spawning both on ground and in the air.
The amount of lightnings per second is based on you power level, as well as the area of this effect. 

You are always protected from lightning damage while your light is active as well.
To trigger this light, you (generally) will need to: be on low health (or your allies need to), surrounded by your allies, have a copper rod, stand on a copper rod, thundery or rainy weather.
The checks are performed when an ally is getting attacked or struck by lightning, you are getting attacked or struck by lightning. Check the wiki for more info.

Get more info in the wiki, or just try it out! It of course comes with its icon, particle effect and sounds!

### WindLight ALLIES update
Added a new effect, called "Wind Walking", which will allow you and your allies to jump and walk on air blocks! Much like scaffolding and the ForestAura effect described above.

### New wiki!
The wiki has been updated and moved to [ModdedMc](https://moddedmc.wiki/en/project/lightwithin/)! The plan is to also allow you to use mods like Oracle Index to view the wiki in game, while waiting for ingame tutorials done by this mod itself.

### Trigger items and blocks are now tags
The list of blocks and item that acted as triggers for some of the lights have now been moved away from the config files and into new tags, located in
`..data/lightwithin/tags/` and you will be able to modify them via a datapack. 

### New trigger options and checks
A new check has been added, _surrounded by allies_, which checks how many allies are you close to. It is used in the thunder aura light currently.
There are also checks for the weather, and the surrounding leaves. There are also triggers for when you get zapped by lightning.

### Config Files
- Updated the main Config to version 5, added the "surrounded_by_allies" option, similar to surrounded by instead of enemies it uses allies.
- BalanceConfig to version 2, added the new settings for forest and thunder aura. You will also be able to set the puff action range, their max and min distance relative to the player. 
You can also change the number of lightnings per level and the size of the superstorm.
- TriggerConfig updated to version 3, added the settings for Forest and Thunder Aura
- ClientConfig to verso 2, added the options to scale the runes displayed on activation as well as the option to choose the colors for ForsetAura's vision of allies enemies and neutrals.

### Under the hood changes
Updated to Loom 1.9, updated to the newer gradle version, updated to ParticleAnimationLib 0.1.0. Updated to new YAWP version (1.21.1)

### Misc & Bug fixes
- Added a few new particles, the lightnings ones and puff particle ones.
- Added the relevant BottledLights to the 2 new lights
- Tweaked how the light types particle look, they now go a bit more horizontally before floating up. They also are a little less randomly coloured.
- The area of search for entities (the one that checks for allies and enemies and does stuff) has been double to 12 blocks (from 6) by default
- Added sound subtitles translations
- Fixed how the sounds are played 
- Fixed [#23](https://github.com/Emafire003/LightWithin/issues/23)
- Fixed [#17](https://github.com/Emafire003/LightWithin/issues/17)
- Fixed the runes rendering off-center
- Fixed a bug where the ClientConfig loaded wrong defaults on startup
- Fixed a bug that didn't allow changing the size of the area of search for entities
- Fixed FrostLight particles non spawning
- Fixed HealLight sound being played too low
- Fixed the `/light ready|activate delay` timers, they now work properly [#26](https://github.com/Emafire003/LightWithin/issues/27)


