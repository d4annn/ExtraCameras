package dan.extracameras.packets;

import dan.extracameras.utils.ErrorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigLoadPacket extends Packet {

    private String name;
    private boolean onJoinWorld;
    private List<String> deletedFiles;

    public ConfigLoadPacket(String name, boolean onJoinWorld, List<String> deletedFiles) {
        super(name);
        this.name = name;
        this.onJoinWorld = onJoinWorld;
        this.deletedFiles = deletedFiles;
    }

    public void execute() {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("[Extra Cameras] §4§lAn error ocurred while trying to load worlds config"));
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§4§lConflictive config files has been deleted, check logs for more info"));
        ErrorUtils.consoleWarn("An error ocurred while loading config, the following config files has been deleted:");
        deletedFiles.forEach(ErrorUtils::consoleInfo);
    }
}
