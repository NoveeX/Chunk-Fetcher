package me.noveex;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatLogger {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static void log(String content){
        client.player.sendMessage(Text.literal("§1[Chunk Fetcher] §3    " + content));
        System.out.println("[Chunk Fetcher] > " + content);
    }
}
