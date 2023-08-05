package dan.extracameras.mixins;

import dan.extracameras.camera.CameraEntity;
import dan.extracameras.utils.Instance;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow
    public boolean noClip;

    @Shadow
    protected boolean submergedInWater;

    @Shadow
    protected boolean touchingWater;

    @Shadow
    @Nullable
    protected Tag<Fluid> submergedFluidTag;

    @Inject(method = "move", at = @At("HEAD"))
    private void noClip(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (Instance.cameraOn && (Entity) ((Object) this) instanceof ClientPlayerEntity) {
            this.noClip = true;
        }
    }

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void overrideYaw(double yawChange, double pitchChange, CallbackInfo ci) {
        if ((Object) this instanceof ClientPlayerEntity) {
            if (Instance.cameraOn) {
                CameraEntity.getCamera().updateCameraRotations((float) yawChange, (float) pitchChange);
            }
        }
    }
}
