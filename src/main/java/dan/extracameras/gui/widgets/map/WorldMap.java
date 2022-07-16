package dan.extracameras.gui.widgets.map;

import dan.extracameras.utils.Variables;
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
        if(this.timeSinceLastUpdate - System.currentTimeMillis() < Variables.CameraOptions.mapUpdateRate) {
            update();
        }
    }

    public void update() {
        this.timeSinceLastUpdate = System.currentTimeMillis();
        new Thread(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
//        int chunks = MinecraftClient.getInstance().options.getViewDistance();
            int chunks = 8;
            World world = client.world;

            int startPoint = (int) client.player.getChunkPos().getStartX();
            int startY = (int) client.player.getChunkPos().getStartZ();
            int endPoint = client.player.getChunkPos().getEndX();
            int endY = client.player.getChunkPos().getEndZ();

            for (int i = startPoint; i <= endPoint; i++) {
                for (int j = startY; j <= endY; j++) {
                    Block result = null;
                    int y = 0;
                    for (int k = 256; k >= 0; k--) {
                        BlockPos pos = new BlockPos(i, k, j);
                        BlockState block = world.getBlockState(pos);
                        if (!block.getBlock().equals(Blocks.AIR)) {
                            result = block.getBlock();
                            y = k;
                            break;
                        } else if(k == 0) {
                            result = block.getBlock();
                            y = 0;
                            break;
                        }
                    }
                    String position = i + "-" + j;
                    Position point = map.get(position);
                    if (point == null) {
                        map.put(position, new Position(i, y, j, result.getName().getString()));
                    } else {
                        map.replace(position, new Position(i, y, j, result.getName().getString()));
                    }
                }
            }
        }).start();
    }
}
