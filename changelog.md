Hello! First major version bump uh?
# Luxcognita Overhaul - An in-game guide!
Now there are proper guides and explanations in the mod itself! Woohoo! Only took, two/three years?
Eating a **Luxcognita** berry will now open up a custom "Luxdialogue" screen, where you will be able to ask the
"light of knowledge" herself *(kind of)* everything that you need to know. You can also chat a bit about random things, but
be careful, she gets upset fairly easily!

Mainly, you will interact using buttons displayed in the bottom half of the screen, while in the top half there will be 
the main text, which is what Luxcognita is saying. There will also be some images and "scenes" that will help you understand
what she's talking about a bit better. 

I could list all the dialogue options and things, but it's way too much stuff and you can just open up the game and eat
a berry to see it for yourself. 

Some features of note: 
- You will be invulnerable (not unmovable!) as long as you see the luxdialogue screen, but as soon when you get attacked, you'll get a warning and the screen will automatically close.
- While talking, you will also appear translucent to other players! (who knows where 'you' *really* are uh?)
- Some dialogues will only be unlocked after others, for example, you won't reach the main dialogue unless you have completed the intro dialogue
- The dialogues are formatted as json files, and in theory you can add your own using a resource pack. Let me know if you would like a guide on how to do that, or if you have suggestions for more dialogues, please add an issue (or a pull request!) on github!
- A background music will also play in the background

## Backend/Code-wise updates
The code that manages the light types (the things that give you the powers) has been migrated to a registry,
(hence the major version bump, which is a very breaking thing), which should make adding more light types considerably 
easier, potentially allowing a future API to add more lights using addons (not currently planned, but still).
This was already present in the alpha 1.4.0 alpha release of the mod. 

## Changes and additions
### Commands
- Added the /light spoof command, that will allow you to copy another player's light attributes, using their UUID
- Added /light set dialogueProgress add/remove to manage the dialogue progress states (the "gates" that prevent a player from seeing certain screens and stuff)
### Config files
#### Main config
- Added config option to set custom maximum power level settable using commands (won't affect the natural maximum power level cap)
- Added config option to give new players a Luxcognita Berry as soon as they join for the first time (as a sort of introduction tutorial)
#### Client config
- Added a new config tab related to the Luxdialogue options
- TODO
### Misc
- Added the Equipment Break trigger to EarthenLight
- Fixed luxcognita texture
- Added Luxcognita Dream effect and Luxcognita Offended effect
- Updated the tooltips of the lux berries
- Added snowing check to FrostLight's conditions
- Added a new music disc 'Luxcognita Dream', which is the background music played inside the screen. Currently only obtainable in creative mode.
### Mod Compatibility
- Added compatibility for allies enemies and such with "Guilded"
- Sodium could sometimes cause some issues with the display of the transition animation and the text blink speed. You can account for such issues using the options in the client config. 