package dan.extracameras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dan.extracameras.ExtraCameras;
import dan.extracameras.utils.ErrorUtils;
import dan.extracameras.utils.Instance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static Config INSTANCE;
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public List<CameraConfig> worldCameras;
    public double cameraSpeed;
    public double tickCooldownSupplyInfo;
    public double entryWidth;
    public double entryHeight;
    public double previewSize;
    public double cameraChunkDistance;
    public double mapUpdateRate;

    public Config() {
        checkFile();
        setDefault();
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public static void setInstance(Config INSTANCE) {
        Config.INSTANCE = INSTANCE;
    }

    public void setDefault() {
        worldCameras = new ArrayList<>();
        cameraSpeed = 3;
        tickCooldownSupplyInfo = 40;
        cameraChunkDistance = 5;
        mapUpdateRate = 80;
        entryHeight = 26;
        entryWidth = 220;
        previewSize = 50;
    }

    public void changeWorldCamerasConfig(CameraConfig newConfig) {
        for (int i = 0; i < worldCameras.size(); i++) {
            if (worldCameras.get(i).isServer() == newConfig.isServer() && worldCameras.get(i).getWorld().equals(newConfig.getWorld())) {
                worldCameras.set(i, newConfig);
            }
        }
    }

    public boolean addCameraConfig(CameraConfig config) {
        for (CameraConfig config1 : worldCameras) {
            if (config1.getWorld().equals(config.getWorld())) {
                return false;
            }
        }
        worldCameras.add(config);
        return true;
    }

    public void saveConfig() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(Instance.CONFIG_FILE));
            GSON.toJson(this, bw);
            bw.flush();
        } catch (IOException e) {
            ExtraCameras.logger.warn("Error occurred while saving config");
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(Instance.CONFIG_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Config cf = GSON.fromJson(sb.toString(), this.getClass());
            if (cf == null) {
                setDefault();
                saveConfig();
                setInstance(this);
            } else {
                setInstance(cf);
            }
        } catch (IOException e) {
            ExtraCameras.logger.warn("Error occurred while loading config");
            e.printStackTrace();
        }
    }

    public void checkFile() {
        try {
            Instance.CONFIG_FOLDER.mkdirs();
            Instance.CONFIG_FILE.createNewFile();
        } catch (IOException e) {
            ErrorUtils.consoleWarn("An error ocurred while creating or checking the config file");
        }
    }
/*
    public static BufferedImage getCameraImage(String cameraName) {
        try {
            return ImageIO.read(new File(Variables.CAMERA_IMAGES_FOLDER.getPath() + cameraName + ".png"));
        } catch (IOException e) {
            ExtraCameras.logger.error("Error ocurred while trying to find the " + cameraName + "camera image!");
        }
        return null;
    }

 */
/*
    public static void saveCameraImage(String cameraName, BufferedImage image) {
        try {
            ImageIO.write(image, "png", new File(Variables.CAMERA_IMAGES_FOLDER.getPath() + cameraName + ".png"));
        } catch (IOException e) {
            ExtraCameras.logger.error("Error ocurred while trying to save the " + cameraName + "camera image!");
        }
    }

 */
}
