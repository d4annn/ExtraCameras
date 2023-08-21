package dan.extracameras.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.camera.Camera;
import dan.extracameras.config.Config;
import dan.extracameras.gui.CamerasListScreen;
import dan.extracameras.gui.CreateCameraScreen;
import dan.extracameras.objects.DoubleDuple;
import dan.extracameras.utils.Instance;
import dan.extracameras.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Map;

public class CameraListWidget extends ExtraCamerasEntryList<CameraListWidget.CameraEntry> {

    private final Identifier pinImageIdentifier = new Identifier("extracameras", "textures/pin_image.png");
    private ArrayList<CameraEntry> camerasFiltered;
    private CamerasListScreen gui;

    private String filter = "";

    public CameraListWidget(CamerasListScreen screen) {
        super(MinecraftClient.getInstance(), screen.width, screen.height, 40, screen.height - 86, (int) Config.getInstance().entryHeight);
        this.gui = screen;
        this.camerasFiltered = new ArrayList<>();
        for (Camera camera : Instance.currentWorldCameras.getCameras()) {
            this.camerasFiltered.add(new CameraEntry(gui, camera));
        }
        this.camerasFiltered.forEach(this::addEntry);
    }

    public void updateEntries() {
        clearEntries();
        this.camerasFiltered.clear();
        for (Camera camera : Instance.currentWorldCameras.getCameras()) {
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
            if (cameraEntry.getCamera().isPinned()) {
                finalList.add(cameraEntry);
            }
        }
        for (CameraEntry cameraEntry : this.camerasFiltered) {
            if (!finalList.contains(cameraEntry)) {
                finalList.add(cameraEntry);
            }
        }
        this.camerasFiltered = finalList;
    }

    @Override
    public int getRowWidth() {
        return (int) Config.getInstance().entryWidth;
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
            byte spaceDifference = 3;
            if (Config.getInstance().showCoords) {
                if (mouseX >= x - spaceDifference && mouseY >= y && mouseX <= x + 215 + spaceDifference && mouseY <= y + entryHeight) {
                    String tooltip = "X:" + this.camera.getPos().getX() + " Y:" + this.camera.getPos().getY() + " Z:" + this.camera.getPos().getZ();
                    if (mouseX >= CameraListWidget.this.left && mouseX <= CameraListWidget.this.right && mouseY >= CameraListWidget.this.top && mouseY <= CameraListWidget.this.bottom) {
                        this.gui.setTooltip(Text.of(tooltip));
                    }
                }
            }
            if (this.getCamera().isPinned()) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, pinImageIdentifier);
                RenderSystem.texParameter(3553, 10241, 9728);
                RenderUtils.renderImage(matrices.peek().getPositionMatrix(), (float) ((x) + Config.getInstance().pinnedPosition.getX()), (float) (y + Config.getInstance().pinnedPosition.getY()), 0, 0, 12, 12, 12, 12, 12, 12, 1);
            }
            DoubleDuple name = Config.getInstance().namePosition;
            if (name.getX() != -1 && name.getY() != -1) {
                client.textRenderer.draw(matrices, camera.getName(), (float) ((x) + name.getX() - client.textRenderer.getWidth(camera.getName()) / 2), (float) (y + name.getY()), camera.getColor());
            }
            DoubleDuple dimension = Config.getInstance().dimensionPosition;
            if (dimension.getX() != -1 && dimension.getY() != -1) {
                client.textRenderer.draw(matrices, camera.getDimension(), (float) ((x) + dimension.getX() - client.textRenderer.getWidth(camera.getDimension()) / 2), (float) (y + dimension.getY()), camera.getColor());
            }
            DoubleDuple position = Config.getInstance().posPosition;
            if (position.getX() != -1 && position.getY() != -1) {
                String tooltip = "X:" + this.camera.getPos().getX() + " Y:" + this.camera.getPos().getY() + " Z:" + this.camera.getPos().getZ();
                client.textRenderer.draw(matrices, tooltip, (float) ((x) + position.getX() - client.textRenderer.getWidth(tooltip) / 2), (float) (y + position.getY()), camera.getColor());
            }
            DoubleDuple active = Config.getInstance().activePosition;
            if (active.getX() != -1 && active.getY() != -1) {
                client.textRenderer.draw(matrices, String.valueOf(camera.isActive()), (float) ((x) + active.getX() - client.textRenderer.getWidth(String.valueOf(camera.isActive())) / 2), (float) (y + active.getY()), camera.getColor());
            }
            DoubleDuple image = Config.getInstance().imagePlaced;
            if (image.getX() != -1 && image.getY() != -1) {

                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                String world = Instance.currentWorldCameras.getWorld();
                Identifier identifier = null;
                if (Instance.images.containsKey(world)) {
                    Map<String, Identifier> worldImages = Instance.images.get(world);
                    if (worldImages.containsKey(camera.getName())) {
                        identifier = worldImages.get(camera.getName());
                    }
                }
                if (identifier == null) identifier = RenderUtils.NO_IMAGE;
                RenderSystem.setShaderTexture(0, identifier);
                RenderSystem.texParameter(3553, 10241, 9728);
                RenderUtils.renderImage(matrices.peek().getPositionMatrix(), (float) ((x) + image.getX()), (float) (y + image.getY()), 0, 0, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, 1);
            }


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
