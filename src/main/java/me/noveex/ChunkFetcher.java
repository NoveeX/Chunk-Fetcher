package me.noveex;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunkFetcher implements ModInitializer {
	public static final String MOD_ID = "chunk-fetcher";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		LOGGER.info("Chunk-Fetcher initialized!");
		Analyzer analyzer = new Analyzer();
		// Register '/chunkfetcher' command
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
					CommandManager.literal("chunkfetcher")
							// Add 'detailed' optional argument
							.then(CommandManager.argument("detailed", BoolArgumentType.bool())
									// Add 'radius' optional argument
									.then(CommandManager.argument("radius", IntegerArgumentType.integer(0, 32))
											.executes(context -> {
												// Fetch both arguments
												boolean detailed = BoolArgumentType.getBool(context, "detailed");
												int radius = IntegerArgumentType.getInteger(context, "radius");
												// Call analyzeLoadedChunks with both arguments
												analyzer.analyzeLoadedChunks(detailed, radius);
												return 1;
											}))
									.executes(context -> {
										boolean detailed = BoolArgumentType.getBool(context, "detailed");
										// Default radius value (e.g., 100)
										analyzer.analyzeLoadedChunks(detailed, 8);
										return 1;
									}))
							.executes(context -> {
								analyzer.analyzeLoadedChunks(false, 8);
								return 1;
							})
			);
		}));
	}
}