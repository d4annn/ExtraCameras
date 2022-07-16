package dan.extracameras.mixins;

import dan.extracameras.packets.Packet;
import dan.extracameras.utils.Variables;
import dan.extracameras.utils.WorldUtils;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "disconnect*", at = @At("HEAD"))
    private void setDefault(CallbackInfo ci) {
        WorldUtils.onLeave();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!Variables.packets.isEmpty()) {
            for (Packet packet : Variables.packets) {
                packet.execute();
            }
            Variables.packets.clear();
        }
    }
}
