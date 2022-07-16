package dan.extracameras.camera;

import dan.extracameras.utils.CameraUtils;
import dan.extracameras.utils.Variables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class CameraEntity extends ClientPlayerEntity {

    @Nullable
    private static CameraEntity camera;
    @Nullable
    private static Entity originalCameraEntity;
    private static Vec3d cameraMotion = new Vec3d(0.0, 0.0, 0.0);
    private static boolean cullChunksOriginal;
    private static boolean sprinting;
    private static boolean originalCameraWasPlayer;
    private static boolean isZoneLoaded;
    private static boolean tesellating;

    private Camera cam;

    private CameraEntity(MinecraftClient mc, ClientWorld world, ClientPlayNetworkHandler netHandler, StatHandler stats, ClientRecipeBook recipeBook, Camera cam) {
        super(mc, world, netHandler, stats, recipeBook, false, false);
        this.cam = cam;
        //TODO: change tihs
        setIsZoneLoaded(false);
    }

    public static boolean isTesellating() {
        return tesellating;
    }

    public static void setTesellating(boolean tesellating) {
        CameraEntity.tesellating = tesellating;
    }

    public static void setIsZoneLoaded(boolean isZoneLoaded) {
        CameraEntity.isZoneLoaded = isZoneLoaded;
    }

    public static boolean isZoneLoaded() {
        return isZoneLoaded;
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    public static void movementTick() {
        CameraEntity camera = getCamera();
        GameOptions options = MinecraftClient.getInstance().options;
        camera.updateLastTickPosition();
        if (options.keySprint.isPressed()) {
            sprinting = true;
        } else if (options.keyForward.isPressed() == false && options.keyBack.isPressed() == false) {
            sprinting = false;
        }
        cameraMotion = calculatePlayerMotionWithDeceleration(cameraMotion, 0.15, 0.4);
        double forward = sprinting ? cameraMotion.x * 3 : cameraMotion.x;
        camera.handleMotion(forward, cameraMotion.y, cameraMotion.z);
    }

    public static Vec3d calculatePlayerMotionWithDeceleration(Vec3d lastMotion, double rampAmount, double decelerationFactor) {
        GameOptions options = MinecraftClient.getInstance().options;
        int forward = 0;
        int vertical = 0;
        int strafe = 0;
        if (options.keyForward.isPressed()) {
            forward += 1;
        }
        if (options.keyBack.isPressed()) {
            forward -= 1;
        }
        if (options.keyLeft.isPressed()) {
            strafe += 1;
        }
        if (options.keyRight.isPressed()) {
            strafe -= 1;
        }
        if (options.keyJump.isPressed()) {
            vertical += 1;
        }
        if (options.keySneak.isPressed()) {
            vertical -= 1;
        }
        double speed = (forward != 0 && strafe != 0) ? 1.2 : 1.0;
        double forwardRamped = getRampedMotion(lastMotion.x, forward, rampAmount, decelerationFactor) / speed;
        double verticalRamped = getRampedMotion(lastMotion.y, vertical, rampAmount, decelerationFactor);
        double strafeRamped = getRampedMotion(lastMotion.z, strafe, rampAmount, decelerationFactor) / speed;

        return new Vec3d(forwardRamped, verticalRamped, strafeRamped);
    }

    public static double getRampedMotion(double current, int input, double rampAmount, double decelerationFactor) {
        if (input != 0) {
            if (input < 0) {
                rampAmount *= -1.0;
            }
            if ((input < 0) != (current < 0.0)) {
                current = 0.0;
            }
            current = MathHelper.clamp(current + rampAmount, -1.0, 1.0);
        } else {
            current *= decelerationFactor;
        }
        return current;
    }

    private static double getMoveSpeed() {
        return Variables.CameraOptions.cameraSpeed;
    }

    private void handleMotion(double forward, double up, double strafe) {
        float yaw = this.getYaw();
        double scale = getMoveSpeed();
        double xFactor = Math.sin(yaw * Math.PI / 180.0);
        double zFactor = Math.cos(yaw * Math.PI / 180.0);
        double x = (strafe * zFactor - forward * xFactor) * scale;
        double y = up * scale;
        double z = (forward * zFactor + strafe * xFactor) * scale;
        this.setVelocity(new Vec3d(x, y, z));
        this.move(MovementType.SELF, this.getVelocity());
    }

    private void updateLastTickPosition() {
        this.lastRenderX = this.getX();
        this.lastRenderY = this.getY();
        this.lastRenderZ = this.getZ();
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
        this.prevHeadYaw = this.headYaw;
    }

    public void setCameraRotations(float yaw, float pitch) {
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.headYaw = yaw;
    }

    public void updateCameraRotations(float yawChange, float pitchChange) {
        float yaw = this.getYaw() + yawChange * 0.15F;
        float pitch = MathHelper.clamp(this.getPitch() + pitchChange * 0.15F, -90F, 90F);
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.setCameraRotations(yaw, pitch);
    }

    private static CameraEntity createCameraEntity(MinecraftClient mc, Camera cam) {

        ClientPlayerEntity player = mc.player;
        CameraEntity camera = new CameraEntity(mc, mc.world, player.networkHandler, player.getStatHandler(), player.getRecipeBook(), cam);
        camera.noClip = true;
        float yaw = player.getYaw();
        float pitch = player.getPitch();
        camera.refreshPositionAndAngles(cam.getPos().getX(), cam.getPos().getY(), cam.getPos().getZ(), yaw, pitch);
        camera.setRotation(yaw, pitch);
        return camera;
    }

    @Nullable
    public static CameraEntity getCamera() {
        return camera;
    }

    public static void setCameraState(boolean enabled, Camera cam) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world != null && mc.player != null) {
            if (enabled) {
                createAndSetCamera(mc, cam);
            } else {
                removeCamera(mc);
            }
            mc.gameRenderer.setRenderHand(!enabled);
        }
    }

    public static boolean originalCameraWasPlayer() {
        return true;
    }

    private static void createAndSetCamera(MinecraftClient mc, Camera cam) {
        camera = createCameraEntity(mc, cam);
        originalCameraEntity = mc.getCameraEntity();
        originalCameraWasPlayer = originalCameraEntity == mc.player;
        cullChunksOriginal = mc.chunkCullingEnabled;
        mc.setCameraEntity(camera);
        mc.chunkCullingEnabled = false;
    }

    private static void removeCamera(MinecraftClient mc) {
        if (mc.world != null && camera != null) {
            mc.setCameraEntity(mc.player);
            mc.chunkCullingEnabled = cullChunksOriginal;
            final int chunkX = MathHelper.floor(camera.getX() / 16.0) >> 4;
            final int chunkZ = MathHelper.floor(camera.getZ() / 16.0) >> 4;
            CameraUtils.markChunksForRebuildOnDeactivation(chunkX, chunkZ);
        }

        originalCameraEntity = null;
        camera = null;
    }
}
