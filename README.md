![lightwithin_title](https://github.com/Emafire003/LightWithin/assets/29462910/d2898053-0a12-41dc-87aa-dcae6d05d87b)
This mod adds a new mechanic, called *"InnerLights"* which are cool fancy magicalish powers present within the player, and they are pretty much **UNIQUE** for every single
one of them! And you can't just spam them, they will only help you if your are in danger! It is also highly configurable, and multiplayer friendly!


There are lots of lights, ranging from ones that grant powerful status effects like healing, to freezing all of your enemies on the spot, or even save your allies from falling from great heights by flying away! You can even summon creatures to defend yourself against dangers!
Each light has a cool sound design and distinct effects, with particles, custom glowing effects, and runes appearing on your screen!

![banner_lighter](https://github.com/Emafire003/LightWithin/assets/29462910/95d1a6a7-e89a-4956-a148-b0bc215387e0)

And all of this is different from player to player, so everyone will have their personal secret power!

Check the wiki for info about all of these lights. Or you could preserve the surprise and just play with the mod and find out yourself!
<p align="center">
  <img src="https://user-images.githubusercontent.com/29462910/171922554-e776af80-241a-4acc-a5f8-1d0b3f26211c.gif" />
</p>

You can also look at the [gallery](https://modrinth.com/mod/lightwithin/gallery)!

## How is it unique?

The lights are uniquely determined by the player's UUID, a set of bits that are generated by the game and assigned to every entity to uniquly identify it, a bit like fingerprints or DNA.

There a few things that contribute to the uniqueness of a light, called attributes: 
- First and foremost, it's **TYPE**, which could be HEAL, FROST, or BLAZING or whatever. This corresponds to which category/type your light is from. It's the main "ingredient"
- Then, there is the **TARGET**. This specifies what your light targets, aka who will be affected by the triggering of the light. It could be ENEMIES, ALLIES, SELF etc
- Starting with some numerical values we have the **POWER** of your light, which determines how strong your light is. It is a value used in calculating a Status Effect level, the damage done when activating the light and so on. It ranges from 1 to 10.
- We have the **DURATION**, which, unsurpisingly specifies how much your light remains active
- Lastly we have the **COOLDOWN** duration, which is how much time will need to pass between activating your light again! Your light will need some rest after spending all that energy no?

The best way to enjoy this mod is with other people on a server, so you should download something like e4mc or get a server!

## How can I activate them?
In order to activate them you need to meet specific conditions, which often vary between light types. Usually, it involves fighting with another mob/enemy player and being "in danger", for example on low health, or being surrounded by many enemies. To be more precise, usally you need to meet more than one condition in order to trigger the activation. In my opinion it's better to just discover how to activate your light by playing, so I won't go in further detail here, look at the wiki instead.

Once you have met these conditions, an icon ![light](https://github.com/Emafire003/LightWithin/assets/29462910/4453adb1-5c40-4fa6-8a96-94ad8f0d2579) will appear in the top left corner signaling that your light is ready to be released. Press the keybind **V** (default, you can change it) and activate your light!

There is also a magical item called **Luxintus Berry** which lets you activate the light regardless of the conditions. It's kind of an energy drink for your inner light! Another 2 "Lux" berry are present, allowing you to change your light type and knowing which light type you have without needing to trigger it or use commands. (Check the wiki for more info!)

Starting from version 1.1.0, you can also use LightCharges! Read more about them in the wiki!

## Compatibility and Hooks with other mods
This mod is compatible with [ColoredGlowLib](https://modrinth.com/mod/coloredglowlib) to add a custom glow color while the light is active.
For the Allies or Enemies you can use Faction, OPAC, Argonauts, FTBTeams and Minecraft's teams.
You can also use Flan and YetAnotherWorldProtector to enable/disable light activation in certain areas.

There are no currently know incompatiblities with other mods. Let me know if you find any!

## Setup
Download the jar file of this mod from CurseForge or Modrinth put into the /mods/ folder! (You also need to download Fabric API) 

## License
This mod is available under the GNU LGPL3 License.

## Support me
The best way to do that, is sharing the mod with as many people as you know, feel free to make video tutorials/gameplayes and such! It would really help!

You can directly support me by offering a coffe at this link:
[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/S6S88307C)

Another thing you could is make videos and posts about the mod!

## Modpack use
You are allowed to use this mod without directly asking me, but you are asked to credit me, as for most other mods. Also, I would appreciate if your modpack was hosted on Modrinth or Curseforge, which would actually increase the download count of the mod, something that other modpacks sites don't do.

### Attributions:
Text logo: textstudio.com

Some audio files: https://mixkit.co, https://pixabay.com (freesound community)

Other audio files: https://www.beepbox.co/

Some of the code for the commands: Factions mod (MIT license)
Some of the code for the rendering: [Renderer mod](https://github.com/0x3C50/Renderer) (GNU GPL3 license)

## Coming (soon tm) Features (maybe)
- More light types!
- Maybe more targets, not sure!
- More triggers maybe
- Legendary lights with an heavy lore behind them (For now i can say, "black white and ???"
- Fix bugs
- A proper API sort of
- A way to make the adjust the power & duration & cooldown with game items
- Suggestions accepted. But not guaranteed.

