package dan.extracameras.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ExtraCamerasEntryList<E extends EntryListWidget.Entry<E>> extends EntryListWidget<E> {

    public boolean doubleClick = false;
    private long lastClicked = 0L;

    public ExtraCamerasEntryList(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        setZOffset(0);
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        int i = getScrollbarPositionX();
        int j = i + 6;
        setScrollAmount(getScrollAmount());
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tess.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex((double) this.left, (double) this.bottom, 0.0D).texture((float) this.left / 32.0F, (float) (this.bottom + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).next();
        bufferBuilder.vertex((double) this.right, (double) this.bottom, 0.0D).texture((float) this.right / 32.0F, (float) (this.bottom + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).next();
        bufferBuilder.vertex((double) this.right, (double) this.top, 0.0D).texture((float) this.right / 32.0F, (float) (this.top + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).next();
        bufferBuilder.vertex((double) this.left, (double) this.top, 0.0D).texture((float) this.left / 32.0F, (float) (this.top + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).next();
        tess.draw();
        int leftTop = left + width / 2 - getRowWidth() / 2 + 2;
        int topY = top + 4 - (int) getScrollAmount();
        RenderUtils.renderDarkerBackround(this.top, this.left, this.width, this.height, this.bottom, this.right);
        int o = getMaxScroll();
        if (o > 0) {
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int m = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getMaxPosition());
            m = MathHelper.clamp(m, 32, this.bottom - this.top - 8);
            int n = (int) this.getScrollAmount() * (this.bottom - this.top - m) / o + this.top;
            if (n < this.top) {
                n = this.top;
            }
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex((double) i, (double) this.bottom, 0.0D).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((double) j, (double) this.bottom, 0.0D).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((double) j, (double) this.top, 0.0D).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((double) i, (double) this.top, 0.0D).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((double) i, (double) (n + m), 0.0D).color(128, 128, 128, 255).next();
            bufferBuilder.vertex((double) j, (double) (n + m), 0.0D).color(128, 128, 128, 255).next();
            bufferBuilder.vertex((double) j, (double) n, 0.0D).color(128, 128, 128, 255).next();
            bufferBuilder.vertex((double) i, (double) n, 0.0D).color(128, 128, 128, 255).next();
            bufferBuilder.vertex((double) i, (double) (n + m - 1), 0.0D).color(192, 192, 192, 255).next();
            bufferBuilder.vertex((double) (j - 1), (double) (n + m - 1), 0.0D).color(192, 192, 192, 255).next();
            bufferBuilder.vertex((double) (j - 1), (double) n, 0.0D).color(192, 192, 192, 255).next();
            bufferBuilder.vertex((double) i, (double) n, 0.0D).color(192, 192, 192, 255).next();
            tess.draw();
        }
        renderDecorations(matrices, mouseX, mouseY);
        RenderSystem.enableTexture();
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.doubleClick = (System.currentTimeMillis() - this.lastClicked < 250L);
        this.lastClicked = System.currentTimeMillis();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
