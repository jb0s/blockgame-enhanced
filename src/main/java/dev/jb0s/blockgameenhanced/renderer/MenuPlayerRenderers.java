package dev.jb0s.blockgameenhanced.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

public class MenuPlayerRenderers {
    public static PlayerEntityRenderer fakePlayerRenderer = null;
    public static PlayerEntityRenderer fakePlayerRendererSlim = null;

    public static void createPlayerRenderers(EntityRendererFactory.Context context) {
        fakePlayerRenderer = new PlayerEntityRenderer(context, false);
        fakePlayerRendererSlim = new PlayerEntityRenderer(context, true);
    }
}
