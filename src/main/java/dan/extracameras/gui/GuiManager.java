package dan.extracameras.gui;


import dan.extracameras.config.Config;
import dan.extracameras.utils.RenderUtils;
import dan.extracameras.utils.Instance;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    private int x;
    private int y;
    private int width;
    private int height;
    private List<Form> forms;
    public static final int fontHeight = 17;
    private int mouseX;
    private int mouseY;

    public GuiManager(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.forms = new ArrayList<>();
    }

    /*
     * @param text
     *  only if allPreview = false , means picked a text true, or image false
     */
    public void render(MatrixStack matrices, TextRenderer textRenderer, boolean allPreview, boolean text) {
        boolean image = !text;
        boolean text1 = text;
        if (allPreview) {
            image = true;
            text1 = true;
        }
        forms.clear();
        renderRectangles(text1);
        renderImageSquare(image);
    }

    //Neds to be called before render
    public void updateMouse(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public boolean isMoveOver(int mouseX, int mouseY, int x, int y, int x2, int y2) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    public PositionResult canPlaceThere(int mouseX, int mouseY) {
        for (Form form : forms) {
            if (isMoveOver(mouseX, mouseY, form.x, form.y, form.x2, form.y2)) {
                //TODO: mirar bien esto
                return new PositionResult(form.x + ((form.x2 - form.x) / 2), y, true, forms.indexOf(form));
            }
        }
        return new PositionResult(0, 0, false, 0);
    }

    public void renderRectangles(boolean draw) {
        RenderUtils.renderHighlightedSquare(x, y, x + width, y + height, Color.WHITE);
        int rectangles = getPosibleRectangle(this.y, this.height);
        int rest = (height + 1) - 25 * rectangles;
        int difference = rest / (rectangles + 1);
        int xv = (int) ((x + width) / 2 + (x + width) / 3) + 2;
        int y1 = y + difference;
        int x2 = x + width - 4;
        int y2 = (int) (y1 + this.fontHeight * 1.5f);
        for (int i = 0; i < rectangles; i++) {
            Color color = Color.WHITE;
            if (isMoveOver(mouseX, mouseY, xv, y1, x2, y2)) {
                color = Color.BLACK;
            }
            if (draw)
                RenderUtils.renderHighlightedSquare(xv, y1, x2, y2, color);
            forms.add(new Form(xv, y1, x2, y2));
            y1 += 25 + difference;
        }
        y1 = y + difference;
        int x1 = 0;
        int wid = Config.getInstance().previewSize >= this.height ? this.height : (int) Config.getInstance().previewSize;
        difference = 4;
        if (acceptsImage(height)) {
            int use = wid == this.height ? this.x + difference + height : this.x + 4 + wid;
            int widt = (this.width - (int) ((x + width) / 2 + (x + width) / 3) + 2 - use);
            int res = ((widt - use) - 93) / 2;
            x1 = use + difference;
        } else {
            x1 = this.x + 4;
        }
        difference = rest / (rectangles + 1);
        y2 = (int) (y1 + this.fontHeight * 1.5);
        for (int i = 0; i < rectangles; i++) {
            Color color = Color.WHITE;
            if (isMoveOver(mouseX, mouseY, x1, y1, x1 + 93, y2)) {
                color = Color.BLACK;
            }
            if (draw)
                RenderUtils.renderHighlightedSquare(x1, y1, x1 + 93, y2, color);
            forms.add(new Form(x1, y1, x1 + 93, y2));
            y1 += 25 + difference;
        }
    }

    private void renderImageSquare(boolean draw) {
        if (acceptsImage(height)) {
            int wid = Config.getInstance().previewSize >= this.height ? this.height : (int) Config.getInstance().previewSize;
            int difference = 4;

            if (wid > 65) wid = 65;
            if (wid == this.height) {
                forms.add(new Form(this.x + difference, this.y + difference, this.x + difference + height, this.y + difference + height));
                if (draw)
                    RenderUtils.renderHighlightedSquare(this.x + difference, this.y + difference, this.x + difference + height, this.y - difference + height, Color.WHITE);
            } else {
                forms.add(new Form(this.x + difference, this.y + difference, this.x + difference + height, this.height));
                if (draw)
                    RenderUtils.renderHighlightedSquare(this.x + 4, this.y + difference, this.x + 4 + wid, this.y + wid - difference, Color.white);
            }
        }

    }


    public static int getPosibleRectangle(int y, int height) {
        int value = height / 25;
        return Math.max(value, 0);
    }

    public boolean acceptsImage(int height) {
        return Config.getInstance().entryWidth >= 250;
    }

    public List<Form> getForms() {
        return forms;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public enum GuiBuilderDist {
        LEFT,
        RIGHT,
        CENTER
    }

    public static class PositionResult {

        private int x;
        private int y;
        private boolean canPlaceThere;
        private int formInList;

        public PositionResult(int x, int y, boolean canPlaceThere, int formInList) {
            this.x = x;
            this.y = y;
            this.canPlaceThere = canPlaceThere;
            this.formInList = formInList;
        }

        public int getFormInList() {
            return this.formInList;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isCanPlaceThere() {
            return canPlaceThere;
        }
    }

    public static class Form {

        private int x;
        private int y;
        private int x2;
        private int y2;

        public Form(int x, int y, int x2, int y2) {
            this.x = x;
            this.y = y;
            this.x2 = x2;
            this.y2 = y2;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getX2() {
            return x2;
        }

        public int getY2() {
            return y2;
        }
    }

    public enum GuiBuildersForm {

        RECTANGLE(15, 60),
        SQUARE(-1, -1);

        private int minHeight;
        private int minWidth;

        private GuiBuildersForm(int minHeight, int minWidth) {
            this.minHeight = minHeight;
            this.minWidth = minWidth;
        }

        public int getMinHeight() {
            return minHeight;
        }

        public int getMinWidth() {
            return minWidth;
        }
    }
}
