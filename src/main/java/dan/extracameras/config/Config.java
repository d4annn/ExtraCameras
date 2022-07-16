package dan.extracameras.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dan.extracameras.ExtraCameras;
import dan.extracameras.utils.ErrorUtils;
import dan.extracameras.utils.Variables;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private List<CameraConfig> worldCameras;
    private Map<String, String> options = new HashMap<>();

    public void loadUp() {
        options.clear();
        options.put("cameraSpeed", String.valueOf(Variables.CameraOptions.cameraSpeed));
        options.put("tickCooldownSupplyInfo", String.valueOf(Variables.CameraOptions.tickCooldownSupplyInfo));
        options.put("entryWidth", String.valueOf(Variables.CamerasListVariables.entryWidth));
        options.put("entryHeight", String.valueOf(Variables.CamerasListVariables.entryHeight));
        options.put("previewSize", String.valueOf(Variables.CamerasListVariables.previewSize));
        options.put("cameraChunkDistance", String.valueOf(Variables.CameraOptions.cameraChunkDistance));
        options.put("mapUpdateRate", String.valueOf(Variables.CameraOptions.mapUpdateRate));
    }

    public void reloadConfig() {
        Variables.CameraOptions.cameraSpeed = Double.parseDouble(options.get("cameraSpeed"));
        Variables.CameraOptions.tickCooldownSupplyInfo = Double.parseDouble(options.get("tickCooldownSupplyInfo"));
        Variables.CamerasListVariables.entryWidth = Double.parseDouble(options.get("entryWidth"));
        Variables.CamerasListVariables.entryHeight = Double.parseDouble(options.get("entryHeight"));
        Variables.CamerasListVariables.previewSize = Double.parseDouble(options.get("previewSize"));
        Variables.CameraOptions.cameraChunkDistance = Double.parseDouble(options.get("cameraChunkDistance"));
        Variables.CameraOptions.mapUpdateRate = Double.parseDouble(options.get("mapUpdateRate"));
    }

    public List<CameraConfig> getWorldCameras() {
        return this.worldCameras;
    }

    public Config() {
        checkFile();
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

    public boolean saveConfig() {
        loadUp();
        checkFile();
        try {
            if (worldCameras != null && !worldCameras.isEmpty()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(Variables.CAMERAS_CONFIG_FILE));
                GSON.toJson(worldCameras, writer);
                writer.flush();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(Variables.OPTIONS_CONFIG_FILE));
            for (String opt : options.keySet()) {
                bw.write(opt + " : " + options.get(opt));
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            ErrorUtils.consoleWarn("An error ocurred while trying to save world cameras config");
            return false;
        }
        return true;
    }

    public boolean loadConfig() {
         checkFile();
        boolean newWorld = true;
        try {
            //World Cameras
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(Variables.CAMERAS_CONFIG_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            List<CameraConfig> config = GSON.fromJson(sb.toString(), new TypeToken<ArrayList<CameraConfig>>() {
            }.getType());
            if (config != null) {
                this.worldCameras = config;
                newWorld = false;
            }
            reader.close();
            //Options
            BufferedReader br = new BufferedReader(new FileReader(Variables.OPTIONS_CONFIG_FILE));
            String line1;
            while ((line1 = br.readLine()) != null) {
                try {
                    //format name : value
                    String[] split = line1.split(":");
                    options.put(split[0].replaceAll(" ", ""), split[1].replaceAll(" ", ""));
                } catch (Exception ignored) {
                }
            }
            br.close();
            reloadConfig();
        } catch (IOException e) {
            //TODO: Variables.packets.add(new ConfigLoadPacket("ConfigLoad", true))
            ErrorUtils.consoleWarn("An error ocurred while loading the config");
            return false;
        }
        if (newWorld) {
            this.worldCameras = new ArrayList<>();
        }
        return true;
    }

    public void checkFile() {
        try {
            Variables.CONFIG_FOLDER.mkdirs();
            Variables.CAMERAS_CONFIG_FILE.createNewFile();
            Variables.OPTIONS_CONFIG_FILE.createNewFile();
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
