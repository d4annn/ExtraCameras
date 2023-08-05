package dan.extracameras.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.camera.Camera;
import dan.extracameras.config.Config;
import dan.extracameras.gui.widgets.CustomButtonWidget;
import dan.extracameras.gui.widgets.CustomCheckBoxWidget;
import dan.extracameras.gui.widgets.CustomTextFieldWidget;
import dan.extracameras.utils.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreateCameraScreen extends Screen {

    private TextFieldWidget cameraNameButton;
    private CustomTextFieldWidget xButton;
    private CustomTextFieldWidget yButton;
    private CustomTextFieldWidget zButton;
    private ButtonWidget addButton;
    private Dimensions dimension;
    private boolean active = true;
    private boolean visuals = true;
    private final ErrorUtils.CreateError createError;
    private CustomCheckBoxWidget overworld;
    private CustomCheckBoxWidget nether;
    private CustomCheckBoxWidget end;
    private Dimensions last;
    private final Identifier colorPaletteTexture = new Identifier("extracameras", "textures/paleta.png");
    boolean colorPalette = false;
    private float red;
    private float blue;
    private float green;
    private boolean editing;

    //If minecraft code was well done this will be unnecessary
    private int activeTimer = 0;
    private int visualsTimer = 0;

    private Screen parent;
    private Camera camera;

    public CreateCameraScreen(Screen parent, Camera camera, boolean editing) {
        super(new TranslatableText("text.extracameras.create_camera"));
        Window window = MinecraftClient.getInstance().getWindow();
        this.createError = new ErrorUtils.CreateError(false, "", width / 2 - 155, height / 6 + 150);
        this.dimension = Dimensions.getCurrent();
        this.last = Dimensions.getCurrent();
        this.parent = parent;
        this.camera = camera;
        this.editing = editing;
    }

    protected void init() {
        super.init();
        assert this.client != null;
        this.client.keyboard.setRepeatEvents(true);
        this.createError.setPos(width / 2 - 153, height / 6 + 160);

        List<Text> supplyInfo = new ArrayList<>();
        this.cameraNameButton = this.addDrawableChild(new TextFieldWidget(textRenderer, width / 2 - 100, height / 6 + 13, 200, 20, null));
        this.xButton = this.addDrawableChild(new CustomTextFieldWidget(textRenderer, width / 2 - 100, height / 6 + 41 + 13, 56, 20, null, "[0-9-]"));
        this.xButton.setMaxLength(128);
        this.xButton.setChangedListener(this::suggestX);
        assert client.player != null;
        this.xButton.setText("" + (int) client.player.getX());
        this.yButton = this.addDrawableChild(new CustomTextFieldWidget(textRenderer, width / 2 - 28, height / 6 + 41 + 13, 56, 20, null, "[0-9-]"));
        this.yButton.setMaxLength(128);
        this.yButton.setText("" + (int) client.player.getY());
        this.yButton.setChangedListener(this::suggestY);
        this.zButton = this.addDrawableChild(new CustomTextFieldWidget(textRenderer, width / 2 + 44, height / 6 + 41 + 13, 56, 20, null, "[0-9-]"));
        this.zButton.setMaxLength(128);
        this.zButton.setText("" + (int) client.player.getZ());
        this.zButton.setChangedListener(this::suggestZ);

        int buttonsY = height / 6 + 82 + 6;
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.active).build(width / 2 - 101, buttonsY, 100, 20, new TranslatableText("text.extracameras.camera_active"), (button, active) -> this.active = active));
        supplyInfo.add(Text.of("The color of the visuals"));
        supplyInfo.add(Text.of("Needs visuals on to work"));
        this.addDrawableChild(new CustomButtonWidget(width / 2 - 101, buttonsY + 24, 100, 20, new TranslatableText("text.extracameras.color"), button -> this.colorPalette = true, supplyInfo));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.visuals).build(width / 2 - 101, buttonsY + 48, 100, 20, new TranslatableText("text.extracameras.visuals_active"), (button, active) -> this.visuals = active));
        TranslatableText addKey = editing ? new TranslatableText("text.extracameras.confirm") : new TranslatableText("text.extracameras.add");
        this.addButton = this.addDrawableChild(new ButtonWidget(width / 2 - 155, height / 6 + 168, 150, 20, addKey, button -> {
            Camera newCamera = new Camera(createBlockPos(), this.cameraNameButton.getText().trim(), this.dimension, this.active, this.visuals, this.red, this.green, this.blue);
            if (this.editing) {
                Instance.currentWorldCameras.changeCamera(Instance.currentWorldCameras.getCameras().indexOf(this.camera), newCamera);
                Config.getInstance().changeWorldCamerasConfig(Instance.currentWorldCameras);
                Config.getInstance().saveConfig();
            } else {
                newCamera.add();
            }
            close();
        }));
        this.addDrawableChild(new ButtonWidget(width / 2 + 5, height / 6 + 168, 150, 20, new TranslatableText("text.extracameras.cancel"), button -> close()));
        if (camera == null) {
            this.addButton.active = false;
            Random rnd = new Random();
            int color = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat()).getRGB();
            this.red = (color >> 16 & 0xFF) / 255.0F;
            this.blue = (color >> 8 & 0xFF) / 255.0F;
            this.green = (color >> 0 & 0xFF) / 255.0F;
        } else {
            this.cameraNameButton.setText(camera.getName());
            this.xButton.setText(String.valueOf(camera.getPos().getX()));
            this.yButton.setText(String.valueOf(camera.getPos().getY()));
            this.zButton.setText(String.valueOf(camera.getPos().getZ()));
            this.red = camera.getRed();
            this.blue = camera.getBlue();
            this.green = camera.getGreen();
            this.dimension = Dimensions.getByName(camera.getDimension());
            this.visuals = camera.isVisible();
        }
        int BUTTON_WIDTH = 84;
        boolean overworld = this.dimension == Dimensions.OVERWORLD;
        boolean nether = this.dimension == Dimensions.NETHER;
        boolean end = this.dimension == Dimensions.END;
        List<Text> supplyInfo1 = new ArrayList<>();
        supplyInfo1.add(Text.of("The dimension where the"));
        supplyInfo1.add(Text.of("camera will be added"));
        this.overworld = this.addDrawableChild(new CustomCheckBoxWidget(width / 2 + BUTTON_WIDTH / 2 - 15, buttonsY, 20, 20, Text.of("Overworld"), overworld, overw -> {
            if (this.nether.isChecked()) this.nether.onPress();
            if (this.end.isChecked()) this.end.onPress();
            this.last = Dimensions.OVERWORLD;
            this.dimension = Dimensions.OVERWORLD;
        }, supplyInfo1));
        this.nether = this.addDrawableChild(new CustomCheckBoxWidget(width / 2 + BUTTON_WIDTH / 2 - 15, buttonsY + 24, 20, 20, Text.of("Nether"), nether, neth -> {
            if (this.overworld.isChecked()) this.overworld.onPress();
            if (this.end.isChecked()) this.end.onPress();
            this.dimension = Dimensions.NETHER;
            this.last = Dimensions.NETHER;
        }, supplyInfo));
        this.end = this.addDrawableChild(new CustomCheckBoxWidget(width / 2 + BUTTON_WIDTH / 2 - 15, buttonsY + 48, 20, 20, Text.of("End"), end, endw -> {
            if (this.overworld.isChecked()) this.overworld.onPress();
            if (this.nether.isChecked()) this.nether.onPress();
            this.dimension = Dimensions.END;
            this.last = Dimensions.END;
        }, supplyInfo));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        //Tooltip
        int buttonsY = height / 6 + 82 + 6 + 7;
        int GRAY = 10526880;
        drawStringWithShadow(matrices, textRenderer, I18nUtils.getString("text.extracameras.camera_name", new Object[0]), width / 2 - 100, height / 6 + 0, GRAY);
        drawStringWithShadow(matrices, textRenderer, I18nUtils.getString("text.extracameras.x", new Object[0]), width / 2 - 100, height / 6 + 41, GRAY);
        drawStringWithShadow(matrices, textRenderer, I18nUtils.getString("text.extracameras.y", new Object[0]), width / 2 - 28, height / 6 + 41, GRAY);
        drawStringWithShadow(matrices, textRenderer, I18nUtils.getString("text.extracameras.z", new Object[0]), width / 2 + 44, height / 6 + 41, GRAY);

        RenderSystem.setShaderColor(this.red, this.green, this.blue, 1.0f);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, RenderUtils.WHITE);
        drawTexture(matrices, width / 2 - 25, buttonsY + 22, 0, 0, 16, 10);

        BlockPos pos;
        boolean active = true;
        if ((pos = createBlockPos()) == null) {
            active = false;
            this.createError.setError("Provide a coordinate");
        } else {
            for (Camera camera1 : Instance.currentWorldCameras.getCameras()) {
                if (camera1.getPos().equals(pos) && camera1.getDimension().equals(this.dimension.getName().getString())) {
                    if (this.camera == null) {
                        active = false;
                        this.createError.setError("A camera already exists at this coordinates");
                    } else if (!this.camera.getPos().equals(camera1.getPos())) {
                        active = false;
                        this.createError.setError("A camera already exists at this coordinates");
                    }
                }
                if (camera1.getName().equalsIgnoreCase(this.cameraNameButton.getText().trim())) {
                    if (this.camera == null) {
                        active = false;
                        this.createError.setError("A camera already exists with this name");
                    } else if (this.camera != null && !this.camera.getName().equalsIgnoreCase(camera1.getName())) {
                        active = false;
                        this.createError.setError("A camera already exists with this name");
                    }
                }
            }
            if (this.cameraNameButton.getText().trim().equals("")) {
                active = false;
                this.createError.setError("Provide a name");
            }
        }
        this.addButton.active = active;
        this.createError.render(matrices, textRenderer);
        if (active) this.createError.setShow(false);

        //Color palette
        if (this.colorPalette) {
            renderBackground(matrices);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, this.colorPaletteTexture);
            RenderSystem.texParameter(3553, 10241, 9728);
            drawTexture(matrices, width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        }

        if (!this.overworld.isChecked() && !this.nether.isChecked() && !this.end.isChecked()) {
            if (Dimensions.OVERWORLD == this.last) {
                this.overworld.onPress();
            } else if (Dimensions.NETHER == this.last) {
                this.nether.onPress();
            } else if (Dimensions.END == this.last) {
                this.end.onPress();
            }
        }

        //Tooltip supplier cuz miencraft code is shit
        List<Text> supplyInfo = new ArrayList<>();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        buttonsY -= 7;
        if (mouseX >= width / 2 - 101 && mouseX < width / 2 - 101 + 100 && mouseY >= buttonsY && mouseY < buttonsY + 20) {
            if (this.activeTimer <= Config.getInstance().tickCooldownSupplyInfo) {
                this.activeTimer++;
            } else {
                supplyInfo.clear();
                supplyInfo.add(Text.of("The camera info will be saved"));
                supplyInfo.add(Text.of("but it can't be used"));
                supplyInfo.add(Text.of("You can change it by editing the camera"));
                client.currentScreen.renderTooltip(matrices, supplyInfo, mouseX, mouseY);
            }
        } else if (mouseX >= width / 2 - 101 && mouseX < width / 2 - 101 + 100 && mouseY >= buttonsY + 48 && mouseY < buttonsY + 48 + 20) {
            if (this.visualsTimer <= Config.getInstance().tickCooldownSupplyInfo) {
                this.visualsTimer++;
            } else {
                supplyInfo.clear();
                supplyInfo.add(Text.of("Special icon at the position"));
                supplyInfo.add(Text.of("of the camera with the chosen color"));
                client.currentScreen.renderTooltip(matrices, supplyInfo, mouseX, mouseY);
            }
        } else {
            this.activeTimer = 0;
            this.visualsTimer = 0;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.colorPalette) {
            if (mouseX >= (width / 2 - 128) && mouseX < (width / 2 + 128) && mouseY >= (height / 2 - 128) && mouseY < (height / 2 + 128)) {
                BufferedImage image = RenderUtils.bufferedImageFromIdentifier(this.colorPaletteTexture);
                try {
                    int color = image.getRGB((int) mouseX - (width / 2 - 128), (int) mouseY - (height / 2 - 128));
                    this.red = (color >> 16 & 0xFF) / 255.0F;
                    this.green = (color >> 8 & 0xFF) / 255.0F;
                    this.blue = (color >> 0 & 0xFF) / 255.0F;
                } catch (IndexOutOfBoundsException e) {
                    //Some scalling problems sometimes happens
                    e.printStackTrace();
                }
                this.colorPalette = false;
            }
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return true;
    }

    private BlockPos createBlockPos() {
        try {
            if (!this.xButton.getText().trim().equals("") && !this.yButton.getText().trim().equals("") && !this.zButton.getText().trim().equals("")) {
                return new BlockPos(Integer.parseInt(this.xButton.getText()), Integer.parseInt(this.yButton.getText()), Integer.parseInt(this.zButton.getText()));
            }
        } catch (NumberFormatException ignored) {
            //This means he wrote a - without anything more
        }
        return null;
    }

    @Override
    public void onClose() {
        super.onClose();
        close();
    }

    private void close() {
        if (this.parent == null) {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
            client.keyboard.setRepeatEvents(false);
        } else {
            client.setScreen(parent);
        }
    }

    private void suggestX(String text) {
        assert client != null;
        assert client.player != null;
        this.xButton.setSuggestion(suggest(text, (int) client.player.getX()));
    }

    private void suggestY(String text) {
        assert client != null;
        assert client.player != null;
        this.yButton.setSuggestion(suggest(text, (int) client.player.getY()));
    }

    private void suggestZ(String text) {
        assert client != null;
        this.zButton.setSuggestion(suggest(text, (int) client.player.getZ()));
    }

    //TODO: maybe do it better
    private String suggest(String text, int suggestion) {
        if (text.trim().length() == 0) {
            return String.valueOf(suggestion);
        }
        if (text.equals(String.valueOf(suggestion))) {
            return (String) null;
        }
        String[] textChars = new String[text.length()];
        String[] suggestionChars = new String[String.valueOf(suggestion).length()];

        String help = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '-') {
                help = "-";
                continue;
            }
            if (!help.equals("")) {
                help = "";
                textChars[i] = "-".concat(String.valueOf(text.charAt(i)));
            } else {
                textChars[i] = String.valueOf(text.charAt(i));
            }
        }

        String sugg = String.valueOf(suggestion);
        String help1 = "";
        for (int i = 0; i < sugg.length(); i++) {
            if (sugg.charAt(i) == '-') {
                help1 = "-";
                continue;
            }
            if (!help1.equals("")) {
                help1 = "";
                suggestionChars[i] = "-".concat(String.valueOf(sugg.charAt(i)));
            } else {
                suggestionChars[i] = String.valueOf(sugg.charAt(i));
            }
        }

        for (int i = 0; i < text.length() || i < sugg.length(); i++) {
            try {
                if (text.charAt(i) != sugg.charAt(i)) {
                    return (String) null;
                }
            } catch (StringIndexOutOfBoundsException ignored) {
                //just a little of child porn
            }
        }

        if (suggestionChars.length > textChars.length) {
            StringBuilder sb = new StringBuilder(sugg);
            sb.delete(0, textChars.length);
            return sb.toString();
        }

        return (String) null;
    }
}
