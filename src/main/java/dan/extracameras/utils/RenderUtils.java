package dan.extracameras.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.ExtraCameras;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.BufferUtils;

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
}

