package dan.extracameras.mixins;

import dan.extracameras.utils.WorldUtils;
import dan.extracameras.objects.JoinEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

    @Inject(method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;)V", at = @At("HEAD"))
    private void onConnect(MinecraftClient client, ServerAddress address, CallbackInfo ci) {
        WorldUtils.onJoin(new JoinEvent(address.getAddress(), true));
    }
}
