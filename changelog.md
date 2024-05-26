### Minecraft 1.19.2 specific changes:
- Some mods like argonauts and Flan aren't supported anymore, since they don't exist for this version of minecraft
- Some features like the position of the buttons on the screen may differ slightly from the 1.20.1+ version, since some code is missing
- In general, some rendering stuff changed. But everything should work correctly regardless.
- Explosions may differ slightly

### Minecraft 1.20.5/.6 specific changes:
- Old BottledLight items are still compatible even if they use nbt instead of components
- Some mods that are compatible with LightWithin won't work yet since they haven't updated yet
- If you find any bugs related in particular to networking and items doing weird stuff
- There is a known bug of consuming the BottledLight anyway even if in creative. And a similar one regarding the bottling up of a ready light

Welcome to release 1.1.0 which had 43 commits, 155 changed files with 5,633 additions and 1,881 deletions! (yay, and it's not even the port to 1.20.5 yet)

### LightCharges
This is the primary feature added by this release. LightCharges will allow you to activate your light whenever you want! Sort of.

- You will be able to get and use LightCharges after you have triggred your light normally at least once. 
- Every player has maximum number of charges that they can store at a given time, and it ranges from 0-8 (or 1-7, see the config). 
- You can use a charge by pressing the same button you use to activate it, by default it's **V**. Using a charge will set you in a "light-ready" status, press the activation button again to activate your light. You can't automatically recover a charge you have used, so make sure to activate your light as well! Or, harvest it with a Empty bottle to get the BottledLight back.
- An icon will also appear showing how many light charges you currently have. Also, the more light chargese you have stored, the more you will glow. When you have max light charges you will also emit some light particles. The icon won't show up unless you have triggered your light naturally at least once. By default it's in the same position as the activate icon, top left corner!
- Using a light charge is more costly for your light, and will increase the cooldown time by quite a lot!
- Getting a charge is a complicated and somewhat expensive process. You will need to get a BottledLight, an item that stores InnerLight energy in a bottle. You can use this item with a right click, and after the bottle breaks you will have obtained one light charge. You will also be put in a temporary cooldown.
- To get the BottledLight there are two ways: The first and simpler one is using an empty bottle when your light is ready. This will make a BottledLight store your own InnerLight, and only you will be able to use it. The other way is the alchemist way, you will be able to brew some of it by using an Experience Bottle and crushed luxintus berry, which you can obtain by smashing an anvil on top a luxintus berry. Be warned! May explode!. After that, you will need to insert the ingredients associated to the desidere light type and target. Every player with the same type and target will be able to use these. But if you have a different light type, please refrain from trying to use it!
- Cool rendering effects, like particles and rays are displayed when you use a light charge, when you increase your number of charges, when you harvest it etc. Also some new sounds will play.

### Luxcognita Rework
The luxcognita berry has been reworked, now when you eat it a new screen will open, with the berry asking you what you want to know. You will be able to chose learn about 4 things: Your InnerLight type, target and their corresponding ingredients. An animation and a jingle will play when you press the button. Be warned! You are "actively thinking-speaking with the luxintus berry" aka the game isn't paused, so you are vulnerable to mob attacks!

In the future this may be the start of somekind of in-game tutorial of the mod, so tell me if you like it and if you'd change anything.

### Client Config
A client config file has been added. This allows you to customize the graphical settings for the mod, and this one comes wiht a GUI! To open the config menu you will need to install YACL and ModMenu. The client config adds options to adjust the position of the icons, their scale, the presence of the runes on screen and more. Check it out in game or look at the respective section on the wiki!

### Config version 4
A few new settings were added, mostly concerning the newly added stuff. You will also be able to specify if and who can activate an InnerLight in a faction's territory, if the Factions mod is installed.

### Commands
- A new command sub type has benn added, `/light charge <add|remove|fill|empty> [amount]` to manage the light charges. Also, the `/light get all` command now displays information on what the inner lights of a player would originally have been. For example, if they used a luxmutua berry it will show what their type should be according to their UUID. The permission to use it, as usual, is `lightwithin.commands.charge`.
- Added options to mange the *hasTriggeredNaturally* status and *max_charges* to `/light set & get` commands.
- Also, a mostly debug command to reload the client config has been added, `/light_client reload`. 

### Misc
Added a trigger for a player causing an explosion, it will be treated as Enity Attack on another one.
Added new particles! These will spawn when using or obtaining light charges mostly. Also added a system to display render effects, and quite a bit of this sort of things under the hood, like two new libraries written by my are included in this version.
Added new sound effects!
Fixed a few issues
Updated dependencies, mainly compatibility with flan 1.10+ has been added. 
Optimized some of the code
Possibily something else i've forgot.


