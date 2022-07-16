package dan.extracameras.gui.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;

public class CustomTextFieldWidget extends TextFieldWidget {

    private String pattern;

    public CustomTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, String pattern) {
        super(textRenderer, x, y, width, height, text);
        this.pattern = pattern;
    }

    @Override
    public void write(String text) {
        if(text.matches(pattern)) {
            super.write(text);
        }
    }
}
