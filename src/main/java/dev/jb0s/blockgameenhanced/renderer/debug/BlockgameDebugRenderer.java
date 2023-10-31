package dev.jb0s.blockgameenhanced.renderer.debug;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class BlockgameDebugRenderer extends DebugRenderer {
    private AdventureZoneDebugRenderer adventureZoneDebugRenderer;
    private BlockPosDebugRenderer blockPosDebugRenderer;

    public BlockgameDebugRenderer(MinecraftClient client) {
        super(client);
        adventureZoneDebugRenderer = new AdventureZoneDebugRenderer();
        blockPosDebugRenderer = new BlockPosDebugRenderer();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        super.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);

        //adventureZoneDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        //blockPosDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }
}
