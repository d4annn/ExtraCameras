package dan.extracameras.camera;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class FakeCameraClient extends OtherClientPlayerEntity {

    private final ClientPlayerEntity player = MinecraftClient.getInstance().player;
    private final ClientWorld world = MinecraftClient.getInstance().world;

    public FakeCameraClient() {
        super(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getGameProfile());
        copyPositionAndRotation(player);
        copyRotation();
        spawn();
    }

    private void copyRotation() {
        headYaw = player.headYaw;
        bodyYaw = player.bodyYaw;
    }

    private void spawn() {
        world.addEntity(getId(), this);
    }

    public void despawn() {
        discard();
    }

    public void resetPlayerPosition() {
        player.refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());
    }


}
