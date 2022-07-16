package dan.extracameras.utils;

import dan.extracameras.ExtraCameras;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;

public class ErrorUtils {

    public static final String identifier = "[Extra Cameras]";

    public static void throwGameError(String error, List<String> description) {

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(identifier + " §4§lError: " + error));
        consoleWarn(error);
        if (!description.isEmpty()) {
            description.forEach((desc) -> {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(identifier + " §c§l" + desc));
                consoleWarn(desc);
            });
        }
    }

    public static void consoleInfo(String text) {
        ExtraCameras.logger.info(text);
    }

    public static void consoleWarn(String text) {
        ExtraCameras.logger.warn(text);
    }

    public static void gameInfo(String text) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(identifier + "  " + text));
    }

    public static class CreateError {

        private boolean show;
        private String error;
        private int x;
        private int y;

        public CreateError(boolean show, String error, int x, int y) {
            show = show;
            this.error = error;
            this.x = x;
            this.y = y;
        }

        public boolean isShow() {
            return show;
        }

        public void setPos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
            this.show = true;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void move(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void render(MatrixStack matrices, TextRenderer textRenderer) {
            if (this.show)
                textRenderer.drawWithShadow(matrices, this.error, this.x, this.y, Color.RED.getRGB());
        }
    }

}
