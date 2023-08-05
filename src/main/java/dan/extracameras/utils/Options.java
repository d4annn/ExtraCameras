package dan.extracameras.utils;

import dan.extracameras.config.Config;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.text.LiteralText;

public class Options {

    public static final DoubleOption CAMERA_LIST_ENTRY_HEIGHT = new DoubleOption("text.extracameras.entry_height", 26.0D, 70.0D, 1.0f,
            (gameOptions) -> Config.getInstance().entryHeight,
            (gameOptions, height) -> {
                Config.getInstance().entryHeight = height;
            }, (gameOptions, option) -> new LiteralText(I18nUtils.getString("text.extracameras.entry_height", new Object[0]) + (int) Config.getInstance().entryHeight));

    public static final DoubleOption CAMERA_LIST_PREVIEW_HEIGHT = new DoubleOption("text.extracameras.camera_preview_height", 32.0D, 65.0D, 1.0f,
            gameOptions -> Config.getInstance().previewSize,
            (gameOptions, aDouble) -> {
                Config.getInstance().previewSize = aDouble;
            }, (gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.preview_height", new Object[0]) + (int) Config.getInstance().previewSize));

    public static final DoubleOption CAMERA_LIST_ENTRY_WIDTH = new DoubleOption("text.extracameras.entry_width", 190.0D, 360.0D, 1.0f,
            gameOptions -> Config.getInstance().entryWidth,
            (gameOptions, aDouble) -> {
                Config.getInstance().entryWidth = aDouble;
            },
            (gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.entry_width", new Object[0]) + (int) Config.getInstance().entryWidth));

    public static final DoubleOption CAMERA_SPEED = new DoubleOption("text.extracameras.camera_speed", 1.0D, 8.0D, 1.0f,
            gameOptions -> (double) Config.getInstance().cameraSpeed,
            (gameOptions, aDouble) -> {
                Config.getInstance().cameraSpeed = aDouble;
            },
            (gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.camera_speed", new Object[0]) + (int) Config.getInstance().cameraSpeed));

    public static final DoubleOption TICK_TOOLTIP = new DoubleOption("text.extracameras.tick_cooldown_supply_info", 1.0D, 300.0D, 1.0f,
            gameOptions -> (double) Config.getInstance().tickCooldownSupplyInfo,
            (gameOptions, aDouble) -> {
                Config.getInstance().tickCooldownSupplyInfo = aDouble;
            },
            (gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.tick_cooldown_supply_info", new Object[0]) + (int) Config.getInstance().tickCooldownSupplyInfo));

    public static final DoubleOption CAMERA_CHUNKS = new DoubleOption("text.extracameras.camera_chunks", 1.0D, 8.0D, 1.0f,
            gameOptions -> (double) Config.getInstance().cameraChunkDistance,
            (gameOptions, aDouble) -> {
                Config.getInstance().cameraChunkDistance = aDouble;
            },
            ((gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.camera_chunks", new Object[0]) + (int) Config.getInstance().cameraChunkDistance)));

    public static final DoubleOption MAP_UPDATE_RATE = new DoubleOption("text.extracameras.map_update_rate", 1.0D, 200.0D, 20.0f,
            gameOptions -> (double) Config.getInstance().mapUpdateRate,
            (gameOptions, aDouble) -> {
                Config.getInstance().mapUpdateRate = aDouble;
            },
            ((gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.map_update_rate", new Object[0]) + (int) Config.getInstance().mapUpdateRate)));
}
