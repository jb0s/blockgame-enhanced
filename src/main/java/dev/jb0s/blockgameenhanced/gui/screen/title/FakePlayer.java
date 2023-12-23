package dev.jb0s.blockgameenhanced.gui.screen.title;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class FakePlayer extends ClientPlayerEntity {
    private Identifier skin;
    private Identifier cape;
    private Identifier elytra;

    public FakePlayer() {
        super(MinecraftClient.getInstance(), new FakeWorld(), new FakeClientPlayNetHandler(), new StatHandler(), new ClientRecipeBook(), false, false);

        MinecraftClient mc = MinecraftClient.getInstance();
        GameProfile profile = mc.getSession().getProfile();
        mc.getSkinProvider().loadSkin(profile, (type, resourceLocation, minecraftProfileTexture) -> {
            switch (type) {
                case SKIN -> skin = resourceLocation;
                case CAPE -> cape = resourceLocation;
                case ELYTRA -> elytra = resourceLocation;
            }
        }, true);
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public Identifier getSkinTexture() {
        if (skin == null)
            return DefaultSkinHelper.getTexture();

        return skin;
    }

    @Override
    public String getModel() {
        return "default";
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public boolean canRenderCapeTexture() {
        return cape != null;
    }

    @Nullable
    @Override
    public Identifier getCapeTexture() {
        return cape;
    }

    @Override
    public boolean isInvisibleTo(PlayerEntity entity) {
        return false;
    }

    @Override
    public float distanceTo(Entity entity) {
        return Float.MAX_VALUE;
    }

    @Override
    public double squaredDistanceTo(Entity entity) {
        return Float.MAX_VALUE;
    }
}