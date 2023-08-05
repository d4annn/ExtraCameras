package dan.extracameras.gui.widgets;

import dan.extracameras.config.Config;
import dan.extracameras.utils.Instance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class CustomButtonWidget extends ButtonWidget {

    private int timeHovered = 0;
    private List<Text> supplyInfo;

    public CustomButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, List<Text> supplyInfo) {
        super(x, y, width, height, message, onPress);
        this.supplyInfo = supplyInfo;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        if(mouseX >= this.x && mouseX < this.x + width && mouseY >= this.y && mouseY < this.y + this.height) {
            if(timeHovered <= Config.getInstance().tickCooldownSupplyInfo) {
                timeHovered++;
            } else {
                MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, supplyInfo,  mouseX, mouseY);
            }
        } else {
            timeHovered = 0;
        }
    }
}
