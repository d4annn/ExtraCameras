package dan.extracameras.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.camera.Camera;
import dan.extracameras.config.Config;
import dan.extracameras.gui.CamerasListScreen;
import dan.extracameras.gui.CreateCameraScreen;
import dan.extracameras.utils.RenderUtils;
import dan.extracameras.utils.Variables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CameraListWidget extends ExtraCamerasEntryList<CameraListWidget.CameraEntry> {

    private ArrayList<CameraEntry> camerasFiltered;
    private final Identifier pinImageIdentifier = new Identifier("extracameras", "textures/pin_image.png");
    private CamerasListScreen gui;

    private String filter = "";

    public CameraListWidget(CamerasListScreen screen) {
        super(MinecraftClient.getInstance(), screen.width, screen.height, 40, screen.height - 86, 18);
        this.gui = screen;
        this.camerasFiltered = new ArrayList<>();
        for (Camera camera : Variables.currentWorldCameras.getCameras()) {
            this.camerasFiltered.add(new CameraEntry(gui, camera));
        }
        this.camerasFiltered.forEach(this::addEntry);
    }

    public void updateEntries() {
        clearEntries();
        this.camerasFiltered.clear();
        for (Camera camera : Variables.currentWorldCameras.getCameras()) {
            this.camerasFiltered.add(new CameraEntry(gui, camera));
        }
        reOrderPinned();
        this.camerasFiltered.forEach(cameraEntry -> {
            if (!this.filter.isEmpty()) {
                if (cameraEntry.camera.getName().contains(this.filter)) {
                    this.addEntry(cameraEntry);
                }
            } else {
                this.addEntry(cameraEntry);
            }
        });
    }

    private void reOrderPinned() {
        ArrayList<CameraEntry> finalList = new ArrayList<>();
        for (CameraEntry cameraEntry : this.camerasFiltered) {
            if(cameraEntry.getCamera().isPinned()) {
                finalList.add(cameraEntry);
            }
        }
        for (CameraEntry cameraEntry : this.camerasFiltered) {
            if(!finalList.contains(cameraEntry)) {
                finalList.add(cameraEntry);
            }
        }
        this.camerasFiltered = finalList;
    }

    @Override
    public int getRowWidth() {
        return (int) Variables.CamerasListVariables.entryWidth;
    }

    public void updateFilter(String filter) {
        this.filter = filter;
        updateEntries();
    }

    public void deleteEntries() {
        clearEntries();
    }

    public void setSelected(CameraEntry camera) {
        super.setSelected(camera);
        if (getSelectedOrNull() instanceof CameraEntry) {
            NarratorManager.INSTANCE.narrate((new TranslatableText("narrator.select", new Object[]{((CameraEntry) getSelectedOrNull())})));
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public class CameraEntry extends EntryListWidget.Entry<CameraEntry> {

        private final Camera camera;

        private final CamerasListScreen gui;

        protected CameraEntry(CamerasListScreen gui, Camera camera) {
            this.camera = camera;
            this.gui = gui;
        }

        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            //Minecraft is a shit , DrawableHelper uses 0-1 color values textrenderer uses 0-255 , fantastic idea mojang
            drawStringWithShadow(matrices, client.textRenderer, camera.getName(), this.gui.width / 2, y + 3, camera.getColor());
            byte spaceDifference = 3;
            if (mouseX >= x - spaceDifference && mouseY >= y && mouseX <= x + 215 + spaceDifference && mouseY <= y + entryHeight) {
                String tooltip = "X:" + this.camera.getPos().getX() + " Y:" + this.camera.getPos().getY() + " Z:" + this.camera.getPos().getZ();
                if (mouseX >= CameraListWidget.this.left && mouseX <= CameraListWidget.this.right && mouseY >= CameraListWidget.this.top && mouseY <= CameraListWidget.this.bottom) {
                    this.gui.setTooltip(Text.of(tooltip));
                }
            }
            if(this.getCamera().isPinned()) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, pinImageIdentifier);
                RenderSystem.texParameter(3553, 10241, 9728);
                drawTexture(matrices, x + 5, y + 1, 0, 0, 12, 12, 12, 12);
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            CameraListWidget.this.setSelected(this);
            this.gui.setSelectedCamera(this.camera);
            int leftEdge = this.gui.width / 2 - 92 - 16;
            byte spaceDifference = 3;
            int width = 215;
            if (CameraListWidget.this.doubleClick) {
                client.setScreen(new CreateCameraScreen(this.gui, this.camera, true));
            }
            return true;
        }

        public Camera getCamera() {
            return camera;
        }
    }
}
