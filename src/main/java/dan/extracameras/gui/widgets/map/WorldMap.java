package dan.extracameras.gui.widgets.map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dan.extracameras.config.Config;
import dan.extracameras.utils.Instance;
import dan.extracameras.utils.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WorldMap {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Set<Position> blocks;
    private long timeSinceLastUpdate;
    private MinecraftClient client;
    private boolean isScanning;
    private File file;


    public WorldMap() {
        blocks = new HashSet<>();
        this.timeSinceLastUpdate = System.currentTimeMillis();
        client = MinecraftClient.getInstance();
        isScanning = false;
        file = new File(Instance.MAP_FOLDER.getAbsolutePath() + "\\" + Instance.currentWorld + ".json");
//        initIfExists();
    }

    public void close() {
        saveMap();
    }

    private void initIfExists() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                Instance.map = null;
            }
        } else {

            try (FileReader reader = new FileReader(file)) {
                Gson gson = new GsonBuilder().create();
                Type setType = new TypeToken<Set<BlockPos>>() {
                }.getType();
                blocks.addAll(gson.fromJson(reader, setType));
            } catch (IOException e) {
                // Handle any errors that occur during file reading
                e.printStackTrace();
            }
        }
    }

    public void tryUpdate() {
        if (this.timeSinceLastUpdate - System.currentTimeMillis() < Config.getInstance().mapUpdateRate && !Instance.cameraOn) {
            isScanning = true;
            CompletableFuture.runAsync(this::update)
                    .whenComplete((result, throwable) -> {
                        isScanning = false;
                        saveMap();
                    });
        }
    }

    public void update() {
        this.timeSinceLastUpdate = System.currentTimeMillis();
        int renderDistanceChunks = client.options.viewDistance;

        int playerChunkX = (int) client.player.getX() >> 4;
        int playerChunkZ = (int) client.player.getZ() >> 4;
        World world = client.world;

        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; chunkX++) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; chunkZ++) {
                if (client.world.isChunkLoaded(chunkX, chunkZ)) {
                    int chunkY = 256;
                    BlockPos topBlockPos = new BlockPos((chunkX << 4) + 8, chunkY, (chunkZ << 4) + 8);

                    // Scan downwards to find the highest solid block
                    while (chunkY > 0) {
                        Block block = world.getBlockState(topBlockPos).getBlock();
                        if (block != Blocks.AIR && block != Blocks.VOID_AIR) System.out.println(block);
                        if (block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR) {
                            blocks.add(new Position(topBlockPos.getX(), topBlockPos.getY(), topBlockPos.getZ(), ItemUtils.getBlockName(block)));
                            System.out.println(blocks);
                            break;
                        }
                        topBlockPos = topBlockPos.down();
                        chunkY--;
                    }
                }
            }
        }
    }

    public void saveMap() {
        String json = gson.toJson(blocks);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
