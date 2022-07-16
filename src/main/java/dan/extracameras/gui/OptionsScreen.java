package dan.extracameras.gui;

import dan.extracameras.utils.I18nUtils;
import dan.extracameras.utils.Options;
import dan.extracameras.utils.Variables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ObjectUtils;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionsScreen extends Screen {

    private Map<String, List<Text>> tooltips;
    private Map<String, Element> options;
    private Screen gui;
    private int timeHovered;
    private Element hovered;

    public OptionsScreen(Screen gui) {
        super(new TranslatableText("text.extracameras.options"));
        this.gui = gui;
        options = new HashMap<>();
        tooltips = new HashMap<>();
        timeHovered = 0;
        hovered = null;
    }

    @Override
    protected void init() {
        super.init();
        initTooltips();
        this.addDrawableChild(new ButtonWidget(5, height - 28, 80, 20, ScreenTexts.DONE, (button) -> {
            this.client.setScreen(this.gui);
            close();
        }));
        this.options.put("cameraSpeed", this.addDrawableChild(new DoubleOptionSliderWidget(client.options, this.width / 2 - 155, this.height / 6 + 48 - 40, 150, 20, Options.CAMERA_SPEED, new ArrayList<>())));
        this.options.put("tickTime", this.addDrawableChild(new DoubleOptionSliderWidget(client.options, this.width / 2 + 5, this.height / 6 + 48 - 40, 150, 20, Options.TICK_TOOLTIP, new ArrayList<>())));
        this.options.put("cameraChunks", this.addDrawableChild(new DoubleOptionSliderWidget(client.options, this.width / 2 + 5, this.height / 6 + 48 - 40 + 24, 150, 20, Options.CAMERA_CHUNKS, new ArrayList<>())));
        this.options.put("mapUpdateRate", this.addDrawableChild(new DoubleOptionSliderWidget(client.options, this.width / 2 - 155, this.height / 6 + 48 - 40 + 24, 150, 20, Options.MAP_UPDATE_RATE, new ArrayList<>())));
        this.addDrawableChild(new ButtonWidget(5, height - 50, 80, 20, new TranslatableText("text.extracameras.default"), (button -> {
            Variables.CameraOptions.cameraSpeed = 3;
            Variables.CameraOptions.tickCooldownSupplyInfo = 40;
            client.setScreen(gui);
            client.setScreen(new OptionsScreen(gui));
            Variables.config.saveConfig();
        })));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        boolean anyHovered = false;
        for (String element : options.keySet()) {
            if (this.options.get(element).isMouseOver(mouseX, mouseY)) {
                anyHovered = true;
                if (this.hovered == null) {
                    this.hovered = this.options.get(element);
                    this.timeHovered = 0;
                } else {
                    if (this.hovered == this.options.get(element)) {
                        if (this.timeHovered >= Variables.CameraOptions.tickCooldownSupplyInfo) {
                            MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, tooltips.get(element), mouseX, mouseY);
                        } else {
                            this.timeHovered++;
                        }
                    }
                }
            }
        }
        if (!anyHovered) {
            this.hovered = null;
        }
    }

    public void close() {
        Variables.config.saveConfig();
        Variables.config.loadConfig();
        Variables.config.loadUp();
    }

    @Override
    public void onClose() {
        close();
        super.onClose();
    }

    private void initTooltips() {
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(Text.of("The speed that you move while"));
        tooltip.add(Text.of("you are using a camera"));
        tooltips.put("cameraSpeed", tooltip);
        tooltip.clear();
        tooltip.add(Text.of("Ticks for supply info"));
        tooltip.add(Text.of("text appear"));
        tooltips.put("tickTime", tooltip);
        tooltip.clear();
        tooltip.add(Text.of("Chunks visible with the camera"));
        tooltips.put("cameraChunks", tooltip);
        tooltip.clear();
        tooltip.add(Text.of("Ticks form Map updates"));
        tooltip.add(Text.of("§4ATENTION§r having a low update rate"));
        tooltip.add(Text.of("can make the game laggy"));
        tooltips.put("mapUpdateRate", tooltip);
        tooltip.clear();
    }
}
