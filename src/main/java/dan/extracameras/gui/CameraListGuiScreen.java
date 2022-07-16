package dan.extracameras.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.utils.I18nUtils;
import dan.extracameras.utils.Options;
import dan.extracameras.utils.RenderUtils;
import dan.extracameras.utils.Variables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;

public class CameraListGuiScreen extends Screen {

    private Screen gui;
    private boolean imagePicked = false;
    private boolean dimensionPicked = false;
    private boolean posPicked = false;
    private boolean namePicked = false;
    private boolean activePicked = false;
    private DoubleOptionSliderWidget entryHeightSlider;
    private DoubleOptionSliderWidget previewHeightSlider;
    private DoubleOptionSliderWidget entryWidthSlider;
    private ButtonWidget tutorialButton;
    private ButtonWidget restoreButton;
    private CyclingButtonWidget allPreviewButton;
    private boolean allPreview = false;
    private GuiManager guiManager;
    private List<String> placed;
    /*
     * 0 = image
     * 1 = pos
     * 2 = name
     * 3 = active
     * 4 = dimension
     */
    private int[] xPoses = new int[5];
    /*
     * 0 = image
     * 1 = text
     */
    private int[] yPoses = new int[2];
    /*
     * 0 = image
     * 1 = pos
     * 2 = name
     * 3 = active
     * 4 = dimension
     */
    private int[] widthes = new int[5];
    private int fontHeight;
    /*
     * 0 = image
     * 1 = pos
     * 2 = name
     * 3 = active
     * 4 = dimension
     */
    private int[] formRev = new int[5];
    /*
     * 0 = x
     * 1 = y
     */
    private int[] movingMouse = new int[2];
    private int width1;
    private int height1;
    private int image;

    public CameraListGuiScreen(Screen parent) {
        super(new TranslatableText("text.extracameras.camera_list_gui"));
        this.gui = parent;
        this.guiManager = new GuiManager((int) (gui.width / 2 - Variables.CamerasListVariables.entryWidth / 2), 44, (int) Variables.CamerasListVariables.entryWidth, (int) Variables.CamerasListVariables.previewSize);
    }

    public void init() {
        initNegative1(formRev);
        client.keyboard.setRepeatEvents(true);
        this.addDrawableChild(new ButtonWidget(5, height - 28, 74, 20, new TranslatableText("text.extracameras.done"), button -> {
            this.client.setScreen(gui);
        }));
        this.tutorialButton = this.addDrawableChild(new ButtonWidget(5, height - 52, 74, 20, new TranslatableText("text.extracameras.tutorial"), button -> {
            //TODO: tutorial
        }));
        this.restoreButton = this.addDrawableChild(new ButtonWidget(5, height - 76, 74, 20, new TranslatableText("text.extracameras.restore"), button -> {
        }));
        this.entryHeightSlider = this.addDrawableChild(new DoubleOptionSliderWidget(client.options, 100, this.height - 52, 135, 20, Options.CAMERA_LIST_ENTRY_HEIGHT, new ArrayList<>()));
        this.previewHeightSlider = this.addDrawableChild(new DoubleOptionSliderWidget(client.options, 100, this.height - 28, 135, 20, Options.CAMERA_LIST_PREVIEW_HEIGHT, new ArrayList<>()));
        this.entryWidthSlider = this.addDrawableChild(new DoubleOptionSliderWidget(client.options, 100, this.height - 76, 135, 20, Options.CAMERA_LIST_ENTRY_WIDTH, new ArrayList<>()));
        this.allPreviewButton = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.allPreview).build(this.width - 141, this.height - 76, 135, 20, new TranslatableText("text.extracameras.preview_on"), (button, active) -> this.allPreview = active));
        Variables.config.loadConfig();
        Variables.config.reloadConfig();
        this.width1 = (int) Variables.CamerasListVariables.entryWidth;
        this.height1 = (int) Variables.CamerasListVariables.entryHeight;
        this.image = (int) Variables.CamerasListVariables.previewSize;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        initNegative1(xPoses);
        initNegative1(yPoses);
        initNegative1(widthes);
        renderBackground(matrices);
        RenderUtils.renderSpaceBackroundDarker(40, 0, width, height, height - 86, width);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, super.title, width / 2, 20, 16777215);
        if (width < 736) {
            this.entryHeightSlider.visible = false;
            this.entryWidthSlider.visible = false;
            this.previewHeightSlider.visible = false;
            this.tutorialButton.visible = false;
            this.restoreButton.visible = false;
            this.allPreviewButton.visible = false;
            drawCenteredText(matrices, textRenderer, I18nUtils.getString("text.extracameras.width_low", new Object[0]), width / 2, height / 2 - 50, 16777215);
        } else {
            boolean picked = getAnyPicked();
            this.guiManager.updateMouse(mouseX, mouseY);
            this.guiManager.render(matrices, textRenderer, this.allPreview, picked);
            int xPos = 250;
            int yPos = height - 42 - (int) Variables.CamerasListVariables.previewSize / 2;
            this.xPoses[0] = xPos;
            this.yPoses[0] = yPos;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, RenderUtils.NO_IMAGE);
            RenderSystem.texParameter(3553, 10241, 9728);
            drawTexture(matrices, xPos, yPos, 0, 0, (int) Variables.CamerasListVariables.previewSize, (int) Variables.CamerasListVariables.previewSize, (int) Variables.CamerasListVariables.previewSize, (int) Variables.CamerasListVariables.previewSize);
            widthes[0] = (int) Variables.CamerasListVariables.previewSize;
            xPos = 325;
            yPos = height - 42;
            this.yPoses[1] = yPos;
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 1.5f, xPos, yPos);
            drawTextWithShadow(matrices, textRenderer, new TranslatableText("text.extracameras.coordinates"), xPos, yPos, 16777215);
            this.fontHeight = (int) (textRenderer.fontHeight * 1.5);
            this.xPoses[1] = xPos;
            this.widthes[1] = textRenderer.getWidth(new TranslatableText("text.extracameras.coordinates"));
            matrices.pop();
            matrices.push();
            int posWidth = textRenderer.getWidth(I18nUtils.getString("text.extracameras.coordinates"));
            xPos = xPos + 80 + posWidth;
            RenderUtils.positionAccurateScale(matrices, 1.5f, xPos, yPos);
            drawTextWithShadow(matrices, textRenderer, new TranslatableText("text.extracameras.dimension"), xPos, yPos, 16777215);
            this.xPoses[4] = xPos;
            this.widthes[4] = posWidth;
            matrices.pop();
            matrices.push();
            int dimensionWidth = textRenderer.getWidth(I18nUtils.getString("text.extracameras.dimension"));
            xPos = xPos + 80 + dimensionWidth;
            RenderUtils.positionAccurateScale(matrices, 1.5f, xPos, yPos);
            drawTextWithShadow(matrices, textRenderer, new TranslatableText("text.extracameras.active"), xPos, yPos, 16777215);
            this.xPoses[3] = xPos;
            this.widthes[3] = dimensionWidth;
            matrices.pop();
            matrices.push();
            int nameWidth = textRenderer.getWidth(I18nUtils.getString("text.extracameras.name"));
            xPos = xPos + 80 + nameWidth;
            RenderUtils.positionAccurateScale(matrices, 1.5f, xPos, yPos);
            drawTextWithShadow(matrices, textRenderer, new TranslatableText("text.extracameras.name"), xPos, yPos, 16777215);
            this.xPoses[2] = xPos;
            this.widthes[2] = nameWidth;
            matrices.pop();
            this.entryHeightSlider.visible = true;
            this.entryWidthSlider.visible = true;
            this.previewHeightSlider.visible = true;
            this.tutorialButton.visible = true;
            this.restoreButton.visible = true;
            this.allPreviewButton.visible = true;
            this.guiManager.setX((int) (gui.width / 2 - Variables.CamerasListVariables.entryWidth / 2));
            this.guiManager.setWidth((int) Variables.CamerasListVariables.entryWidth);
            this.guiManager.setHeight((int) Variables.CamerasListVariables.entryHeight);
        }
    }

    public boolean getAnyPicked() {
        return this.namePicked || this.dimensionPicked || this.posPicked || this.activePicked;
    }

    @Override
    public void onClose() {
        this.client.setScreen(gui);
        Variables.config.saveConfig();
    }

    /*
     * 0 = x
     * 1 = y
     */
    public int[] calculateWithMouseCenter(int mouseX, int mouseY, int x, int y) {
        int[] res = new int[2];
        int xDifference = mouseX - x;
        res[0] = mouseX - xDifference;
        int yDifference = mouseY - y;
        res[1] = mouseY - yDifference;
        return res;
    }

    public void initNegative1(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = -1;
        }
    }

    public void place(int mouseX, int mouseY, int pos) {
        GuiManager.PositionResult res = this.guiManager.canPlaceThere((int) mouseX, (int) mouseY);
        if (res.isCanPlaceThere()) {
            this.formRev[pos] = res.getFormInList();
        } else {
            formRev[pos] = -1;
        }
        Variables.config.saveConfig();
    }
}
