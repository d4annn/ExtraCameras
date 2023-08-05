package dan.extracameras.gui.widgets.map;

import dan.extracameras.config.Config;
import dan.extracameras.utils.Instance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class WorldMap {

    private Map<String, Position> map;
    private long timeSinceLastUpdate;

    public WorldMap() {
        map = new HashMap<>();
        this.timeSinceLastUpdate = System.currentTimeMillis();
    }

    private void initIfExists() {
        //TODO: this
    }

    public void tryUpdate() {
        if(this.timeSinceLastUpdate - System.currentTimeMillis() < Config.getInstance().mapUpdateRate) {
            update();
        }
    }

    public void update() {
        this.timeSinceLastUpdate = System.currentTimeMillis();

    }
}
