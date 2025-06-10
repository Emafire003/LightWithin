package me.emafire003.dev.lightwithin.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.ActivationKey;
import me.emafire003.dev.lightwithin.client.LightRenderLayer;
import me.emafire003.dev.lightwithin.client.LightWithinClient;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.ClickActions;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.DialogueProgressState;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.LuxDialogue;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.Replaceables;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.NoneLight;
import me.emafire003.dev.lightwithin.networking.DialogueProgressUpdatePacketC2S;
import me.emafire003.dev.lightwithin.networking.LuxDialogueActions;
import me.emafire003.dev.lightwithin.networking.LuxdreamClientPacketC2S;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.ScreenUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import me.x150.renderer.ClipStack;
import me.x150.renderer.Rectangle;
import me.x150.renderer.Renderer2d;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public class LuxcognitaScreenV2 extends Screen{
    protected long loadStartTime;
    private final LuxDialogue dialogue;

    @Override
    public String toString() {
        return "LuxcognitaScreenV2{" +
                "dialogue=" + dialogue +
                ", title=" + title +
                '}';
    }

    public LuxcognitaScreenV2(Text title, LuxDialogue dialogue) {
        super(title);
        this.dialogue = dialogue;
    }


    @Override
    public void init(){
        this.loadStartTime = System.currentTimeMillis();

        if(this.client == null || this.client.player == null){
            LightWithin.LOGGER.error("ERROR! The ClientPlayer is null! (or the client itself, somehow)");
            return;
        }

        // Check to see if this is a dialogue that can be redirected if the player has a certain dialogue progress
        // for example, "intro" will redirect to "main" once its completed.
        if(dialogue.canRedirect){
            if(LightWithin.LIGHT_COMPONENT.get(this.client.player).getDialogueProgressStates().contains(dialogue.redirectStateRequired) && !dialogue.invertRedirectRequirement){
                if(!LuxdialogueScreens.LUXDIALOGUE_SCREENS.containsKey(dialogue.redirectTo)){
                    if(dialogue.redirectTo.equals("CLOSE")){
                        this.closeWithAnimation();
                        sendDialogueStopDreamPacket();
                        return;
                    }
                    this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Error! Cannot redirect to screen '" + dialogue.redirectTo + "' since it's not registered!").formatted(Formatting.RED));
                    return;
                }
                this.client.setScreen(LuxdialogueScreens.LUXDIALOGUE_SCREENS.get(dialogue.redirectTo));
                return;
            }
            if(!LightWithin.LIGHT_COMPONENT.get(this.client.player).getDialogueProgressStates().contains(dialogue.redirectStateRequired) && dialogue.invertRedirectRequirement){
                if(!LuxdialogueScreens.LUXDIALOGUE_SCREENS.containsKey(dialogue.redirectTo)){
                    if(dialogue.redirectTo.equals("CLOSE")){
                        this.closeWithAnimation();
                        sendDialogueStopDreamPacket();
                        return;
                    }
                    this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Error! Cannot redirect to screen '" + dialogue.redirectTo + "' since it's not registered!").formatted(Formatting.RED));
                    return;
                }
                this.client.setScreen(LuxdialogueScreens.LUXDIALOGUE_SCREENS.get(dialogue.redirectTo));
                return;
            }
        }

        // If this screen produces a dialogue progress state, update it
        if(dialogue.dialogueProgress){
            if(dialogue.removeDialogueProgress || !LightWithin.LIGHT_COMPONENT.get(this.client.player).getDialogueProgressStates().contains(dialogue.dialogueProgressState)){
                sendDialogueStateUpdatePacket(dialogue.dialogueProgressState, dialogue.removeDialogueProgress);
            }

        }

        // Sets up the grid for the screen
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().margin(4, 10, 4, 0);
        gridWidget.setSpacing(10);
        int maxButtonsPerRow = 4;
        // adjusts the number of columns based on the amount of buttons
        int buttonAmount = dialogue.buttons.size();
        if(buttonAmount > 4 && buttonAmount%2==1 ){
            maxButtonsPerRow = 3;
        }
        GridWidget.Adder adder = gridWidget.createAdder(Math.min(buttonAmount, maxButtonsPerRow));


        // Gets the buttons for this screen and the action
        List<Widget> buttons = getButtons();

        AtomicInteger buttonCount = new AtomicInteger(1);
        AtomicInteger emptyWidgetsAdded = new AtomicInteger();
        if(buttons.isEmpty()){
            LightWithin.LOGGER.warn("The button list for dialogueId '" + dialogue.dialogueId + "' is empty!");
        }else{
            buttons.forEach(buttonWidget -> {

                if(buttonCount.get() > 3 && buttonAmount > 4){
                    if(buttonAmount == 5){
                        // Button | Button | Button
                        // button | button (can't do better)
                    /*if(emptyWidgetsAdded.get() == 0 && buttonCount.get() == 4){
                        adder.add(new EmptyWidget((int) 10, 20));
                        emptyWidgetsAdded.getAndIncrement();
                        buttonCount.getAndIncrement();
                    }*/
                        adder.add(buttonWidget);
                        buttonCount.getAndIncrement();
                    }
                    else if(buttonAmount == 6){
                        // button button button button
                        //        button button
                        if(emptyWidgetsAdded.get() == 0 && buttonCount.get() == 5){
                            adder.add(new EmptyWidget((int) (((this.width/2) /2.3)), 20));
                            emptyWidgetsAdded.getAndIncrement();
                            buttonCount.getAndIncrement();
                        }
                        adder.add(buttonWidget);
                        buttonCount.getAndIncrement();
                    }else if(buttonAmount == 7){
                        // button button button button
                        //     button button button
                        //
                        if(emptyWidgetsAdded.get() == 0 && buttonCount.get() == 7){
                            adder.add(new EmptyWidget((int) (((this.width/2) /2.3))/2, 20));
                            emptyWidgetsAdded.getAndIncrement();
                            buttonCount.getAndIncrement();
                        }
                        adder.add(buttonWidget);
                        buttonCount.getAndIncrement();
                    }
                }else{
                    adder.add(buttonWidget);
                    buttonCount.getAndIncrement();
                }




            /*if(buttonCount.get() > 3 && buttonAmount > 4){
                //buttonAmount-buttonCount.get() questi sono i button rimanenti. Se è solo uno deve stare al c
                adder.add(new EmptyWidget((int) ((this.width/2) /2.3), 20));
                adder.add(buttonWidget, 1);
                this.client.player.sendMessage(Text.literal("yes adding on new row"));
            }else{
                adder.add(buttonWidget);
            }*/
            });

            //adder.add(gridWidget.getMainPositioner().relativeY(-10));
        }

        
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height/2 - this.height/10, this.width, this.height, 0.5F, 0.25F);
        gridWidget.forEachChild(this::addDrawableChild);

    }

    private List<Widget> getButtons(){
        if(this.client == null || this.client.player == null){
            return null;
        }
        int center_x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;
        List<Widget> buttons = new ArrayList<>();
        HashMap<String, String> pickedButtons = new HashMap<>(dialogue.buttons);

        if(dialogue.randomizedButtons){
            List<String> buttonKeys = dialogue.buttons.keySet().stream().toList();
            pickedButtons.clear();
            // generates randomButtonsAmonut numbers, and picks the corresponding button from the map
            for (int i = 0; i < dialogue.randomButtonsAmount; i++){
                int n = this.client.player.getRandom().nextBetween(0, dialogue.buttons.size()-1);
                //Check to avoid duplicates
                while(pickedButtons.containsKey(buttonKeys.get(n))){
                    n = this.client.player.getRandom().nextBetween(0, dialogue.buttons.size()-1);
                }
                pickedButtons.put(buttonKeys.get(n), dialogue.buttons.get(buttonKeys.get(n)));
            }
        }

        AtomicBoolean textFieldAlreadyPresent = new AtomicBoolean(false);

        pickedButtons.forEach( (text, action) -> {

            ClickActions clickAction;
            String target = "screen.lightwithin.luxdialogue.text_error";
            if(action.contains("<")){
                String[] stringParts = action.split("<");

                clickAction = ClickActions.valueOf(stringParts[0].replaceAll("<", ""));
                target = stringParts[1].replaceAll("<", "").replaceAll(">", "");
            }else{
                try{
                    clickAction = ClickActions.valueOf(action);
                }catch (IllegalArgumentException e){
                    clickAction = ClickActions.SEND_CHAT_MSG;
                    target = "screen.lightwithin.luxdialogue.action_error";
                    LightWithin.LOGGER.error("Invalid action '"+ action + "', defaulting to 'CLOSE' for dialogue with id: '" + dialogue.dialogueId + "'");
                    e.printStackTrace();
                    sendDialogueStopDreamPacket();
                }

            }

            ButtonWidget.PressAction pressAction = (buttonWidget) -> {
                Objects.requireNonNull(this.client.player).sendMessage(Text.literal(LightWithin.PREFIX_MSG+"Something went wrong trying to perform that action").formatted(Formatting.DARK_RED));
                sendDialogueStopDreamPacket();
                this.close();
            };

            Widget new_button;

            if(clickAction.equals(ClickActions.CLOSE)){
                pressAction = (button) -> {
                    this.closeWithAnimation();
                    sendDialogueStopDreamPacket();
                };
            }else if(clickAction.equals(ClickActions.SHOW_TYPE_RUNES)){
                pressAction = this::lightTypeAndRuneAction;
            }else if(clickAction.equals(ClickActions.SHOW_TARGET)){
                pressAction = this::lightTargetAction;
            }else if(clickAction.equals(ClickActions.SHOW_TYPE_INGREDIENT)){
                pressAction = this::lightTypeIngredientAction;
            }else if(clickAction.equals(ClickActions.SHOW_TARGET_INGREDIENT)){
                pressAction = this::lightTargetIngredientAction;
            }else if(clickAction.equals(ClickActions.SHOW_POWER)){
                pressAction = this::lightPowerAction;
            }else if(clickAction.equals(ClickActions.SHOW_DURATION)){
                pressAction = this::lightDurationAction;
            }else if(clickAction.equals(ClickActions.SHOW_MAXCOOLDOWN)){
                pressAction = this::lightMaxCooldownAction;
            }else if(clickAction.equals(ClickActions.SHOW_MAXCHARGES)){
                pressAction = this::lightMaxChargesAction;
            }else if(clickAction.equals(ClickActions.SHOW_LIGHT_CONDITIONS)){
                pressAction = this::lightLightConditionsAction;
            }else if(clickAction.equals(ClickActions.SHOW_TRIGGER_EVENTS)){
                pressAction = this::lightLightTriggerEventsAction;
            }else if(clickAction.equals(ClickActions.TEXT_INPUT)) {
                if (textFieldAlreadyPresent.get()) {
                    LightWithin.LOGGER.warn("A TextField is already present in this screen! You can't have more than one currently! DialogueId: " + dialogue.dialogueId);
                }
                new_button = new TextFieldWidget(this.textRenderer, 20, 20, (int) (center_x / 2.3), 20, Text.literal("you should not see this. Report please!"));
                ((TextFieldWidget) new_button).setEditable(true);
                ((TextFieldWidget) new_button).setChangedListener(this::onTextChanged);
                textFieldAlreadyPresent.set(true);
                //Adds the text field and goes to the next one
                buttons.add(new_button);
                return;
            }else if(clickAction.equals(ClickActions.GO_TYPE_AFTER_INPUT)){
                pressAction = this::goTypeAfterInput;
            }
            else if(clickAction.equals(ClickActions.GO_TARGET_AFTER_INPUT)){
                pressAction = this::goTargetAfterInput;
            }

            //Action with a target
            else if(clickAction.equals(ClickActions.GO_DIALOGUE)){
                LuxcognitaScreenV2 targetScreen = LuxdialogueScreens.LUXDIALOGUE_SCREENS.get(target);
                if(targetScreen == null){
                    this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Could not find the screen with id: " + target).formatted(Formatting.RED));
                }else{
                    TransitionScreen transitionScreen = new TransitionScreen(Text.literal("transition_to:"+targetScreen.title), targetScreen);
                    pressAction = (button) -> MinecraftClient.getInstance().setScreen(transitionScreen);
                }
            }else if(clickAction.equals(ClickActions.GO_IF_PROGRESS)){
                @NotNull String[] args = target.split(",");

                args[0] = args[0].replaceAll(",", "").replaceAll(" ", "");
                args[1] = args[1].replaceAll(",", "").replaceAll(" ", "");
                DialogueProgressState state = DialogueProgressState.valueOf(args[1]);
                if(state == null){
                    this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Could not find the dialogue progress: '" + args[1] + "'").formatted(Formatting.RED));
                    return;
                }
                if(LightWithin.LIGHT_COMPONENT.get(this.client.player).getDialogueProgressStates().contains(state)){
                    LuxcognitaScreenV2 targetScreen = LuxdialogueScreens.LUXDIALOGUE_SCREENS.get(args[0]);
                    if(targetScreen == null){
                        this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Could not find the screen with id: " + args[0]).formatted(Formatting.RED));
                        pressAction = (button -> {
                            sendDialogueStopDreamPacket();
                            this.close();
                        });
                    }else{
                        TransitionScreen transitionScreen = new TransitionScreen(Text.literal("transition_to:"+targetScreen.title), targetScreen);
                        pressAction = (button) -> MinecraftClient.getInstance().setScreen(transitionScreen);
                    }
                }
                else{
                    pressAction = (button -> {
                        sendDialogueStopDreamPacket();
                        this.closeWithAnimation();
                    });
                }
            }else if(clickAction.equals(ClickActions.SEND_CHAT_MSG)){
                String finalTarget = target;
                pressAction = (button -> {
                    this.client.player.sendMessage(Text.translatable(finalTarget));
                    sendDialogueStopDreamPacket();
                    this.closeWithAnimation();
                });
            }else if(clickAction.equals(ClickActions.SEND_OVERLAY_MSG)){
                String finalTarget = target;
                pressAction = (button -> {
                    this.client.player.sendMessage(Text.translatable(finalTarget), true);
                    sendDialogueStopDreamPacket();
                    this.closeWithAnimation();
                });
            }
            else{
                this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG+"Could not parse click action: " + action).formatted(Formatting.RED));
            }

             new_button = ButtonWidget
                    .builder(Text.translatable(text).formatted(Formatting.YELLOW),
                            pressAction
                    )
                    .size((int) (center_x/2.3), 20)
                    .build();

            buttons.add(new_button);
        });
        return buttons;
    }

    public void closeWithAnimation(){
        TransitionScreen transitionScreen = new TransitionScreen(Text.literal("closing_transition"), null, true);
        imageTicker = -1;
        MinecraftClient.getInstance().setScreen(transitionScreen);
    }

    // Utility methods not necessarily used in here but in the screen class

    //#f1f657
    private static final Style style = Style.EMPTY.withColor(15857239);

    public static final String LUXCOGNITA_PREFIX = "§b[§a§iLuxCognita§b]§r: ";

    public void lightTypeAndRuneAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderRunes();
        //light blue is 6288592
        InnerLight type = LightWithin.LIGHT_COMPONENT.get(this.client.player).getType();
        if(type instanceof NoneLight){
            LightWithinClient.sendOverlayMessageWithDuration(Text.translatable("light.description.error").formatted(Formatting.RED), ClientConfig.OVERLAY_TEXT_DURATION);
            return;
        }
        LightWithinClient.sendOverlayMessageWithDuration(Text.translatable("light.description." + type.toString().toLowerCase()).setStyle(style), ClientConfig.OVERLAY_TEXT_DURATION);

        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightTypeIngredientAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderLuxTypeItem();
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightTargetAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderTargetIcon();
        TargetType type = LightWithin.LIGHT_COMPONENT.get(this.client.player).getTargets();
        if(type.equals(TargetType.NONE)){
            LightWithinClient.sendOverlayMessageWithDuration(Text.translatable("light.description.error").formatted(Formatting.RED), ClientConfig.OVERLAY_TEXT_DURATION);
            return;
        }
        LightWithinClient.sendOverlayMessageWithDuration(Text.translatable("light.description.target." + type.toString().toLowerCase()).setStyle(style), ClientConfig.OVERLAY_TEXT_DURATION);

        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightTargetIngredientAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderLuxTargetItem();
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightPowerAction(ButtonWidget buttonWidget) {
        double power = LightWithin.LIGHT_COMPONENT.get(this.client.player).getPowerMultiplier();
        LightWithinClient.sendOverlayMessageWithDuration(Text.translatable("light.description.power", String.valueOf(power)).setStyle(style), ClientConfig.OVERLAY_TEXT_DURATION);

        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightDurationAction(ButtonWidget buttonWidget) {
        double duration = LightWithin.LIGHT_COMPONENT.get(this.client.player).getDuration();
        LightWithinClient.sendOverlayMessageWithDuration(Text.translatable("light.description.duration", String.valueOf(duration)).setStyle(style), ClientConfig.OVERLAY_TEXT_DURATION);
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightMaxCooldownAction(ButtonWidget buttonWidget) {
        int cooldown = LightWithin.LIGHT_COMPONENT.get(this.client.player).getMaxCooldown();
        LightWithinClient.sendOverlayMessageWithDuration(Text.translatable("light.description.maxcooldown", String.valueOf(cooldown)).setStyle(style), ClientConfig.OVERLAY_TEXT_DURATION);

        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightMaxChargesAction(ButtonWidget buttonWidget) {
        int charges = LightWithin.LIGHT_COMPONENT.get(this.client.player).getMaxLightCharges();
        LightWithinClient.sendOverlayMessageWithDuration(Text.translatable("light.description.maxcharges", String.valueOf(charges)).setStyle(style), ClientConfig.OVERLAY_TEXT_DURATION);
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightLightConditionsAction(ButtonWidget buttonWidget) {
        LightWithinClient.sendOverlayMessageWithDuration(Text.translatable("light.description.triggering.light_conditions").setStyle(style)
                .append(Text.translatable("light.description.triggering.light_conditions."+LightWithin.LIGHT_COMPONENT.get(this.client.player).getType().toString()).formatted(Formatting.AQUA)), ClientConfig.OVERLAY_TEXT_DURATION);

        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightLightTriggerEventsAction(ButtonWidget buttonWidget) {
        List<TriggerChecks> checks = LightWithin.LIGHT_COMPONENT.get(this.client.player).getType().getTriggerChecks();
        this.client.player.sendMessage(Text.literal(LUXCOGNITA_PREFIX).append(Text.translatable("light.description.trigger_events").formatted(Formatting.AQUA)));
        checks.forEach(triggerCheck -> this.client.player.sendMessage(Text.literal(" > ").setStyle(style).append(Text.translatable(triggerCheck.getTranslationString()))));

        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    /// The string currently written in a text field
    private String currentField = "";

    /** Updates the */
    public void onTextChanged(String input){
        this.currentField = input;
    }


    /**Parses a string that could be similar to a light's name but isn't quite it, and returns
     * the correct name
     * @param input The wannabe name of the light type
     * @return The true name/id of the light type
     */
    public String parsePossibleTypeString(String input){
        if(input.equals("defense")){
            input = "defence";
        }else if(input.equals("earth")){
            input = "earthen";
        }else if(input.equals("blaze")){
            input = "blazing";
        } else if(input.equals("fire")){
            input = "blazing";
        }else if(input.equals("ice")){
            input = "frost";
        }else if(input.equals("snow")){
            input = "frost";
        }else if(input.equals("water")){
            input = "aqua";
        }else if(input.equals("rock")){
            input = "earthen";
        }else if(input.equals("water")){
            input = "aqua";
        }else if(input.equals("air")){
            input = "wind";
        }else if(input.equals("forest")){
            input = "forest_aura";
        }else if(input.equals("thunder")){
            input = "thunder_aura";
        }else if(input.equals("forestaura")){
            input = "forest_aura";
        }else if(input.equals("thunderaura")){
            input = "thunder_aura";
        }else if(input.equals("air")){
            input = "wind";
        }else if(input.equals("nature")){
            input = "forest_aura";
        }

        return input;
    }
    
    /** Goes to the screen linked to the light type written in a text field*/
    public void goTypeAfterInput(ButtonWidget buttonWidget){
        if(currentField.equals("")){
            LightWithin.LOGGER.warn("Maybe a TextField hasn't been updated or inserted in the screen!");
        }

        // If the input is not really correct, tries to guess
        currentField = parsePossibleTypeString(currentField);
        //removes potential stuff like spaces and "_light" ecc.
        currentField = currentField.toLowerCase().replaceAll("_light", "")
                .replaceAll("light", "").replaceAll(" ", "");

        LuxcognitaScreenV2 targetScreen = LuxdialogueScreens.LUXDIALOGUE_SCREENS.get("light_info/type_unrecognized");

        if(LightWithin.INNERLIGHT_REGISTRY.containsId(LightWithin.getIdentifier(currentField))){
            targetScreen = LuxdialogueScreens.LUXDIALOGUE_SCREENS.get("light_info/"+currentField+"/start");
        }


        if(targetScreen == null){
            this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Could not find the screen with id: light_info/" + currentField).formatted(Formatting.RED));
            sendDialogueStopDreamPacket();
            this.close();
        }else{
            TransitionScreen transitionScreen = new TransitionScreen(Text.literal("transition_to:"+targetScreen.title), targetScreen);
            MinecraftClient.getInstance().setScreen(transitionScreen);
        }

    }

    /** Goes to the screen linked to the light target type written in a text field*/
    public void goTargetAfterInput(ButtonWidget buttonWidget){
        if(currentField.equals("")){
            //TODO actually the player could leave it blank on purpose
            LightWithin.LOGGER.warn("Maybe a TextField hasn't been updated or inserted in the screen!");
        }

        //removes potential stuff
        currentField = currentField.toLowerCase().replaceAll("_", "").replaceAll(" ", "");

        LuxcognitaScreenV2 targetScreen = LuxdialogueScreens.LUXDIALOGUE_SCREENS.get("light_info/target_unrecognized");

        try{
            if(TargetType.valueOf(currentField.toUpperCase()) != null){
                targetScreen = LuxdialogueScreens.LUXDIALOGUE_SCREENS.get("light_info/targets/"+currentField+"/desc");
            }
        }catch (IllegalArgumentException e){
            //transition screen to unrecognized target
        }



        if(targetScreen == null){
            this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Could not find the screen with id: light_info/targets/" + currentField+"/desc").formatted(Formatting.RED));
            sendDialogueStopDreamPacket();
            this.close();
        }else{
            TransitionScreen transitionScreen = new TransitionScreen(Text.literal("transition_to:"+targetScreen.title), targetScreen);
            MinecraftClient.getInstance().setScreen(transitionScreen);
        }

    }

    private int colorTicks = 0;

    /**Periodically changes the color, to make a little animation thing.
     * It remains in the greens anyway*/
    private int getTextColor(){
        //#3ad94f #31c83f #28b72f #1ea71e #129706
        colorTicks++;
        if(colorTicks < 5 + ClientConfig.LUXDIALOGUE_TEXT_BLINK_SPEED){
            return 3856719;
        }
        if(colorTicks < 5 + ClientConfig.LUXDIALOGUE_TEXT_BLINK_SPEED*2){
            return 3262527;
        }
        if(colorTicks < 5 + ClientConfig.LUXDIALOGUE_TEXT_BLINK_SPEED*3){
            return 2668335;
        }
        if(colorTicks < 5 + ClientConfig.LUXDIALOGUE_TEXT_BLINK_SPEED*4){
            return 2008862;
        }
        if(colorTicks < 5 + ClientConfig.LUXDIALOGUE_TEXT_BLINK_SPEED*5){
            return 1218310;
        }
        if(colorTicks < 5 + ClientConfig.LUXDIALOGUE_TEXT_BLINK_SPEED*6){
            return 2008862;
        }
        if(colorTicks < 5 + ClientConfig.LUXDIALOGUE_TEXT_BLINK_SPEED*7){
            return 2668335;
        }
        if(colorTicks < 5 + ClientConfig.LUXDIALOGUE_TEXT_BLINK_SPEED*8){
            return 3262527;
        }
        if(colorTicks < 5 + ClientConfig.LUXDIALOGUE_TEXT_BLINK_SPEED*9){
            return 3856719;
        }
        colorTicks = 0;
        return 3856719;
    }


    private int imageTicker = -1;
    private int currentImage = 0;

    private int itemTicker = -1;
    private int currentItem = 0;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        // Low (lighter) 3735330
        //Middle 2415936
        // Top (darker) 2406703
        MatrixStack matrixStack = context.getMatrices();

        matrixStack.push();


        matrixStack.scale(dialogue.mainTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE, dialogue.mainTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE, dialogue.mainTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE);


        Text mainText = Text.translatable(dialogue.mainText);
        if(dialogue.hasReplaceableMainText){
            List<String> toReplace = new ArrayList<>(dialogue.replaceablesListMain.size());
            dialogue.replaceablesListMain.forEach( replaceable -> {
                toReplace.add(parseReplacable(replaceable));
            });
            mainText = Text.translatable(dialogue.mainText, toReplace.toArray());
        }

        context.drawCenteredTextWithShadow(this.textRenderer, mainText, (int) (((float) this.width / 2)/(dialogue.mainTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE)), (int) (((float) this.height / 2 - 70)/(dialogue.mainTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE)), getTextColor());
        matrixStack.pop();
        matrixStack.push();

        if(dialogue.subTextPresent){
            Text subText = Text.translatable(dialogue.subText);


            if(dialogue.hasReplaceableSubText){
                List<String> toReplace = new ArrayList<>(dialogue.replaceablesListSub.size());
                dialogue.replaceablesListSub.forEach( replaceable -> {
                    toReplace.add(parseReplacable(replaceable));
                });
                subText = Text.translatable(dialogue.subText, toReplace.toArray());
            }
            matrixStack.scale(dialogue.subTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE, dialogue.subTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE, dialogue.subTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE);
            context.drawCenteredTextWithShadow(this.textRenderer, subText, (int) (((float) this.width / 2)/(dialogue.subTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE)), (int) (((float) this.height / 2 - 40)/(dialogue.subTextScale*ClientConfig.LUXDIALOGUE_TEXT_SCALE)), getTextColor());
            matrixStack.pop();
            matrixStack.push();
        }

        if(dialogue.showBerry){
            //TODO adjust with the screen's or config's scale
            float berryScale = 2f;
            //TODO the padding fucks up things a little
            Pair<Integer, Integer> xy = ScreenUtils.getXYItems(dialogue.berryPos, berryScale, this.width, this.height, 0, 16, 16);

            matrixStack.scale(berryScale, berryScale, berryScale);
            context.drawItem(new ItemStack(LightItems.LUXCOGNITA_BERRY), xy.getFirst(), xy.getSecond());

            matrixStack.pop();
            matrixStack.push();
        }

        if(dialogue.showItem){
            Pair<Integer, Integer> xy = ScreenUtils.getXYItems(dialogue.itemPos, dialogue.itemScale, this.width, this.height, 0, 16, 16);

            Identifier item = LuxDialogue.convertMapToId(dialogue.item);

            if(dialogue.multipleItems){
                //checks if it should change the
                if(itemTicker > dialogue.itemsInterval){
                    currentItem++;
                    if(currentItem >= dialogue.items.size()){
                        currentItem = 0;
                    }
                    itemTicker = 0;
                }

                item = LuxDialogue.convertMapToId(dialogue.items.get(currentItem));
                itemTicker++;
            }

            matrixStack.scale(dialogue.itemScale, dialogue.itemScale, dialogue.itemScale);

            context.drawItem(new ItemStack(Registries.ITEM.get(item)), xy.getFirst(), xy.getSecond());

            matrixStack.pop();
            matrixStack.push();
        }

        
        if(dialogue.showImage){
            Pair<Integer, Integer> xy = ScreenUtils.getXYImgs(dialogue.imagePos, dialogue.imageScale, dialogue.imageWidth, dialogue.imageHeight, 0);

            Identifier image = LuxDialogue.convertMapToId(dialogue.imagePath);

            if(dialogue.imageHasStages){
                //checks if it should change the
                if(imageTicker > dialogue.imageInterval){
                    currentImage++;
                    if(currentImage >= dialogue.imageStages.size()){
                        currentImage = 0;
                    }
                    imageTicker = 0;
                }
                image = LuxDialogue.convertMapToId(dialogue.imageStages.get(currentImage));
                imageTicker++;
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            ClipStack.addWindow(context.getMatrices(), new Rectangle(0,0, width, height));

            Renderer2d.renderTexture(context.getMatrices(), image, xy.getFirst(), xy.getSecond(), dialogue.imageWidth*dialogue.imageScale, dialogue.imageHeight*dialogue.imageScale);
            ClipStack.popWindow();
        }

        matrixStack.pop();


    }

    public String parseReplacable(Replaceables replaceable){
        if(replaceable.equals(Replaceables.ACTIVATION_KEY)){
            return ActivationKey.lightActivationKey.getBoundKeyLocalizedText().getString();
        }else if(replaceable.equals(Replaceables.PLAYER_NAME)){
            return this.client.player.getName().getString();
        }
        LightWithin.LOGGER.warn("The replacable: " + replaceable + " could not be parsed correctly");
        return "";
    }

    public void fillWithLayer(DrawContext context, RenderLayer layer, int startX, int startY, int endX, int endY, int z) {
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(layer);
        vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, (float)z).next();
        vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, (float)z).next();
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, (float)z).next();
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, (float)z).next();
        context.draw();
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        fillWithLayer(context, LightRenderLayer.getLightScreen(), 0, 0, this.width, this.height, 0);
    }

    public static void sendDialogueStateUpdatePacket(DialogueProgressState state, boolean shouldRemove){
        ClientPlayNetworking.send(DialogueProgressUpdatePacketC2S.ID, new DialogueProgressUpdatePacketC2S(state, shouldRemove));
    }

    /**Stops the dialogue with luxcognita and also clears the {@link me.emafire003.dev.lightwithin.status_effects.LuxcognitaDreamEffect}
     * as well as stopping the ticking of the Luxcognita BGM song, as well as the song itself*/
    public static void sendDialogueStopDreamPacket(){
        ClientPlayNetworking.send(LuxdreamClientPacketC2S.ID, new LuxdreamClientPacketC2S(LuxDialogueActions.STOP_DREAM));
        playLuxcognitaDisplaySound();
    }

    public static void playLuxcognitaDisplaySound(){
        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null || client.player == null){
            LightWithin.LOGGER.error("Error! Can't play the Luxcognita sound the ClientPlayerEntity is null");
            return;
        }
        client.player.playSound(LightSounds.LUXCOGNITA_DISPLAY, 1f, 1f);
    }

    @Override
    public void tick() {
        super.tick();
        if (System.currentTimeMillis() > this.loadStartTime + ClientConfig.CLOSE_LUXDIALOGUE_SCREEN_AFTER*1000) {
            sendDialogueStopDreamPacket();
            this.close();
        }
    }

    @Override
    public void close() {
        imageTicker = -1;
        currentImage = 0;
        itemTicker = -1;
        currentItem = 0;
        currentField = "";
        super.close();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean shouldPause() {
        //This is the thing that allows the screen to keep being animated by the way.
        return false;
    }
}


