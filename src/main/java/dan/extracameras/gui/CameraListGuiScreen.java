package dan.extracameras.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.config.Config;
import dan.extracameras.utils.I18nUtils;
import dan.extracameras.utils.Instance;
import dan.extracameras.utils.Options;
import dan.extracameras.utils.RenderUtils;
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
    private DoubleOptionSliderWidget entryHeightSlider;
    private DoubleOptionSliderWidget previewHeightSlider;
    private DoubleOptionSliderWidget entryWidthSlider;
    private ButtonWidget tutorialButton;
    private ButtonWidget restoreButton;
    private CyclingButtonWidget allPreviewButton;
    private boolean allPreview = false;


    public CameraListGuiScreen(Screen parent) {
        super(new TranslatableText("text.extracameras.camera_list_gui"));
        this.gui = parent;
    }

    public void init() {
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
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
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
//            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//            RenderSystem.setShaderTexture(0, RenderUtils.NO_IMAGE);
//            RenderSystem.texParameter(3553, 10241, 9728);
        }
    }

    @Override
    public void onClose() {
        this.client.setScreen(gui);
        Config.getInstance().saveConfig();
    }
}
