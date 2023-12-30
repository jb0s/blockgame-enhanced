package dev.jb0s.blockgameenhanced.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MathHelper {
    public static final float EPSILON = 0.00001f;

    public static Vec3d clampMagnitude(Vec3d vec, double minRadius, double maxRadius) {
        double len = vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
        if(len == 0.0) return vec;

        double x = maxRadius * maxRadius;
        if(len > x) {
            double scalar = Math.sqrt(x / len);
            return new Vec3d(vec.x * scalar, vec.y * scalar, vec.z * scalar);
        }

        double y = minRadius * minRadius;
        if(len < y) {
            double scalar = Math.sqrt(y / len);
            return new Vec3d(vec.x * scalar, vec.y * scalar, vec.z * scalar);
        }

        return vec;
    }

    /*
     * Credit to LukenSkyne for most of these, I suck ass at maths
     */
    public static Vector3f worldToScreenSpace(Vec3d pos, Matrix4f modelViewMatrix, Matrix4f projMatrix) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        GameRenderer gameRenderer = minecraft.gameRenderer;
        Camera cam = gameRenderer.getCamera();

        Vector4f x = new Vector4f(cam.getPos().negate().add(pos).toVector3f(), 1f);
        x.mul(modelViewMatrix);
        x.mul(projMatrix);

        if(x.w != 0f) {
            x.div(x.w);
        }

        Window w = minecraft.getWindow();

        return new Vector3f(
                w.getWidth() * (0.5f + x.x * 0.5f),
                w.getHeight() * (0.5f - x.y * 0.5f),
                x.w
        );
    }
    public static void rotateZ(MatrixStack matrices, float theta) {
        matrices.multiplyPositionMatrix(new Matrix4f().rotateZ(theta));
    }
}
