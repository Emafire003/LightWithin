# Light Within Mod

## So... What does it do?

This mod adds a new mechanic, called *"InnerLights"* which are present within player from the start, and are pretty much **UNIQUE** for every
player.

Currently there only 3 types of innerlights, Heal Light, Defense Light and Strength Light. For now, when they trigger they add a powerful potion effect (beyond vanilla levels) when the player (or the target of the light) needs it the most. Aka, for now, when they are below 25/50% depending on the specif light.
But this is just a start, i'm going to make more complex lights with more complex triggers that may summon a raging storm at whover is attacking the player or his allies, or ice up the surroundings and such. I just don't have the time right now, and besides, this is just the alpha version!

### How your unique light is deteremined
The duration, power, the cooldown and other similar things are all based on the players UUID (which is unique for every player, kind of like their DNA in here) and they differ quite a bit from each other! Although the type can alter this to make it a bit better (in theory you could get a duration 1 "trait" but it wouldn't really makes sense so there is a minimum duration depending on the light type)

Of course the LightType and Target of the light are determined with the UUID as well. More on that later.

### How to trigger a light
Whenever you meet the trigger conditions for the activation of your inner light, a in icon like: ![icon](https://user-images.githubusercontent.com/29462910/171633058-da9bb4a7-64d0-4706-9c86-fe4eaa587860.PNG) is going to appear in the top-left corner of your screen, along with a sound effect. When you noitce this press the **V** button (default, you can change this in the keybinds settings) to activate it!

When you trigger it a bunch of particles arranged in a circle with 8 columns, and a flash representing your light being unleashed. Also, if you are in 1st person mode a writing with the colors of the effect will show up at the center of your vision (they need to be reworked a bit tho). I wonder what it says... and which language...


# SPOILERS AHED: 
### If you want to discover the features of this mod yourself (which is better in my opinion since it's intened to be something like "Oh no i'm gonna lose, but wait something within me is magicly helping me" like a main character of an anime) DO NOT LOOK AT THE LIGHT TYPES UNDER HERE. SKIP TO THE BERRIES IF YOU WANT!


### The light types:
#### Heal
If a player has this inner light when they or their allies get attacked and their HP drop below 25% (usually 3 hearts) (50% for allies) the light is going to trigger and start to heal you/your allies/others. It summons a reverse cascade of particles of heal light on the target of the effect, and it regenerates life based on the power and duration of your effect (which are different for each player as seen above).
There is a small chance of being a "other target" heal lightuser, which targets passive entities, animals and similar. The trigger is the same, when one of these creatures HP drop under 50% your light starts to shine inside you and you can activate it.

*Trivia: This was the first light that was developed!*

#### Defense
If a player has this inner light when they or their allies get attacked and their HP drop below 25% (usually 3 hearts) (50% for allies) the light is going to trigger and start to shield you/your allies/others from damages giving resistance. It summons a reverse cascade of particles of defense light on the target of the effect, and it protectes you based on the power and duration of your effect (which are different for each player as seen above).
There is a small chance of being a "other target" defense lightuser, which targets passive entities, animals and similar. The trigger is the same, when one of these creatures HP drop under 50% your light starts to shine inside you and you can activate it.

*Trivia: This was the first light to recive the particle/logo in development!*

#### Strength 
If a player has this inner light when they attack another entity and their (or their allies) HP is below 25% (usually 3 hearts) (50% for allies) the light is going to trigger and your/your allies/others attackes gets boosted. It summons a reverse cascade of particles of strength light on the target of the effect, and it strengths you based on the power and duration of your effect (which are different for each player as seen above).
There is a small chance of being a "other target" strength lightuser, which targets passive entities, animals and similar. The trigger is the same, when one of these creatures HP drop under 50% your light starts to shine inside you and you can activate it. But it's likely that this is going to get reworked.

*Trivia: This was the trickies to pull of for random reasons!*

### The target types (cuurently used)
#### Self
This is self explainatory. The target of the effect of your light is going to go towards you. For example, you will be the one getting the regeneretion when your HP drops below 25% with a heal light.

#### Allies
When your allies are in a certain condition (hp below 50% usually) your light is going to activate. Allies consist of minecraft team members and pets (if i recall correctly i will modify later if i haven't implemented this yet) and in the future other mods that add features like guilds, factions, parties and such will be included.

#### Others
Usually depends on the type of light, for now it represents the passive entities

## Setup

Download the jar of this mod from CurseForge or Modrinth along with Cardinal Components and chuck them into /mods/ folder.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
