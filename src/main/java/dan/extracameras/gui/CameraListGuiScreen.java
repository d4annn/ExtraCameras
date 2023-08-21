package dan.extracameras.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.config.Config;
import dan.extracameras.objects.DoubleDuple;
import dan.extracameras.objects.GuiElement;
import dan.extracameras.utils.I18nUtils;
import dan.extracameras.utils.Options;
import dan.extracameras.utils.RenderUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraListGuiScreen extends Screen {

    private Map<String, GuiElement> elements;
    private Screen gui;
    private DoubleOptionSliderWidget entryHeightSlider;
    private DoubleOptionSliderWidget previewHeightSlider;
    private DoubleOptionSliderWidget entryWidthSlider;
    private ButtonWidget tutorialButton;
    private ButtonWidget restoreButton;
    private CyclingButtonWidget allPreviewButton;
    private List<Box> boxes;
    private Box imageBox;
    private List<Box> pinBoxes;

    public CameraListGuiScreen(Screen parent) {
        super(new TranslatableText("text.extracameras.camera_list_gui"));
        this.gui = parent;
    }

    public void init() {
        boxes = new ArrayList<>();
        pinBoxes = new ArrayList<>();
        client.keyboard.setRepeatEvents(true);
        this.addDrawableChild(new ButtonWidget(5, height - 28, 74, 20, new TranslatableText("text.extracameras.done"), button -> {
            onClose();
        }));
        this.tutorialButton = this.addDrawableChild(new ButtonWidget(5, height - 52, 74, 20, new TranslatableText("text.extracameras.tutorial"), button -> {
            //TODO: tutorial
        }));
        this.restoreButton = this.addDrawableChild(new ButtonWidget(5, height - 76, 74, 20, new TranslatableText("text.extracameras.restore"), button -> {
            resetAll();
        }));
        this.entryHeightSlider = this.addDrawableChild(new DoubleOptionSliderWidget(client.options, 100, this.height - 52, 135, 20, Options.CAMERA_LIST_ENTRY_HEIGHT, new ArrayList<>()));
        this.previewHeightSlider = this.addDrawableChild(new DoubleOptionSliderWidget(client.options, 100, this.height - 28, 135, 20, Options.CAMERA_LIST_PREVIEW_HEIGHT, new ArrayList<>()));
        this.entryWidthSlider = this.addDrawableChild(new DoubleOptionSliderWidget(client.options, 100, this.height - 76, 135, 20, Options.CAMERA_LIST_ENTRY_WIDTH, new ArrayList<>()));
        this.allPreviewButton = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(Config.getInstance().showAll).build(this.width - 141, this.height - 76, 135, 20, new TranslatableText("text.extracameras.preview_on"), (button, active) -> Config.getInstance().showAll = active));

        elements = new HashMap<>();
        GuiElement image = new GuiElement(250, this.height - 42 - (int) Config.getInstance().previewSize / 2, false, false, true);
        GuiElement name = new GuiElement(image.getX() + 80, this.height - 42 - textRenderer.fontHeight / 2, false, false, true);
        GuiElement pos = new GuiElement(name.getX() + textRenderer.getWidth("Name") + 15, name.getY(), false, false, true);
        GuiElement dimension = new GuiElement(pos.getX() + textRenderer.getWidth("Position") + 15, name.getY(), false, false, true);
        GuiElement active = new GuiElement(dimension.getX() + textRenderer.getWidth("Dimension") + 15, name.getY(), false, false, true);
        GuiElement pin = new GuiElement(active.getX() + textRenderer.getWidth("Active") + 15, name.getY(), false, false, true);

        if (Config.getInstance().previewSize <= Config.getInstance().entryHeight && checkPoint(Config.getInstance().imagePlaced)) {

            image.setPlaced(true);
            image.setX(this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().imagePlaced.getX());
            image.setY(44 + Config.getInstance().imagePlaced.getY());
        }
        elements.put("image", image);

        if (checkPoint(Config.getInstance().namePosition)) {

            name.setPlaced(true);
            name.setX(this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().namePosition.getX() - textRenderer.getWidth("Name") / 2);
            name.setY(44 + Config.getInstance().namePosition.getY());
        }
        elements.put("name", name);

        if (checkPoint(Config.getInstance().posPosition)) {

            pos.setPlaced(true);
            pos.setX(this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().posPosition.getX() - textRenderer.getWidth("Position") / 2);
            pos.setY(44 + Config.getInstance().posPosition.getY());
        }
        elements.put("Position", pos);

        if (checkPoint(Config.getInstance().dimensionPosition)) {

            dimension.setPlaced(true);
            dimension.setX(this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().dimensionPosition.getX() - textRenderer.getWidth("Dimension") / 2);
            dimension.setY(44 + Config.getInstance().dimensionPosition.getY());
        }
        elements.put("dimension", dimension);

        if (checkPoint(Config.getInstance().activePosition)) {

            active.setPlaced(true);
            active.setX(this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().activePosition.getX() - textRenderer.getWidth("Active") / 2);
            active.setY(44 + Config.getInstance().activePosition.getY());
        }
        elements.put("active", active);

        if (checkPoint(Config.getInstance().pinnedPosition)) {

            pin.setPlaced(true);
            pin.setX(this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().pinnedPosition.getX());
            pin.setY(44 + Config.getInstance().pinnedPosition.getY());
        }
        elements.put("pin", pin);
    }

    public boolean checkPoint(DoubleDuple point) {
        return point.getX() != -1 && point.getY() != -1;
    }

    @Override
    public void tick() {
        super.tick();

        if ((!elements.get("image").isPlaced() && !elements.get("image").isHold()))
            elements.get("image").setY((float) (this.height - 42 - Config.getInstance().previewSize / 2));
        if (Config.getInstance().previewSize > Config.getInstance().entryHeight) {
            elements.get("image").setPlaced(false);
            elements.get("image").setDef();
        }
        for (String s : elements.keySet()) {
            GuiElement element = elements.get(s);
            if (element.isPlaced()) {
                if (Config.getInstance().showAll) {
                    if (Config.getInstance().entryWidth < 230) break;

                    DoubleDuple vals = getByName(s);
                    if (vals == null) continue;
                    boolean isImage = s.equals("image");
                    double xValue = (this.width / 2 - Config.getInstance().entryWidth / 2) + vals.getX();
                    if (!isImage && !s.equals("pin")) xValue -= textRenderer.getWidth(s) / 2;
                    element.setX(xValue);
                    element.setY(44 + vals.getY());
                } else {
                    double mainX = this.width / 2 - Config.getInstance().entryWidth / 2;
                    double mainY = 44;
                    double mainX2 = (this.width / 2 - Config.getInstance().entryWidth / 2) + Config.getInstance().entryWidth;
                    double mainY2 = 44 + Config.getInstance().entryHeight;
                    DoubleDuple botRight;
                    if (s.equals("image")) {
                        botRight = new DoubleDuple(element.getX() + Config.getInstance().previewSize, element.getY() + Config.getInstance().previewSize);
                    } else if (s.equals("pin")) {
                        botRight = new DoubleDuple(element.getX() + 12, element.getY() + 12);
                        mainX -= 50;
                        mainX2 += 50;
                    } else {
                        botRight = new DoubleDuple(element.getX() + textRenderer.getWidth(s), element.getY() + textRenderer.fontHeight);
                    }
                    if (!(element.getX() >= mainX && element.getY() >= mainY && botRight.getX() <= mainX2 && botRight.getY() <= mainY2)) {
                        element.setDef();
                        setDefaultByName(s);
                    }
                }

            }
        }

    }

    public DoubleDuple getByName(String name) {
        switch (name) {
            case "image":
                return Config.getInstance().imagePlaced;
            case "name":
                return Config.getInstance().namePosition;
            case "dimension":
                return Config.getInstance().dimensionPosition;
            case "active":
                return Config.getInstance().activePosition;
            case "Position":
                return Config.getInstance().posPosition;
            case "pin":
                return Config.getInstance().pinnedPosition;
            default:
                return null;
        }

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (String s : elements.keySet()) {
            GuiElement element = elements.get(s);
            if (element.isHold()) {
                int multiplier = 25;
                if (!s.equals("image")) multiplier = textRenderer.getWidth(s) / 2;
                element.setX(mouseX - multiplier);
                element.setY(mouseY - 10);
            }
        }
        super.mouseMoved(mouseX, mouseY);
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
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, RenderUtils.NO_IMAGE);
            RenderSystem.texParameter(3553, 10241, 9728);

            GuiElement image = elements.get("image");
            RenderUtils.renderImage(matrices.peek().getPositionMatrix(), (float) image.getX(), (float) image.getY(), 0, 0, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, (int) Config.getInstance().previewSize, 1);

            GuiElement name = elements.get("name");
            textRenderer.draw(matrices, "Name", (float) name.getX(), (float) name.getY(), -1);

            GuiElement pos = elements.get("Position");
            textRenderer.draw(matrices, "Position", (float) pos.getX(), (float) pos.getY(), -1);

            GuiElement dim = elements.get("dimension");
            textRenderer.draw(matrices, "Dimension", (float) dim.getX(), (float) dim.getY(), -1);

            GuiElement active = elements.get("active");
            textRenderer.draw(matrices, "Active", (float) active.getX(), (float) active.getY(), -1);

            GuiElement pin = elements.get("pin");
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, new Identifier("extracameras", "textures/pin_image.png"));
            RenderSystem.texParameter(3553, 10241, 9728);
            RenderUtils.renderImage(matrices.peek().getPositionMatrix(), (float) pin.getX(), (float) pin.getY(), 0, 0, 12, 12, 12, 12, 12, 12, 1);

            RenderSystem.lineWidth(5);
            RenderUtils.renderEmptyQuad(matrices, RenderUtils.RGBAToInt(255, 255, 255, 255), this.width / 2 - Config.getInstance().entryWidth / 2, 44, (this.width / 2 - Config.getInstance().entryWidth / 2) + Config.getInstance().entryWidth, 44 + Config.getInstance().entryHeight, 1.7);

            if (Config.getInstance().showAll) {
                this.boxes.clear();
                if (Config.getInstance().previewSize <= Config.getInstance().entryHeight) {
                    this.imageBox = RenderUtils.renderEmptyQuad(matrices, RenderUtils.RGBAToInt(255, 255, 255, 200), this.width / 2 - Config.getInstance().entryWidth / 2 + 1.7, 46.7, this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize, 41.3 + Config.getInstance().entryHeight, 0.8);
                }
                //up and down
                boolean space = Config.getInstance().entryHeight >= 45;
                boolean doubleQuad = Config.getInstance().entryWidth >= 230;

                double factor = Config.getInstance().previewSize <= Config.getInstance().entryHeight ? 0 : Config.getInstance().previewSize;
                double totalSpaceW = (this.width / 2 - Config.getInstance().entryWidth / 2) + Config.getInstance().entryWidth - 1.7 - (this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize - factor);
                double width = (totalSpaceW - 6) / 2;
                double totalSpaceH = 44 + Config.getInstance().entryHeight - 1.7 - (44 + 1.7);
                double height = (totalSpaceH - 6) / 2;
                this.boxes.add(RenderUtils.renderEmptyQuad(matrices, RenderUtils.RGBAToInt(255, 255, 255, 200), doubleQuad ? this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + 2 - factor : this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + totalSpaceW / 2 - 60 - factor, space ? 47.7 : 47.5, doubleQuad ? (this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + 2.7) + width - factor : this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + totalSpaceW / 2 + 60 - factor, space ? 47.7 + height : 44 + Config.getInstance().entryHeight - 3.7, 0.8));
                if (space)
                    this.boxes.add(RenderUtils.renderEmptyQuad(matrices, RenderUtils.RGBAToInt(255, 255, 255, 200), doubleQuad ? this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + 2 - factor : this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + totalSpaceW / 2 - 60 - factor, 47.7 + height + 2, doubleQuad ? (this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + 2.7) + width - factor : this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + totalSpaceW / 2 + 60 - factor, 47.7 + height + 2 + height, 0.8));
                if (doubleQuad) {
                    this.boxes.add(RenderUtils.renderEmptyQuad(matrices, RenderUtils.RGBAToInt(255, 255, 255, 200), this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + 2.7 + width + 2 - factor, space ? 47.7 : 47.5, (this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + 2.7) + width * 2 + 2 - factor, space ? 47.7 + height : 44 + Config.getInstance().entryHeight - 3.7, 0.8));
                    if (space)
                        this.boxes.add(RenderUtils.renderEmptyQuad(matrices, RenderUtils.RGBAToInt(255, 255, 255, 200), this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + 2.7 + width + 2 - factor, 47.7 + height + 2, (this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().previewSize + 2.7) + width * 2 + 2 - factor, 47.7 + height + 2 + height, 0.8));
                }
                this.pinBoxes.add(RenderUtils.renderEmptyQuad(matrices, RenderUtils.RGBAToInt(255, 255, 255, 200), this.width / 2 - Config.getInstance().entryWidth / 2 - 13, 44 + Config.getInstance().entryHeight / 2 - 6, this.width / 2 - Config.getInstance().entryWidth / 2 - 1, 44 + Config.getInstance().entryHeight / 2 + 6, 0.8));
                this.pinBoxes.add(RenderUtils.renderEmptyQuad(matrices, RenderUtils.RGBAToInt(255, 255, 255, 200), this.width / 2 + Config.getInstance().entryWidth / 2 + 1, 44 + Config.getInstance().entryHeight / 2 - 6, this.width / 2 + Config.getInstance().entryWidth / 2 + 13, 44 + Config.getInstance().entryHeight / 2 + 6, 0.8));

            }
        }
    }

    private void setPosByName(String name, DoubleDuple pos) {
        switch (name) {
            case "image":
                Config.getInstance().imagePlaced = pos;
                break;
            case "name":
                Config.getInstance().namePosition = pos;
                break;
            case "dimension":
                Config.getInstance().dimensionPosition = pos;
                break;
            case "active":
                Config.getInstance().activePosition = pos;
                break;
            case "Position":
                Config.getInstance().posPosition = pos;
                break;
            case "pin":
                Config.getInstance().pinnedPosition = pos;
                break;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (Config.getInstance().showAll) {
            GuiElement image = elements.get("image");
            if (image == null) return false;
            boolean placed = false;
            if (imageBox != null && isMouseOver(mouseX, mouseY, imageBox.minX, imageBox.minY, imageBox.maxX, imageBox.maxY) && image.isHold()) {
                image.setPlaced(true);
                image.setHold(false);
                Config.getInstance().imagePlaced = new DoubleDuple(1, 1);
                image.setX(this.width / 2 - Config.getInstance().entryWidth / 2 + Config.getInstance().imagePlaced.getX());
                image.setY(44 + Config.getInstance().imagePlaced.getY());
                return true;
            }
            GuiElement pin = elements.get("pin");
            if (!pinBoxes.isEmpty() && pin.isHold()) {
                for (Box box : pinBoxes) {
                    if (isMouseOver(mouseX, mouseY, box.minX, box.minY, box.maxX, box.maxY)) {
                        pin.setPlaced(true);
                        pin.setHold(false);
                        Config.getInstance().pinnedPosition = new DoubleDuple(box.minX - (this.width / 2 - Config.getInstance().entryWidth / 2), box.minY - 44);
                        break;
                    }
                }
            }
            for (String s : elements.keySet()) {
                GuiElement element = elements.get(s);
                if (s.equals("image")) {
                    if (element.isHold()) {
                        element.setDef();
                        element.setHold(false);
                        Config.getInstance().imagePlaced = new DoubleDuple(-1, -1);
                    }
                    continue;
                } else if (s.equals("pin")) {
                    if (element.isHold()) {
                        element.setDef();
                        element.setHold(false);
                        Config.getInstance().pinnedPosition = new DoubleDuple(-1, -1);
                    }
                    continue;
                }
                for (Box box : boxes) {
                    if (isMouseOver(mouseX, mouseY, box.minX, box.minY, box.maxX, box.maxY) && element.isHold()) {
                        element.setPlaced(true);
                        DoubleDuple point = new DoubleDuple((box.minX + ((box.maxX - box.minX) / 2)) - (this.width / 2 - Config.getInstance().entryWidth / 2), box.minY + ((box.maxY - box.minY) / 2) - 44 - textRenderer.fontHeight / 2);
                        setPosByName(s, point);
                        element.setX(this.width / 2 - Config.getInstance().entryWidth / 2 + point.getX() - textRenderer.getWidth(s) / 2);
                        element.setY(44 + point.getY());
                        placed = true;
                    }
                }
                element.setHold(false);
                if (!placed && !element.isPlaced()) {
                    element.setDef();
                    setDefaultByName(s);
                }
            }
        } else {
            for (String s : elements.keySet()) {
                GuiElement element = elements.get(s);
                //check if its inside
                if (element.isHold()) {
                    element.setHold(false);
                    DoubleDuple topLeft = new DoubleDuple(element.getX(), element.getY());
                    DoubleDuple botRight;
                    if (s.equals("image")) {
                        botRight = new DoubleDuple(element.getX() + Config.getInstance().previewSize, element.getY() + Config.getInstance().previewSize);
                    } else {
                        botRight = new DoubleDuple(element.getX() + textRenderer.getWidth(s), element.getY() + textRenderer.fontHeight);
                    }
                    double mainX = this.width / 2 - Config.getInstance().entryWidth / 2;
                    double mainY = 44;
                    double mainX2 = (this.width / 2 - Config.getInstance().entryWidth / 2) + Config.getInstance().entryWidth;
                    double mainY2 = 44 + Config.getInstance().entryHeight;
                    if (s.equals("pin")) {
                        botRight = new DoubleDuple(element.getX() + 12, element.getY() + 12);
                        mainX -= 50;
                        mainX2 += 50;
                    }
                    if (topLeft.getX() >= mainX && topLeft.getY() >= mainY && botRight.getX() <= mainX2 && botRight.getY() <= mainY2) {
                        int posDif = 0;
                        if (!s.equals("image") && !s.equals("pin")) posDif = textRenderer.getWidth(s) / 2;
                        setPosByName(s, new DoubleDuple(topLeft.getX() - (this.width / 2 - Config.getInstance().entryWidth / 2) + posDif, topLeft.getY() - 44));
                        element.setPlaced(true);
                        element.setX(topLeft.getX());
                        element.setY(topLeft.getY());
                    } else {
                        if (!element.isPlaced())
                            setDefaultByName(s);
                        element.setDef();
                    }
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void setDefaultByName(String name) {
        switch (name) {
            case "image":
                Config.getInstance().imagePlaced = new DoubleDuple(-1, -1);
                break;
            case "name":
                Config.getInstance().namePosition = new DoubleDuple(-1, -1);
                break;
            case "dimension":
                Config.getInstance().dimensionPosition = new DoubleDuple(-1, -1);
                break;
            case "active":
                Config.getInstance().activePosition = new DoubleDuple(-1, -1);
                break;
            case "Position":
                Config.getInstance().posPosition = new DoubleDuple(-1, -1);
                break;
            case "pin":
                Config.getInstance().pinnedPosition = new DoubleDuple(-1, -1);
                break;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_1) return false;
        GuiElement image = elements.get("image");
        if (isMouseOver(mouseX, mouseY, image.getX(), image.getY(), image.getX() + Config.getInstance().previewSize, image.getY() + Config.getInstance().previewSize)) {
            image.setPlaced(false);
            image.setHold(true);
        }
        for (String s : elements.keySet()) {
            if (s.equals("image")) continue;
            GuiElement element = elements.get(s);
            if (isMouseOver(mouseX, mouseY, element.getX() - 3, element.getY() - 3, element.getX() + textRenderer.getWidth(s) + 3, 3 + element.getY() + textRenderer.fontHeight)) {
                element.setPlaced(false);
                element.setHold(true);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean isMouseOver(double mouseX, double mouseY, double x, double y, double x2, double y2) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    @Override
    public void onClose() {
        this.client.setScreen(gui);
        Config.getInstance().saveConfig();
    }

    public void reset() {
        for (GuiElement element : elements.values()) {
            element.setDef();
        }
        Config.getInstance().namePosition = new DoubleDuple(-1, -1);
        Config.getInstance().activePosition = new DoubleDuple(-1, -1);
        Config.getInstance().dimensionPosition = new DoubleDuple(-1, -1);
        Config.getInstance().posPosition = new DoubleDuple(-1, -1);
        Config.getInstance().imagePlaced = new DoubleDuple(-1, -1);
        ;
    }

    public void resetAll() {
        reset();
        Options.CAMERA_LIST_ENTRY_HEIGHT.set(client.options, 26);
        Options.CAMERA_LIST_ENTRY_WIDTH.set(client.options, 220);
        Options.CAMERA_LIST_PREVIEW_HEIGHT.set(client.options, 50);
        Config.getInstance().entryHeight = 26;
        Config.getInstance().entryWidth = 220;
        Config.getInstance().previewSize = 50;
        Config.getInstance().namePosition = new DoubleDuple(110, 13 - textRenderer.fontHeight / 2);
        client.setScreen(new CameraListGuiScreen(gui));
    }
}
