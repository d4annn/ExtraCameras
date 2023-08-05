package dan.extracameras.gui;

import dan.extracameras.camera.Camera;
import dan.extracameras.config.Config;
import dan.extracameras.gui.widgets.CameraListWidget;
import dan.extracameras.utils.CameraUtils;
import dan.extracameras.utils.I18nUtils;
import dan.extracameras.utils.Size;
import dan.extracameras.utils.Instance;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CamerasListScreen extends Screen {

    protected int left;
    private Camera selectedCamera;
    private ButtonWidget deleteButton;
    private ButtonWidget editButton;
    private ButtonWidget watchButton;
    private ButtonWidget pinButton;
    private ButtonWidget addButton;
    private ButtonWidget tutorialButton;
    private ButtonWidget backButton;
    private ButtonWidget editGuiButton;
    private CameraListWidget camerasList;
    private Text tooltip;
    private TextFieldWidget filter;
    private boolean delete = false;
    private Size deleteButtonSize;

    public CamerasListScreen() {
        super(new TranslatableText("text.extracameras.cameras_list"));
        this.selectedCamera = null;
    }

    protected void init() {
        super.init();
        client.keyboard.setRepeatEvents(true);
        this.camerasList = new CameraListWidget(this);
        this.camerasList.updateEntries();
        int stringWidth = client.textRenderer.getWidth(I18nUtils.getString("text.extracameras.filter", new Object[0]) + ":");
        this.filter = this.addDrawableChild(new TextFieldWidget(client.textRenderer, width / 2 - 153 + stringWidth + 5, height - 80, 305 - stringWidth - 5, 20, null));
        this.filter.setMaxLength(35);
        this.addButton = this.addDrawableChild(new ButtonWidget(width / 2 - 154, height - 52, 74, 20, new TranslatableText("text.extracameras.add"), button -> {
            client.setScreen(new CreateCameraScreen(this, null, false));
        }));
        this.deleteButton = this.addDrawableChild(new ButtonWidget(width / 2 - 76, height - 52, 74, 20, new TranslatableText("text.extracameras.delete"), button -> {
            if (this.delete) {
                Instance.currentWorldCameras.deleteCamera(Instance.currentWorldCameras.getCameras().indexOf(this.selectedCamera));
                Config.getInstance().changeWorldCamerasConfig(Instance.currentWorldCameras);
                Config.getInstance().saveConfig();
                this.delete = false;
                this.camerasList.updateEntries();
            } else {
                this.delete = true;
            }
        }));
        this.deleteButtonSize = new Size(width / 2 - 76, height - 52, 74, 20);
        this.deleteButton.active = false;
        this.editButton = this.addDrawableChild(new ButtonWidget(width / 2 + 2, height - 52, 74, 20, new TranslatableText("text.extracameras.edit"), button -> {
            client.setScreen(new CreateCameraScreen(this, this.selectedCamera, true));
        }));
        this.editButton.active = false;
        this.watchButton = this.addDrawableChild(new ButtonWidget(width / 2 + 80, height - 52, 74, 20, new TranslatableText("text.extracameras.watch"), button -> {
            CameraUtils.onEnable(this.selectedCamera);
        }));
        this.watchButton.active = false;
        this.tutorialButton = this.addDrawableChild(new ButtonWidget(width / 2 + 80, height - 28, 74, 20, new TranslatableText("text.extracameras.tutorial"), button -> {
            //TODO: tutorial
        }));
        this.pinButton = this.addDrawableChild(new ButtonWidget(width / 2 - 76, height - 28, 74, 20, new TranslatableText("text.extracameras.pin"), button -> {
            Camera cam = this.selectedCamera;
            this.selectedCamera.setPinned(!this.selectedCamera.isPinned());
            Instance.currentWorldCameras.changeCamera(Instance.currentWorldCameras.getCameras().indexOf(cam), this.selectedCamera);
            Config.getInstance().changeWorldCamerasConfig(Instance.currentWorldCameras);
            Config.getInstance().saveConfig();
            this.camerasList.updateEntries();
        }));
        this.pinButton.active = false;
        this.editGuiButton = this.addDrawableChild(new ButtonWidget(width / 2 + 2, height - 28, 74, 20, new TranslatableText("text.extracameras.edit_gui"), button -> {
            client.setScreen(new CameraListGuiScreen(this));
        }));
        this.backButton = this.addDrawableChild(new ButtonWidget(width / 2 - 154, height - 28, 74, 20, new TranslatableText("text.extracameras.back"), button -> {
            client.setScreen(new MapScreen());
        }));
        this.filter.setTextFieldFocused(true);
        boolean cameraSelected = (this.selectedCamera != null);
        this.editButton.active = cameraSelected;
        this.pinButton.active = cameraSelected;
        this.watchButton.active = cameraSelected;
        this.deleteButton.active = cameraSelected;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bool = super.keyPressed(keyCode, scanCode, modifiers);
        if (this.filter.isFocused()) {
            this.camerasList.updateFilter(this.filter.getText().toLowerCase());
        }
        return bool;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        boolean bool = super.charTyped(chr, modifiers);
        if (this.filter.isFocused()) {
            this.camerasList.updateFilter(this.filter.getText().toLowerCase());
        }
        return bool;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.camerasList.mouseClicked(mouseX, mouseY, button);
        if(this.delete) {
            if(mouseX >= this.deleteButtonSize.x && mouseX < this.deleteButtonSize.x + this.deleteButtonSize.width && mouseY >= this.deleteButtonSize.y && mouseY < this.deleteButtonSize.y + this.deleteButtonSize.height) {
                this.delete = true;
            } else {
                this.camerasList.updateEntries();
                this.delete = false;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.camerasList.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.camerasList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.camerasList.mouseScrolled(mouseX, mouseY, amount);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(700);
        this.tooltip = null;
        this.camerasList.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, super.title, width / 2, 20, 16777215);
        drawStringWithShadow(matrices, textRenderer, I18nUtils.getString("text.extracameras.filter", new Object[0]) + ":", (int) width / 2 - 153, height - 75, 16777215);
        if (this.tooltip != null) {
            renderTooltip(matrices, tooltip, mouseX, mouseY);
        }
        if (this.camerasList.getSelectedOrNull() == null) {
            this.selectedCamera = null;
        }
        boolean cameraSelected = (this.selectedCamera != null);
        this.editButton.active = cameraSelected && !this.delete;
        this.pinButton.active = cameraSelected && !this.delete;
        this.watchButton.active = cameraSelected && !this.delete;
        this.deleteButton.active = cameraSelected;
        this.addButton.active = !delete;
        this.tutorialButton.active = !delete;
        this.backButton.active = !delete;
        this.editGuiButton.active = !delete;

        if (this.delete) {
            this.camerasList.deleteEntries();
            drawCenteredText(matrices, textRenderer, I18nUtils.getString("text.extracameras.confirm_delete", new Object[0]), width / 2, height / 2 - 50, 16777215);
            drawCenteredText(matrices, textRenderer, I18nUtils.getString("text.extracameras.cancel_delete", new Object[0]), width / 2, height / 2 - 40, 16777215);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        close();
    }

    public void setTooltip(Text tooltip) {
        this.tooltip = tooltip;
    }

    private void close() {
        assert this.client != null;
        this.client.setScreen(null);
        this.client.mouse.lockCursor();
        client.keyboard.setRepeatEvents(false);
    }

    public void setSelectedCamera(Camera camera) {
        this.selectedCamera = camera;
        boolean selected = (this.selectedCamera != null);
        this.editButton.active = selected;
        this.pinButton.active = selected;
        this.watchButton.active = selected;
        this.deleteButton.active = selected;
    }

    public Camera getSelected() {
        return this.selectedCamera;
    }
}
