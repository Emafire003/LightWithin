package me.emafire003.dev.lightwithin.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.LightRenderLayer;
import me.emafire003.dev.lightwithin.client.LightWithinClient;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.ClickActions;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.LuxDialogue;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.items.LuxcognitaBerryItem;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.ScreenUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//TODO when this is open the player should be mostly transparent and should also be invulnerable for a time. An attack will cancel the screen and make the player vulnerable again after 2/3 seconds.
public class LuxcognitaScreenV2 extends Screen{
    private static final long MIN_LOAD_TIME_MS = 60000L;
    private long loadStartTime;
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

        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().margin(4, this.height/2, 4, 0);
        gridWidget.setSpacing(10);
        //TODO i should probably scale things if i want to have more that 4 buttons
        GridWidget.Adder adder = gridWidget.createAdder(dialogue.buttons.size());

        List<ButtonWidget> buttons = getButtons();

        if(this.client.player == null){
            LightWithin.LOGGER.error("ERROR! The ClientPlayer is null!");
            return;
        }

        if(dialogue.dialogueProgress){
            this.client.player.sendMessage(Text.literal("Ohi, implement the dialogue progress state!"));
        }

        buttons.forEach(adder::add);

        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, 0, this.width, this.height, 0.5F, 0.25F);
        gridWidget.forEachChild(this::addDrawableChild);
    }

    private List<ButtonWidget> getButtons(){
        if(this.client.player == null){
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
                }

            }

            ButtonWidget.PressAction pressAction = (buttonWidget) -> {
                Objects.requireNonNull(this.client.player).sendMessage(Text.literal(LightWithin.PREFIX_MSG+"Something went wrong trying to perform that action").formatted(Formatting.DARK_RED));
                this.close();
            };

            if(clickAction.equals(ClickActions.CLOSE)){
                pressAction = (buttonWidget) -> this.close();
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
            }

            //Action with a target
            else if(clickAction.equals(ClickActions.GO_DIALOGUE)){
                LuxcognitaScreenV2 targetScreen = LuxdialogueScreens.LUXDIALOGUE_SCREENS.get(target);
                if(targetScreen == null){
                    this.client.player.sendMessage(Text.literal(LightWithin.PREFIX_MSG + "Could not find the screen with id: " + target).formatted(Formatting.RED));
                }
                pressAction = (button) -> MinecraftClient.getInstance().setScreen(targetScreen);
            }else if(clickAction.equals(ClickActions.SEND_CHAT_MSG)){
                String finalTarget = target;
                pressAction = (button -> {
                    this.client.player.sendMessage(Text.translatable(finalTarget));
                    this.close();
                });
            }else if(clickAction.equals(ClickActions.SEND_OVERLAY_MSG)){
                String finalTarget = target;
                pressAction = (button -> {
                    this.client.player.sendMessage(Text.translatable(finalTarget), true);
                    this.close();
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

    public void playLuxcognitaDisplaySound(){
        if(this.client.player == null){
            LightWithin.LOGGER.error("Error! Can't play the Luxcognita sound the ClientPlayerEntity is null");
            return;
        }
        this.client.player.playSound(LightSounds.LUXCOGNITA_DISPLAY, 1f, 1f);
    }

    public void lightTypeAndRuneAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderRunes();
        LuxcognitaBerryItem.sendLightTypeMessage(this.client.player);
        this.close();
    }

    public void lightTypeIngredientAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderLuxTypeItem();
        this.close();
    }

    public void lightTargetAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderTargetIcon();
        LuxcognitaBerryItem.sendLightTargetMessage(this.client.player);
        this.close();
    }

    public void lightTargetIngredientAction(ButtonWidget buttonWidget) {
        LightWithinClient.getRendererEventHandler().renderLuxTargetItem();
        this.close();
    }

    public void lightPowerAction(ButtonWidget buttonWidget) {
        LuxcognitaBerryItem.sendLightPowerMessage(this.client.player);
        this.close();
    }

    public void lightDurationAction(ButtonWidget buttonWidget) {
        LuxcognitaBerryItem.sendLightDurationMessage(this.client.player);
        this.close();
    }

    private int colorTicks = 0;

    /**Periodically changes the color, to make a little animation thing.
     * It remains in the greens anyway*/
    private int getTextColor(){
        //#3ad94f #31c83f #28b72f #1ea71e #129706
        //TODO make better?
        colorTicks++;
        if(colorTicks < 10){
            return 3856719;
        }
        if(colorTicks < 20){
            return 3262527;
        }
        if(colorTicks < 30){
            return 2668335;
        }
        if(colorTicks < 40){
            return 2008862;
        }
        if(colorTicks < 50){
            return 1218310;
        }
        if(colorTicks < 60){
            return 2008862;
        }
        if(colorTicks < 70){
            return 2668335;
        }
        if(colorTicks < 80){
            return 3262527;
        }
        if(colorTicks < 90){
            return 3856719;
        }
        colorTicks = 0;
        return 3856719;
    }

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
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable(dialogue.mainText), (int) (((float) this.width / 2)/dialogue.mainTextScale), (int) (((float) this.height / 2 - 70)/dialogue.mainTextScale), getTextColor());
        matrixStack.pop();
        matrixStack.push();

        if(dialogue.subTextPresent){
            matrixStack.scale(dialogue.subTextScale, dialogue.subTextScale, dialogue.subTextScale);
            context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable(dialogue.subText), (int) (((float) this.width / 2)/dialogue.subTextScale), (int) (((float) this.height / 2 - 40)/dialogue.subTextScale), getTextColor());
            matrixStack.pop();
            matrixStack.push();
        }

        if(dialogue.showBerry){
            //TODO adjust with the screen's or config's scale
            float berryScale = 2f;
            //TODO the padding fucks up things a little
            Pair<Integer, Integer> xy = ScreenUtils.getXY(dialogue.berryPos, berryScale, this.width, this.height, 0, 16, 16);

            matrixStack.scale(berryScale, berryScale, berryScale);
            context.drawItem(new ItemStack(LightItems.LUXCOGNITA_BERRY), xy.getFirst(), xy.getSecond());

            matrixStack.pop();
            matrixStack.push();
        }

        if(dialogue.showItem){
            Pair<Integer, Integer> xy = ScreenUtils.getXY(dialogue.itemPos, dialogue.itemScale, this.width, this.height, 0, 16, 16);

            matrixStack.scale(dialogue.itemScale, dialogue.itemScale, dialogue.itemScale);

            context.drawItem(new ItemStack(Registries.ITEM.get(dialogue.item)), xy.getFirst(), xy.getSecond());

            matrixStack.pop();
            matrixStack.push();
        }

        
        if(dialogue.showImage){
            matrixStack.scale(dialogue.imageScale, dialogue.imageScale, dialogue.imageScale);
            Pair<Integer, Integer> xy = ScreenUtils.getXY(dialogue.imagePos, dialogue.imageScale, width, height, 5, dialogue.imageWidth, dialogue.imageHeight);
            context.drawTexture(dialogue.imagePath, xy.getFirst(), xy.getSecond(), 1, 1, dialogue.imageWidth, dialogue.imageHeight, dialogue.imageWidth, dialogue.imageHeight);
            matrixStack.pop();
            matrixStack.push();
        }

        matrixStack.pop();


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

    @Override
    public void tick() {
        super.tick();
        if (System.currentTimeMillis() > this.loadStartTime + MIN_LOAD_TIME_MS) {
            this.close();
        }
    }

    @Override
    public void close() {
        //TODO maybe remove especially if it's not a "finish" screen
        playLuxcognitaDisplaySound();
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


