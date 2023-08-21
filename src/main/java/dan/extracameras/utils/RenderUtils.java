package dan.extracameras.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.ExtraCameras;
import dan.extracameras.objects.DoubleDuple;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Range;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.include.com.google.common.base.Preconditions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class RenderUtils {

    public static final Identifier WHITE = new Identifier("textures/misc/white.png");
    public static final Identifier NO_IMAGE = new Identifier("extracameras", "textures/no_image.png");

    public static double getEquivalentPosition(double reference, double reference2, double equivalent) {
        return (equivalent * reference2) / reference;
    }

    public static BufferedImage bufferedImageFromIdentifier(Identifier identifier) {
        try {
            InputStream is = MinecraftClient.getInstance().getResourceManager().getResource(identifier).getInputStream();
            BufferedImage image = ImageIO.read(is);
            is.close();
            if (image.getType() != 6) {
                BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), 6);
                Graphics2D g2 = temp.createGraphics();
                g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
                g2.dispose();
                image = temp;
            }
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveBufferedImageAsIdentifier(BufferedImage bufferedImage, Identifier identifier) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", stream);
            byte[] bytes = stream.toByteArray();
            ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
            data.flip();
            NativeImage img = NativeImage.read(data);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(img);
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture));
        } catch (IOException e) {
            ExtraCameras.logger.error("Error ocurred while trying to convert image to identifier!");
        }
    }

    public static int RGBAToInt(@Range(from = 0, to = 255) int r, @Range(from = 0, to = 255) int g, @Range(from = 0, to = 255) int b, @Range(from = 0, to = 255) int a) {
        Preconditions.checkArgument(validateColorRange(r), "Expected r to be 0-255, received " + r);
        Preconditions.checkArgument(validateColorRange(g), "Expected g to be 0-255, received " + g);
        Preconditions.checkArgument(validateColorRange(b), "Expected b to be 0-255, received " + b);
        Preconditions.checkArgument(validateColorRange(a), "Expected a to be 0-255, received " + a);
        return r << 8 * 3 | g << 8 * 2 | b << 8 | a;
    }

    public static Box renderEmptyQuad(MatrixStack matrices, int color, double x1, double y1, double x2, double y2, double lineWidth) {
        float xOffset = (float) (x2 - x1);
        float yOffset = (float) (y2 - y1);

        // Top quad
        renderQuad(matrices, color, x1, y1, x2, y1 + lineWidth);

        // Bottom quad
        renderQuad(matrices, color, x1, y2 - lineWidth, x2, y2);

        // Left quad
        renderQuad(matrices, color, x1, y1 + lineWidth, x1 + lineWidth, y2 - lineWidth);

        // Right quad
        renderQuad(matrices, color, x2 - lineWidth, y1 + lineWidth, x2, y2 - lineWidth);

        return new Box(x1, y1, -1, x2, y2, -1);
    }

    public static void renderQuad(MatrixStack matrices, int color, double x1, double y1, double x2, double y2) {
        double j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float) x1, (float) y2, 0.0F)
                .color(g, h, k, f)
                .next();
        buffer.vertex(matrix, (float) x2, (float) y2, 0.0F)
                .color(g, h, k, f)
                .next();
        buffer.vertex(matrix, (float) x2, (float) y1, 0.0F)
                .color(g, h, k, f)
                .next();
        buffer.vertex(matrix, (float) x1, (float) y1, 0.0F)
                .color(g, h, k, f)
                .next();

        buffer.end();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferRenderer.draw(buffer);
        endRender();
    }

    public static void setupRender() {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    /**
     * <p>Reverts everything back to normal after rendering</p>
     */
    public static void endRender() {
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }

    private static boolean validateColorRange(int in) {
        return in >= 0 && in <= 255;
    }

    public static void renderBoxWithoutCenter(Matrix4f matrix, double x1, double y1, double x2, double y2, int color) {
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static int getDistanceSupplyInfoAbove(List<Text> text) {
        int layers = text.size();
        return layers * 8; //8 is the height that occupies a line
    }

    public static void renderDarkerBackround(int y1, int x1, int width, int height, int y2, int x2) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tes.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex((double) x1, (double) y1, -100.0D).texture(0.0F, (float) y1 / 32.0F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex((double) (x1 + width), (double) y1, -100.0D).texture((float) width / 32.0F, (float) y1 / 32.0F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex((double) (x1 + width), 0.0D, -100.0D).texture((float) width / 32.0F, 0.0F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex((double) x1, 0.0D, -100.0D).texture(0.0F, 0.0F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex((double) x1, (double) height, -100.0D).texture(0.0F, (float) height / 32.0F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex((double) (x1 + width), (double) height, -100.0D).texture((float) width / 32.0F, (float) height / 32.0F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex((double) (x1 + width), (double) y2, -100.0D).texture((float) width / 32.0F, (float) y2 / 32.0F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex((double) x1, (double) y2, -100.0D).texture(0.0F, (float) y2 / 32.0F).color(64, 64, 64, 255).next();
        tes.draw();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex((double) x1, (double) (y1 + 4), 0.0D).color(0, 0, 0, 0).next();
        bufferBuilder.vertex((double) x2, (double) (y1 + 4), 0.0D).color(0, 0, 0, 0).next();
        bufferBuilder.vertex((double) x2, (double) y1, 0.0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex((double) x1, (double) y1, 0.0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex((double) x1, (double) y2, 0.0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex((double) x2, (double) y2, 0.0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex((double) x2, (double) (y2 - 4), 0.0D).color(0, 0, 0, 0).next();
        bufferBuilder.vertex((double) x1, (double) (y2 - 4), 0.0D).color(0, 0, 0, 0).next();
        tes.draw();
    }

    //Nice name tho
    public static void renderSpaceBackroundDarker(int y1, int x1, int width, int height, int y2, int x2) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tess.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex((double) x1, (double) y2, 0.0D).texture((float) x1 / 32.0F, (float) (y2 + 0) / 32.0F).color(32, 32, 32, 255).next();
        bufferBuilder.vertex((double) x2, (double) y2, 0.0D).texture((float) x2 / 32.0F, (float) (y2 + 0) / 32.0F).color(32, 32, 32, 255).next();
        bufferBuilder.vertex((double) x2, (double) y1, 0.0D).texture((float) x2 / 32.0F, (float) (y1 + 0) / 32.0F).color(32, 32, 32, 255).next();
        bufferBuilder.vertex((double) x1, (double) y1, 0.0D).texture((float) x1 / 32.0F, (float) (y1 + 0) / 32.0F).color(32, 32, 32, 255).next();
        tess.draw();
        RenderUtils.renderDarkerBackround(y1, x1, width, height, y2, x2);
    }

    public static Vec3d calculateRelativePosition(Vec3d cameraPos, Vec3d expectedPos) {
        expectedPos.subtract(cameraPos);
        return expectedPos;
    }

    public static void positionAccurateScale(MatrixStack stack, float scale, double x, double y) {
        stack.translate(1, 1, 1);
        stack.translate(x, y, 0);
        stack.scale(scale, scale, scale);
        stack.translate(-x, -y, 0);
    }

    public static void renderHighlightedSquare(int x1, int y1, int x2, int y2, Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tes.getBuffer();
        bufferBuilder.clear();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex((double) (x1 + 4), (double) (y1 + 4), 0.0D).color(r, g, b, 255).next();
        bufferBuilder.vertex((double) (x2 - 4), (double) (y1 + 4), 0.0D).color(r, g, b, 255).next();
        bufferBuilder.vertex((double) x2, (double) y1, 0.0D).color(r, g, b, 0).next();
        bufferBuilder.vertex((double) x1, (double) y1, 0.0D).color(r, g, b, 0).next();
        bufferBuilder.vertex((double) x1, (double) y2, 0.0D).color(r, g, b, 0).next();
        bufferBuilder.vertex((double) x2, (double) y2, 0.0D).color(r, g, b, 0).next();
        bufferBuilder.vertex((double) (x2 - 4), (double) (y2 - 4), 0.0D).color(r, g, b, 255).next();
        bufferBuilder.vertex((double) (x1 + 4), (double) (y2 - 4), 0.0D).color(r, g, b, 255).next();
        bufferBuilder.vertex((double) (x1 + 4), (double) (y2 - 4), 0.0D).color(r, g, b, 255).next();
        bufferBuilder.vertex((double) (x1 + 4), (double) (y1 + 4), 0.0D).color(r, g, b, 255).next();
        bufferBuilder.vertex((double) x1, (double) (y1), 0.0D).color(r, g, b, 0).next();
        bufferBuilder.vertex((double) x1, (double) y2, 0.0D).color(r, g, b, 0).next();
        bufferBuilder.vertex((double) x2, (double) y2, 0.0D).color(r, g, b, 0).next();
        bufferBuilder.vertex((double) x2, (double) y1, 0.0D).color(r, g, b, 0).next();
        bufferBuilder.vertex((double) (x2 - 4), (double) (y1 + 4), 0.0D).color(r, g, b, 255).next();
        bufferBuilder.vertex((double) (x2 - 4), (double) (y2 - 4), 0.0D).color(r, g, b, 255).next();
        tes.draw();
        bufferBuilder.clear();
    }

    public static void renderImage(Matrix4f matrix, float x0, float y0, int u, int v, float width, float height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float transparency) {
        float x1 = x0 + width;
        float y1 = y0 + height;
        int z = 1;
        float u0 = (u + 0.0F) / (float) textureWidth;
        float u1 = (u + (float) regionWidth) / (float) textureWidth;
        float v0 = (v + 0.0F) / (float) textureHeight;
        float v1 = (v + (float) regionHeight) / (float) textureHeight;
        RenderSystem.enableBlend();
        if (transparency != 1)
            RenderSystem.setShaderColor(1, 1, 1, transparency);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();
        Tessellator.getInstance().draw();
        RenderSystem.disableBlend();
    }
}

