package dan.extracameras.utils;

import dan.extracameras.camera.Camera;
import dan.extracameras.camera.FakeCameraClient;
import dan.extracameras.config.CameraConfig;
import dan.extracameras.config.Config;
import dan.extracameras.gui.widgets.map.WorldMap;
import dan.extracameras.packets.Packet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Variables {

    public static boolean isOnWorld = false;

    public static List<Packet> packets = new ArrayList<>();

    public static final File CONFIG_FOLDER = new File(new File(MinecraftClient.getInstance().runDirectory, "config").getAbsolutePath() + "\\ExtraCameras");

    public static final File CAMERAS_CONFIG_FILE = new File(new File(MinecraftClient.getInstance().runDirectory, "config").getAbsolutePath() + "\\ExtraCameras\\WorldCameras.json");

    public static final File OPTIONS_CONFIG_FILE = new File(new File(MinecraftClient.getInstance().runDirectory, "config").getAbsolutePath() + "\\ExtraCameras\\Config.txt");

    public static Config config;

    public static FakeCameraClient fakeCamera;

    public static WorldMap map;

    public static boolean cameraOn = false;

    public static CameraConfig currentWorldCameras = null;

    public static Camera lastCamera;

    public static class CameraOptions {

        public static double cameraSpeed = 3;

        public static double tickCooldownSupplyInfo = 40;

        public static double cameraChunkDistance = 5;

        public static double mapUpdateRate = 80;

    }

    public static class CamerasListVariables {

        public static double entryWidth = 220;

        public static double entryHeight = 26;

        public static double previewSize = 50;
    }
}
