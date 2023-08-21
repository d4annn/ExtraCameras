package dan.extracameras.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import dan.extracameras.camera.Camera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtils {

    public static void saveScreenshot(File file, Framebuffer framebuffer, String name) {
        MinecraftClient.getInstance().options.hudHidden = true;
        NativeImage nativeImage = takeScreenshot(framebuffer);
        MinecraftClient.getInstance().options.hudHidden = false;
        Util.getIoWorkerExecutor().execute(() -> {
            try {
                file.createNewFile();
                nativeImage.writeTo(file);
                registerImage(name, file);
            } catch (Exception var7) {
                ErrorUtils.throwGameError("Couldn't save screenshot : " + var7.getMessage(), new ArrayList<>());
            } finally {
                nativeImage.close();
            }
        });
    }

    public static void registerImage(String name, File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        Identifier identifier = new Identifier("extracameras", "camera_image/" + name.toLowerCase());
        Map<String, Identifier> worldCameras = Instance.images.get(Instance.currentWorldCameras.getWorld());
        worldCameras.put(name, identifier);
        RenderUtils.saveBufferedImageAsIdentifier(image, identifier);
    }

    public static void initImages(List<Camera> cameras, String world) {
        if (!Instance.images.containsKey(world)) Instance.images.put(world, new HashMap<>());
        Map<String, Identifier> worldIdentifiers = Instance.images.get(world);
        for (Camera camera : cameras) {
            File file = new File(Instance.IMAGE_FOLDER.getAbsolutePath() + "\\" + world + "\\" + camera.getName() + ".png");
            if (file.exists()) {
                BufferedImage image;
                try {
                    image = ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    worldIdentifiers.put(camera.getName(), RenderUtils.NO_IMAGE);
                    continue;
                }
                if (image == null) {
                    worldIdentifiers.put(camera.getName(), RenderUtils.NO_IMAGE);
                    continue;
                }
                Identifier identifier = new Identifier("extracameras", "camera_image/" + camera.getName().toLowerCase());
                RenderUtils.saveBufferedImageAsIdentifier(image, identifier);
                worldIdentifiers.put(camera.getName(), identifier);
            } else {
                worldIdentifiers.put(camera.getName(), RenderUtils.NO_IMAGE);
            }
        }
        Instance.images.put(world, worldIdentifiers);
    }

    public static NativeImage takeScreenshot(Framebuffer framebuffer) {
        int i = framebuffer.textureWidth;
        int j = framebuffer.textureHeight;
        NativeImage nativeImage = new NativeImage(i, j, false);
        RenderSystem.bindTexture(framebuffer.getColorAttachment());
        nativeImage.loadFromTextureImage(0, true);
        nativeImage.mirrorVertically();
        return nativeImage;
    }
}
