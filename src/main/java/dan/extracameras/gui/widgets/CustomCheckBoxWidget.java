package dan.extracameras.gui.widgets;

import dan.extracameras.interfaces.PressableAction;
import dan.extracameras.utils.Variables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class CustomCheckBoxWidget extends CheckboxWidget {

    private PressableAction action;
    private List<Text> supplyInfo;
    private int timeHovered = 0;

    public CustomCheckBoxWidget(int x, int y, int width, int height, Text message, boolean checked, PressableAction action, List<Text> supplyInfo) {
        super(x, y, width, height, message, checked);
        this.action = action;
        this.supplyInfo = supplyInfo;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        if(mouseX >= this.x && mouseX < this.x + 20 && mouseY >= this.y && mouseY < this.y + this.height) {
            if(timeHovered <= Variables.CameraOptions.tickCooldownSupplyInfo) {
                timeHovered++;
            } else {
                MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, supplyInfo,  mouseX, mouseY);
            }
        } else {
            timeHovered = 0;
        }
    }

    @Override
    public void onPress() {
        this.action.onPress(this);
        super.onPress();
    }

}
