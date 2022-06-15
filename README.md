# Light Within Mod

## So... What does it do?

This mod adds a new mechanic, called *"InnerLights"* which are present within player from the start, and are pretty much **UNIQUE** for every
player.

![demo_heal_brief](https://user-images.githubusercontent.com/29462910/171922554-e776af80-241a-4acc-a5f8-1d0b3f26211c.gif)


Currently there are only 3 types of innerlights, Heal Light, Defense Light and Strength Light. For now, when they trigger they add a powerful potion effect (beyond vanilla levels) when the player (or the target of the light) needs it the most. Aka, for now, when they are below 25/50% HP depending on the specif light.
But this is just a start, i'm going to make more complex lights with more complex triggers that may summon a raging storm at whover is attacking the player or his allies, or ice up the surroundings and such. I just don't have the time right now, and besides, this is just the alpha version!
There also are cool little jingles for pretty much everything you do/activate!

### How your unique light is deteremined
The duration, power, the cooldown and other similar things are all based on the players UUID (which is unique for every player, kind of like their DNA in here) and they differ quite a bit from each other! Although the type can alter this to make it a bit better (in theory you could get a duration 1 "trait" but it wouldn't really makes sense so there is a minimum duration depending on the light type)

Of course the LightType and Target of the light are determined with the UUID as well. More on that later.

### How to trigger a light
Whenever you meet the trigger conditions for the activation of your inner light, a in icon like: ![Senza titolo](https://user-images.githubusercontent.com/29462910/171919382-d65f6f72-4a84-44f9-9ebb-62283755793e.png) is going to appear in the top-left corner of your screen, along with a sound effect. When you noitce this press the **V** button (default, you can change this in the keybinds settings) to activate it!

When you trigger it a bunch of particles arranged in a circle with 8 columns, and a flash representing your light being unleashed. Also, if you are in 1st person mode a writing with the colors of the effect will show up at the center of your vision (they need to be reworked a bit tho). I wonder what it says... and which language...


# SPOILERS AHED: 
### If you want to discover the features of this mod yourself (which is better in my opinion since it's intened to be something like "Oh no i'm gonna lose, but wait something within me is magicly helping me" like a main character of an anime) DO NOT LOOK AT THE LIGHT TYPES UNDER HERE. SKIP TO THE ![BERRIES](https://github.com/Emafire003/LightWithin#the-lux-berries) IF YOU WANT!


## The light types:
### ![heal_light](https://user-images.githubusercontent.com/29462910/171918792-79693d99-249f-45b6-82cc-a2bb81facbea.png) Heal ![heal_light](https://user-images.githubusercontent.com/29462910/171918792-79693d99-249f-45b6-82cc-a2bb81facbea.png)
If a player has this inner light when they or their allies get attacked and their HP drop below 25% (usually 3 hearts) (50% for allies) the light is going to trigger and start to heal you/your allies/others. It summons a reverse cascade of particles of heal light on the target of the effect, and it regenerates life based on the power and duration of your effect (which are different for each player as seen above).
There is a small chance of being a "other target" heal lightuser, which targets passive entities, animals and similar. The trigger is the same, when one of these creatures HP drop under 50% your light starts to shine inside you and you can activate it.

[Demo video](https://youtu.be/n2U2m_OYyW8)

*Trivia: This was the first light that was developed!*

### ![defense_light](https://user-images.githubusercontent.com/29462910/171918951-fc2e9a01-0384-48a5-9472-b0f212053f4b.png) Defense ![defense_light](https://user-images.githubusercontent.com/29462910/171918951-fc2e9a01-0384-48a5-9472-b0f212053f4b.png)
If a player has this inner light when they or their allies get attacked and their HP drop below 25% (usually 3 hearts) (50% for allies) the light is going to trigger and start to shield you/your allies/others from damages giving resistance. It summons a reverse cascade of particles of defense light on the target of the effect, and it protectes you based on the power and duration of your effect (which are different for each player as seen above).
There is a small chance of being a "other target" defense lightuser, which targets passive entities, animals and similar. The trigger is the same, when one of these creatures HP drop under 50% your light starts to shine inside you and you can activate it.

[Demo video](https://youtu.be/vaMZ8sPw9bY)

*Trivia: This was the first light to recive the particle/logo in development!*

### ![strength_light](https://user-images.githubusercontent.com/29462910/171918991-6c40ce1f-9aca-4a2c-ba3f-1c7b8ace63c7.png) Strength ![strength_light](https://user-images.githubusercontent.com/29462910/171918991-6c40ce1f-9aca-4a2c-ba3f-1c7b8ace63c7.png)
If a player has this inner light when they attack another entity and their (or their allies) HP is below 25% (usually 3 hearts) (50% for allies) the light is going to trigger and your/your allies/others attackes gets boosted. It summons a reverse cascade of particles of strength light on the target of the effect, and it strengths you based on the power and duration of your effect (which are different for each player as seen above).
There is a small chance of being a "other target" strength lightuser, which targets passive entities, animals and similar. The trigger is the same, when one of these creatures HP drop under 50% your light starts to shine inside you and you can activate it. But it's likely that this is going to get reworked.

[Demo video](https://youtu.be/fR4cRF6opM8)

*Trivia: This was the trickies to pull of for no-sense reasons!*

## The target types (currently used, there are more)
### Self
This is self explainatory. The target of the effect of your light is going to go towards you. For example, you will be the one getting the regeneretion when your HP drops below 25% with a heal light.

### Allies
When your allies are in a certain condition (hp below 50% usually) your light is going to activate. Allies consist of minecraft team members and pets (if i recall correctly i will modify later if i haven't implemented this yet) and in the future other mods that add features like guilds, factions, parties and such will be included.

### Others
Usually depends on the type of light, for now it represents the passive entities

## The "Lux" Berries
They currently can't be obtained in survival, and when they will there are going to range in rarity from the golden apple to beyond the enchanted one.

![berries](https://user-images.githubusercontent.com/29462910/171917861-fd2f614a-b691-4a2b-b596-eabe64e2ff65.gif)

*From left to right, Luxmuta Berry, Luxintus Berry, Luxcognita Berry*

### Luxmutua Berry ![luxmut_berry](https://user-images.githubusercontent.com/29462910/171920401-c0134016-6a9f-453d-8f4b-2afad5165b17.png)

This one, will mix up your innerlight, allowing you to change it innerlight. The change is random, and it won't be the same one you had before eating it. It also changed the target randomly. This is probably going to be even more rare that an enchanted golden apple

### Luxintus Berry ![sweet_berries](https://user-images.githubusercontent.com/29462910/171920163-2cdb1586-ded6-47ba-97ea-0d259be72187.png)

This magical berry will refill your inner light, and trigger it right away. It will be as rare as an enchanted golden apple
*Trivia: First berry to have developed, and the only one originally planned*

### Luxcognita Berry ![luxcogni](https://user-images.githubusercontent.com/29462910/171920418-17c52b5a-0fa5-497b-94a8-7e5d3ea7c3c4.png) 

This berry will read your innerlight, and tell you how it's made up, aka what does it do, before activating it. It will be useful once the mod is out of alpha... eheh. Anyway, it will be as rare as a normal golden apple. Maybe you could find it in the same places too.

[![bisecthosting](https://www.bisecthosting.com/partners/custom-banners/e9c85d2a-cafa-4e2f-98bf-4f62bd9e951c.png)](https://www.bisecthosting.com/LightDev)

## Coming (soon tm) Features
- QoL changes
- Way more light types such as Thunder, Shockawave, Elemental lights and such 
- More complex targets
- More complex triggers
- Legendary lights with an heavy lore behind them (For now i can say, "black white and ???"
- Fix bugs
- Make the effects better
- Modify the writings that appear on screen when triggering
- Suggestions accepted but likely to be low in the roadmap

## Setup

Download the jar of this mod from CurseForge or Modrinth along with Cardinal Components and chuck them into the /mods/ folder!

## License

This mod is available under the GNU GPL3 License.

## Support me
If you would like to offer me a coffee, here you go.

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/S6S88307C)

For modpack devs: You are permitted to use this mod without directly asking, but please credit me somewhere, it would help! (Also, I'm kind of a curious person so maybe send me a message when you include it into your modpack, and I'd like to check it out)

Thanks to @FranzleOrange for the help with the Strength soundtrack
