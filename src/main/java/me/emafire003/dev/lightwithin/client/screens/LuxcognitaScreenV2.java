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
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.items.LuxcognitaBerryItem;
import me.emafire003.dev.lightwithin.networking.DialogueProgressUpdatePacketC2S;
import me.emafire003.dev.lightwithin.networking.LuxdreamStopPacketC2S;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.ScreenUtils;
import me.x150.renderer.ClipStack;
import me.x150.renderer.Rectangle;
import me.x150.renderer.Renderer2d;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public class LuxcognitaScreenV2 extends Screen{
    private static final long MIN_LOAD_TIME_MS = 60000L;
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
            if(LightWithin.LIGHT_COMPONENT.get(this.client.player).getDialogueProgressStates().contains(dialogue.redirectStateRequired)){
                if(!LuxdialogueScreens.LUXDIALOGUE_SCREENS.containsKey(dialogue.redirectTo)){
                    this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Error! Cannot redirect to screen '" + dialogue.redirectTo + "' since it's not registered!").formatted(Formatting.RED));
                    // TODO make sure these returns don't mess up stuff
                    return;
                }
                this.client.setScreen(LuxdialogueScreens.LUXDIALOGUE_SCREENS.get(dialogue.redirectTo));
                return;
            }
        }

        // If this screen produces a dialogue progress state, update it
        if(dialogue.dialogueProgress){
            if(!LightWithin.LIGHT_COMPONENT.get(this.client.player).getDialogueProgressStates().contains(dialogue.dialogueProgressState)){
                sendDialogueStateUpdatePacket(dialogue.dialogueProgressState, dialogue.removeDialogueProgress);
            }

        }

        // Sets up the grid for the screen
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().margin(4, 10, 4, 0);
        gridWidget.setSpacing(10);
        int maxButtonsPerRow = 4;
        // adjusts the number of columns based on the amount of buttons
        if(dialogue.buttons.size() > 4 && dialogue.buttons.size()%2==1 ){
            maxButtonsPerRow = 3;
        }
        GridWidget.Adder adder = gridWidget.createAdder(Math.min(dialogue.buttons.size(), maxButtonsPerRow));


        // Gets the buttons for this screen and the action
        List<ButtonWidget> buttons = getButtons();

        AtomicInteger buttonCount = new AtomicInteger(1);
        AtomicInteger emptyWidgetsAdded = new AtomicInteger();
        if(buttons.isEmpty()){
            LightWithin.LOGGER.warn("The button list for dialogueId '" + dialogue.dialogueId + "' is empty!");
        }else{
            buttons.forEach(buttonWidget -> {

                if(buttonCount.get() > 3 && dialogue.buttons.size() > 4){
                    if(dialogue.buttons.size() == 5){
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
                    else if(dialogue.buttons.size() == 6){
                        // button button button button
                        //        button button
                        if(emptyWidgetsAdded.get() == 0 && buttonCount.get() == 5){
                            adder.add(new EmptyWidget((int) (((this.width/2) /2.3)), 20));
                            emptyWidgetsAdded.getAndIncrement();
                            buttonCount.getAndIncrement();
                        }
                        adder.add(buttonWidget);
                        buttonCount.getAndIncrement();
                    }else if(dialogue.buttons.size() == 7){
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




            /*if(buttonCount.get() > 3 && dialogue.buttons.size() > 4){
                //dialogue.buttons.size()-buttonCount.get() questi sono i button rimanenti. Se è solo uno deve stare al c
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

    private List<ButtonWidget> getButtons(){
        if(this.client == null || this.client.player == null){
            return null;
        }
        int center_x = MinecraftClient.getInstance().getWindow().getScaledWidth()/2;
        List<ButtonWidget> buttons = new ArrayList<>();
        dialogue.buttons.forEach( (text, action) -> {

            ClickActions clickAction;
            String target = "screen.lightwithin.luxdialogue.text_error";
            if(action.contains("<")){
                String[] stringParts = action.split("<");
                //TODO actually find out where the "<" goes
                clickAction = ClickActions.valueOf(stringParts[0].replaceAll("<", ""));
                //TODO see if there could be a possibility of multiple arguments
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
                this.close();
            };

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
            }

            //Action with a target
            else if(clickAction.equals(ClickActions.GO_DIALOGUE)){
                LuxcognitaScreenV2 targetScreen = LuxdialogueScreens.LUXDIALOGUE_SCREENS.get(target);
                if(targetScreen == null){
                    this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Could not find the screen with id: " + target).formatted(Formatting.RED));
                    pressAction = (button -> {
                        sendDialogueStopDreamPacket();
                        this.close();
                    });
                }else{
                    TransitionScreen transitionScreen = new TransitionScreen(Text.literal("transition_to:"+targetScreen.title), targetScreen);
                    pressAction = (button) -> MinecraftClient.getInstance().setScreen(transitionScreen);
                }
            }else if(clickAction.equals(ClickActions.SEND_CHAT_MSG)){
                String finalTarget = target;
                pressAction = (button -> {
                    this.client.player.sendMessage(Text.translatable(finalTarget));
                    this.closeWithAnimation();
                });
            }else if(clickAction.equals(ClickActions.SEND_OVERLAY_MSG)){
                String finalTarget = target;
                pressAction = (button -> {
                    this.client.player.sendMessage(Text.translatable(finalTarget), true);
                    this.closeWithAnimation();
                });
            }else{
                this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG+"Could not parse click action: " + action).formatted(Formatting.RED));
            }


            ButtonWidget new_button = ButtonWidget
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

    public void playLuxcognitaDisplaySound(){
        if(this.client == null || this.client.player == null){
            LightWithin.LOGGER.error("Error! Can't play the Luxcognita sound the ClientPlayerEntity is null");
            return;
        }
        this.client.player.playSound(LightSounds.LUXCOGNITA_DISPLAY, 1f, 1f);
    }

    public void lightTypeAndRuneAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderRunes();
        LuxcognitaBerryItem.sendLightTypeMessage(this.client.player);
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
        LuxcognitaBerryItem.sendLightTargetMessage(this.client.player);
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightTargetIngredientAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderLuxTargetItem();
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightPowerAction(ButtonWidget buttonWidget) {
        LuxcognitaBerryItem.sendLightPowerMessage(this.client.player);
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightDurationAction(ButtonWidget buttonWidget) {
        LuxcognitaBerryItem.sendLightDurationMessage(this.client.player);
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightMaxCooldownAction(ButtonWidget buttonWidget) {
        LuxcognitaBerryItem.sendLightMaxCooldownMessage(this.client.player);
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    public void lightMaxChargesAction(ButtonWidget buttonWidget) {
        LuxcognitaBerryItem.sendLightMaxChargesMessage(this.client.player);
        sendDialogueStopDreamPacket();
        this.closeWithAnimation();
    }

    private int colorTicks = 0;

    /**Periodically changes the color, to make a little animation thing.
     * It remains in the greens anyway*/
    private int getTextColor(){
        //#3ad94f #31c83f #28b72f #1ea71e #129706
        //TODO make better?
        colorTicks++;
        if(colorTicks < 15){
            return 3856719;
        }
        if(colorTicks < 25){
            return 3262527;
        }
        if(colorTicks < 35){
            return 2668335;
        }
        if(colorTicks < 45){
            return 2008862;
        }
        if(colorTicks < 55){
            return 1218310;
        }
        if(colorTicks < 65){
            return 2008862;
        }
        if(colorTicks < 75){
            return 2668335;
        }
        if(colorTicks < 85){
            return 3262527;
        }
        if(colorTicks < 95){
            return 3856719;
        }
        colorTicks = 0;
        return 3856719;
    }


    private int imageTicker = -1;
    private int currentImage = 0;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        // Low (lighter) 3735330
        //Middle 2415936
        // Top (darker) 2406703
        MatrixStack matrixStack = context.getMatrices();

        matrixStack.push();


        matrixStack.scale(dialogue.mainTextScale, dialogue.mainTextScale, dialogue.mainTextScale);
        Text mainText = Text.translatable(dialogue.mainText);
        if(dialogue.hasReplaceableMainText){
            List<String> toReplace = new ArrayList<>(dialogue.replaceablesListMain.size());
            dialogue.replaceablesListMain.forEach( replaceable -> {
                toReplace.add(parseReplacable(replaceable));
            });
            mainText = Text.translatable(dialogue.mainText, toReplace.toArray());
        }
        
        context.drawCenteredTextWithShadow(this.textRenderer, mainText, (int) (((float) this.width / 2)/dialogue.mainTextScale), (int) (((float) this.height / 2 - 70)/dialogue.mainTextScale), getTextColor());
        matrixStack.pop();
        matrixStack.push();

        if(dialogue.subTextPresent){
            Text subText = Text.translatable(dialogue.subText);
            if(dialogue.hasReplaceableSubText){
                List<String> toReplace = new ArrayList<>(dialogue.replaceablesListMain.size());
                dialogue.replaceablesListMain.forEach( replaceable -> {
                    toReplace.add(parseReplacable(replaceable));
                });
                subText = Text.translatable(dialogue.subText, toReplace.toArray());
            }
            matrixStack.scale(dialogue.subTextScale, dialogue.subTextScale, dialogue.subTextScale);
            context.drawCenteredTextWithShadow(this.textRenderer, subText, (int) (((float) this.width / 2)/dialogue.subTextScale), (int) (((float) this.height / 2 - 40)/dialogue.subTextScale), getTextColor());
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

            matrixStack.scale(dialogue.itemScale, dialogue.itemScale, dialogue.itemScale);

            context.drawItem(new ItemStack(Registries.ITEM.get(dialogue.item)), xy.getFirst(), xy.getSecond());

            matrixStack.pop();
            matrixStack.push();
        }

        
        if(dialogue.showImage){
            Pair<Integer, Integer> xy = ScreenUtils.getXYImgs(dialogue.imagePos, dialogue.imageScale, dialogue.imageWidth, dialogue.imageHeight, 0);

            Identifier image = dialogue.imagePath;
            if(dialogue.imageHasStages){
                //checks if it should change the
                if(imageTicker > dialogue.imageInterval){
                    currentImage++;
                    if(currentImage >= dialogue.imageStages.size()){
                        currentImage = 0;
                    }
                    imageTicker = 0;
                }
                image = dialogue.imageStages.get(currentImage);
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
            //TODO remove
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

    public static void sendDialogueStopDreamPacket(){
        ClientPlayNetworking.send(LuxdreamStopPacketC2S.ID, new LuxdreamStopPacketC2S());
    }

    @Override
    public void tick() {
        super.tick();
        if (System.currentTimeMillis() > this.loadStartTime + MIN_LOAD_TIME_MS) {
            sendDialogueStopDreamPacket();
            this.close();
        }
    }

    @Override
    public void close() {
        //TODO maybe remove especially if it's not a "finish" screen
        playLuxcognitaDisplaySound();
        imageTicker = -1;
        currentImage = 0;
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


