package dan.extracameras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dan.extracameras.ExtraCameras;
import dan.extracameras.objects.DoubleDuple;
import dan.extracameras.utils.ErrorUtils;
import dan.extracameras.utils.Instance;
import net.minecraft.util.Identifier;
import oshi.software.os.mac.MacInternetProtocolStats;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public boolean showCoords;

    public DoubleDuple namePosition;
    public DoubleDuple activePosition;
    public DoubleDuple dimensionPosition;
    public DoubleDuple posPosition;
    public DoubleDuple imagePlaced;
    public DoubleDuple pinnedPosition;

    public boolean showAll;

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
        namePosition = new DoubleDuple(-1, -1);
        activePosition = new DoubleDuple(-1, -1);
        dimensionPosition = new DoubleDuple(-1, -1);
        posPosition = new DoubleDuple(-1, -1);
        imagePlaced = new DoubleDuple(-1, -1);
        pinnedPosition = new DoubleDuple(-1, -1);
        showAll = true;
        showCoords = true;
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
            Instance.IMAGE_FOLDER.mkdirs();
            Instance.MAP_FOLDER.mkdirs();
        } catch (IOException e) {
            ErrorUtils.consoleWarn("An error ocurred while creating or checking the config file");
        }
    }
}
