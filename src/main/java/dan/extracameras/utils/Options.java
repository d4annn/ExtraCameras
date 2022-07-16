package dan.extracameras.utils;

import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

public class Options {

    public static final DoubleOption CAMERA_LIST_ENTRY_HEIGHT = new DoubleOption("text.extracameras.entry_height", 26.0D, 70.0D, 1.0f,
            (gameOptions) -> Variables.CamerasListVariables.entryHeight,
            (gameOptions, height) -> {
                Variables.CamerasListVariables.entryHeight = height;
                Variables.config.loadUp();
            }, (gameOptions, option) -> new LiteralText(I18nUtils.getString("text.extracameras.entry_height", new Object[0]) + (int) Variables.CamerasListVariables.entryHeight));

    public static final DoubleOption CAMERA_LIST_PREVIEW_HEIGHT = new DoubleOption("text.extracameras.camera_preview_height", 32.0D, 65.0D, 1.0f,
            gameOptions -> Variables.CamerasListVariables.previewSize,
            (gameOptions, aDouble) -> {
                Variables.CamerasListVariables.previewSize = aDouble;
                Variables.config.loadUp();
            }, (gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.preview_height", new Object[0]) + (int) Variables.CamerasListVariables.previewSize));

    public static final DoubleOption CAMERA_LIST_ENTRY_WIDTH = new DoubleOption("text.extracameras.entry_width", 190.0D, 360.0D, 1.0f,
            gameOptions -> Variables.CamerasListVariables.entryWidth,
            (gameOptions, aDouble) -> {
                Variables.CamerasListVariables.entryWidth = aDouble;
                Variables.config.loadUp();
            },
            (gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.entry_width", new Object[0]) + (int) Variables.CamerasListVariables.entryWidth));

    public static final DoubleOption CAMERA_SPEED = new DoubleOption("text.extracameras.camera_speed", 1.0D, 8.0D, 1.0f,
            gameOptions -> (double) Variables.CameraOptions.cameraSpeed,
            (gameOptions, aDouble) -> {
                Variables.CameraOptions.cameraSpeed = aDouble;
                Variables.config.loadUp();
            },
            (gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.camera_speed", new Object[0]) + (int) Variables.CameraOptions.cameraSpeed));

    public static final DoubleOption TICK_TOOLTIP = new DoubleOption("text.extracameras.tick_cooldown_supply_info", 1.0D, 300.0D, 1.0f,
            gameOptions -> (double) Variables.CameraOptions.tickCooldownSupplyInfo,
            (gameOptions, aDouble) -> {
                Variables.CameraOptions.tickCooldownSupplyInfo = aDouble;
                Variables.config.loadUp();
            },
            (gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.tick_cooldown_supply_info", new Object[0]) + (int) Variables.CameraOptions.tickCooldownSupplyInfo));

    public static final DoubleOption CAMERA_CHUNKS = new DoubleOption("text.extracameras.camera_chunks", 1.0D, 8.0D, 1.0f,
            gameOptions -> (double)Variables.CameraOptions.cameraChunkDistance,
            (gameOptions, aDouble) -> {
                Variables.CameraOptions.cameraChunkDistance = aDouble;
                Variables.config.loadUp();
            },
            ((gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.camera_chunks", new Object[0]) + (int) Variables.CameraOptions.cameraChunkDistance)));

    public static final DoubleOption MAP_UPDATE_RATE = new DoubleOption("text.extracameras.map_update_rate", 1.0D, 200.0D, 20.0f,
            gameOptions -> (double) Variables.CameraOptions.mapUpdateRate,
            (gameOptions, aDouble) -> {
                Variables.CameraOptions.mapUpdateRate = aDouble;
                Variables.config.loadUp();
            },
            ((gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.extracameras.map_update_rate", new Object[0]) + (int) Variables.CameraOptions.mapUpdateRate)));
}
