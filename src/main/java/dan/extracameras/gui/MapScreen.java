package dan.extracameras.gui;

import dan.extracameras.camera.Camera;
import dan.extracameras.utils.RenderUtils;
import dan.extracameras.utils.Instance;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.awt.*;

public class MapScreen extends Screen {

    private final int referenceHeight = 255;
    private Camera selectedCamera;

    private ButtonWidget editButton;
    private ButtonWidget tpToNearestButton;
    private boolean raycast = false;
    private ButtonWidget camerasButton;
    private int yMovement = 0;

    public MapScreen() {
        super(new TranslatableText("text.extracameras.title"));
    }

    protected void init() {
        super.init();
        this.addDrawableChild(new ButtonWidget(5, height - 28, 80, 20, new TranslatableText("text.extracameras.close"), (button) -> {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
            close();
        }));
        int buttons = 6;
        int xCount = 0;
        int yCount = 28;
        while (!checkForNewRow(xCount, 606)) {
            xCount += 101;
            if (buttons != 0) buttons--;
        }
        if (buttons != 0) {
            yCount += 24;
        }
        this.yMovement = height - yCount;
        int done = 6 - buttons;
        int xCountCopy = xCount;
        this.camerasButton = this.addDrawableChild(new ButtonWidget(width - xCount, height - yCount, 97, 20, new TranslatableText("text.extracameras.cameras"), (button) -> {
            client.setScreen(new CamerasListScreen());
        }));
        xCount -= 101;
        done--;
        if (done == 0) {
            yCount -= 24;
            xCount = xCountCopy;
        }
        this.addDrawableChild(new ButtonWidget(width - xCount, height - yCount, 97, 20, new TranslatableText("text.extracameras.options"), (button) -> {
            this.client.setScreen(new OptionsScreen(this));
        }));
        xCount -= 101;
        done--;
        if (done == 0) {
            yCount -= 24;
            xCount = xCountCopy;
        }
        this.tpToNearestButton = this.addDrawableChild(new ButtonWidget(width - xCount, height - yCount, 97, 20, new TranslatableText("text.extracameras.tp_to_nearest"), (button) -> {
            //TODO: ...
        }));
        xCount -= 101;
        done--;
        if (done == 0) {
            yCount -= 24;
            xCount = xCountCopy;
        }
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.raycast).build(width - xCount, height - yCount, 97, 20, new TranslatableText("text.extracameras.raycast"), (button, active) -> {
            this.raycast = active;
        }));
        xCount -= 101;
        done--;
        if (done == 0) {
            yCount -= 24;
            xCount = xCountCopy;
        }
        this.addDrawableChild(new ButtonWidget(width - xCount, height - yCount, 97, 20, new TranslatableText("text.extracameras.change_dimension"), (button) -> {
            //TODO: change dimension
        }));
        xCount -= 101;
        done--;
        if (done == 0) {
            yCount -= 24;
            xCount = xCountCopy;
        }
        this.editButton = this.addDrawableChild(new ButtonWidget(width - (xCount), height - yCount, 97, 20, new TranslatableText("text.extracameras.edit"), (button) -> {
            client.setScreen(new CreateCameraScreen(this, this.selectedCamera, true));
        }));
        if (Instance.currentWorldCameras.getCameras().isEmpty()) {
            this.camerasButton.active = false;
            this.tpToNearestButton.active = false;
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, (int) RenderUtils.getEquivalentPosition(referenceHeight, 8, height), new Color(255, 255, 255).getRGB());
        super.render(matrices, mouseX, mouseY, delta);
        fill(matrices, 0, (int) RenderUtils.getEquivalentPosition(referenceHeight, 20, height), width,this.yMovement - 7, Color.BLACK.getRGB());

        if (Instance.currentWorldCameras.getCameras().isEmpty()) {
            this.camerasButton.active = false;
            this.tpToNearestButton.active = false;
        }
        if (this.selectedCamera == null) {
            this.editButton.active = false;
        }
    }

    private boolean checkForNewRow(int x, int max) {
        return width - x - 97 <= 90 || x >= max;
    }

    private void close() {
        client.keyboard.setRepeatEvents(false);
        this.client.mouse.lockCursor();
    }

    @Override
    public void onClose() {
        super.onClose();
        close();
    }
}
