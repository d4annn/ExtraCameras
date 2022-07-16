package dan.extracameras.camera;

import dan.extracameras.utils.Dimensions;
import dan.extracameras.utils.ErrorUtils;
import dan.extracameras.utils.Variables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

//Not the actual camera, just an object to save data about the camera, see CameraEntity
public class Camera {

    private BlockPos pos;
    private String name;
    private String dimension;
    private boolean active;
    private boolean visuals;
    private float red;
    private float green;
    private float blue;
    private boolean pinned;
    //Corner 1 is always the one at the top, and corner 2 at the bottom
    private BlockPos corner1;
    private BlockPos corner2;

    public Camera(BlockPos pos, String name, Dimensions dimension, boolean active, boolean visuals, float r, float g, float b) {
        this.pos = pos;
        this.name = name;
        this.dimension = dimension.getName().getString();
        this.active = active;
        this.red = r;
        this.blue = b;
        this.green = g;
        this.pinned = false;
        this.corner1 = new BlockPos(pos.getX() + 16 * Variables.CameraOptions.cameraChunkDistance, 256, pos.getZ() + Variables.CameraOptions.cameraChunkDistance * 16);
        this.corner2 = new BlockPos(pos.getX() - 16 * Variables.CameraOptions.cameraChunkDistance, -64, pos.getZ() - Variables.CameraOptions.cameraChunkDistance * 16);
    }

    public void add() {
        Variables.currentWorldCameras.addCamera(this);
        ErrorUtils.gameInfo("§8" + this.name + " §rcamera added");
    }

    public boolean isActive() {
        return active;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Vec3d getVec3Pos() {
        return new Vec3d(this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }

    public String getName() {
        return this.name;
    }

    public String getDimension() {
        return this.dimension;
    }

    public boolean isVisible() {
        return this.visuals;
    }

    public int getColor() {
        return -16777216 | ((int)(this.red * 255.0F) << 16) | ((int)(this.green * 255.0F) << 8) | (int)(this.blue * 255.0F);
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public BlockPos getCorner1() {
        return corner1;
    }

    public BlockPos getCorner2() {
        return corner2;
    }
}
