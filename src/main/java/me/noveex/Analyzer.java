package me.noveex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;


import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Analyzer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public void analyzeLoadedChunks(boolean detailed, int radius){
        ClientWorld world = client.world;
        ClientPlayerEntity localPlayer = client.player;
        if(localPlayer == null){
            ChatLogger.log("§4Analyzation failed: no player found!");
            return;
        }
        if(world == null){
            ChatLogger.log("§4Analyzation failed: no world found!");
            return;
        }
        int renderDistance = client.options.getViewDistance().getValue();
        List<Map<String, Object>> allChunkData = new ArrayList<>();
        ChatLogger.log("§2Analyzation started! (Detailed)");
        ClientChunkManager chunkManager = world.getChunkManager();
        List<Chunk> chunks = new ArrayList<Chunk>();
        for (int x = -radius; x < radius; x++) {  // Modify range based on desired radius
            for (int z = -radius; z < radius; z++) {
                ChunkPos chunkPos = new ChunkPos(localPlayer.getChunkPos().x + x, localPlayer.getChunkPos().z + z);
                Chunk chunk = chunkManager.getWorldChunk(chunkPos.x, chunkPos.z);
                if (chunk == null) {
                    ChatLogger.log("§4Skipping chunk [X: " + x + "Z: " + z + "]: chunk could not be fetched from server!");
                    continue;
                }
                chunks.add(chunk);

            }
        }
        ChatLogger.log("§9Fetched §3" + chunks.size() + " §9chunks.");
        int i = 1;
        for(Chunk chunk : chunks){
            localPlayer.sendMessage(Text.literal("Analyzing chunk " + i + "/" + chunks.size() + "..."), true);
            Map<String, Object> chunkData = analyzeChunk(chunk, detailed);
            if (chunkData != null) {
                allChunkData.add(chunkData);
            }
            i++;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(allChunkData);


        // Save JSON to a file
        String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
        String finalName = "ChunkFetcherData_" + time + ".json";
        try (FileWriter writer = new FileWriter(finalName)) {
            writer.write(jsonOutput);
            ChatLogger.log("§aAll chunks analysis saved to §2" + finalName);
        } catch (IOException e) {
            e.printStackTrace();
            ChatLogger.log("§4Failed to save all chunks analysis!");
        }
    }

    private Map<String, Object> analyzeChunk(Chunk chunk, boolean detailed) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            return null;
        }

        Set<String> biomesInChunk = new HashSet<>();
        Map<String, Integer> blockCounts = new HashMap<>();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = world.getBottomY(); y < world.getTopY(); y++) {
                    // Biome analysation
                    RegistryEntry<Biome> biome = world.getBiome(chunk.getPos().getStartPos().add(x, y, z));
                    String biomeName = biome.getIdAsString();
                    biomesInChunk.add(biomeName);

                    // Block analysation
                    if(detailed){
                        BlockPos pos = chunk.getPos().getStartPos().add(x, y, z);
                        Block block = world.getBlockState(pos).getBlock();
                        String blockName = block.getRegistryEntry().registryKey().getValue().toString();
                        blockCounts.put(blockName, blockCounts.getOrDefault(blockName, 0) + 1);
                    }

                }
            }
        }
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("Chunk Position", Map.of(
                "x", chunk.getPos().x,
                "z", chunk.getPos().z
        ));
        jsonData.put("Biomes", biomesInChunk);

        if(detailed)
            jsonData.put("Blocks", blockCounts);

        return jsonData;

    }


}
