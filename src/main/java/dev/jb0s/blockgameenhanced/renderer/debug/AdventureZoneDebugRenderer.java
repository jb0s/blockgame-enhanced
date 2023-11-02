package dev.jb0s.blockgameenhanced.renderer.debug;

import com.google.common.collect.Maps;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Map;
import java.util.Random;

public class AdventureZoneDebugRenderer implements DebugRenderer.Renderer {
    private final Map<String, Color> colors;

    public AdventureZoneDebugRenderer() {
        colors = Maps.newHashMap();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        World world = MinecraftClient.getInstance().world;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(world == null || player == null) {
            return;
        }

        // Render labels in all chunks
        for (AdventureZone x : BlockgameEnhancedClient.getAdventureZoneManager().getAdventureZonesByWorld(world)) {
            for (int[] y : x.getChunks()) {
                int startX = y[0] * 16;
                int startZ = y[1] * 16;

                ChunkPos chunkPos = player.getChunkPos();
                int dist = Math.abs(chunkPos.x - y[0]) + Math.abs(chunkPos.z - y[1]);
                boolean isSurroundingPlayer = dist == 1;

                if(isSurroundingPlayer) {
                    BlockPos start = new BlockPos(startX, x.getMinY(), startZ);
                    Color col = getOrCreateColor(x.getId());

                    /*RenderSystem.enableBlend();
                    DebugRenderer.drawBox(start, start.add(16, x.getMaxY() - x.getMinY(), 16), col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
                    RenderSystem.disableBlend();*/
                }

                //DebugRenderer.drawString(x.getId(), startX + 8, player.getEyeY() + 0.15f, startZ + 8, 0xFFFFFF);
                //DebugRenderer.drawString("(" + y[0] + ", " + y[1] + ")", startX + 8, player.getEyeY() - 0.15f, startZ + 8, 0xFFFFFF);
            }
        }
    }

    private Color getOrCreateColor(String zoneId) {
        if(!colors.containsKey(zoneId)) {
            Random rng = new Random();
            float r = rng.nextFloat() / 2f + 0.5f;
            float g = rng.nextFloat() / 2f + 0.5f;
            float b = rng.nextFloat() / 2f + 0.5f;
            Color col = new Color(r, g, b, 0.3f);
            colors.put(zoneId, col);
            return col;
        }

        return colors.get(zoneId);
    }
}
