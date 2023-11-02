package dev.jb0s.blockgameenhanced.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class BlockPosDebugRenderer implements DebugRenderer.Renderer {

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        World world = MinecraftClient.getInstance().world;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(world == null || player == null) {
            return;
        }

        RenderSystem.enableBlend();
        //DebugRenderer.drawBox(player.getBlockPos(), 0.001f, 0.0f, 0.0f, 0.5f, 0.5f);
        RenderSystem.disableBlend();
    }
}
