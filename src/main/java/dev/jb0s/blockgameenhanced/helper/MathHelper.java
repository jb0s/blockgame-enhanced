package dev.jb0s.blockgameenhanced.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

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
    public static Vector4f worldToScreenSpace(Vec3d pos, Matrix4f modelViewMatrix, Matrix4f projMatrix) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        GameRenderer gameRenderer = minecraft.gameRenderer;

        Vec3d x = gameRenderer.getCamera().getPos().negate().add(pos);
        Quaternionf y = new Quaternionf((float) x.x, (float) x.y, (float) x.z, 1.0f);
        Quaternionf z = quatProduct(projMatrix, quatProduct(modelViewMatrix, y));

        if(z.w() <= 0f) {
            return null;
        }

        Window w = minecraft.getWindow();
        Quaternionf sp = quatToScreen(z);
        float a = sp.x() * w.getWidth();
        float b = sp.y() * w.getHeight();

        if(Float.isInfinite(a) || Float.isInfinite(b)) {
            return null;
        }

        return new Vector4f(a, w.getHeight() - b, sp.z(), 1.0f / (sp.w() * 2.0f));
    }
    private static Quaternionf quatProduct(Matrix4f m4f, Quaternionf quat) {
        var m = BufferUtils.createFloatBuffer(16);
        //m4f.writeExternal();

        return new Quaternionf(
                m.get(0) * quat.x() + m.get(1) * quat.y() + m.get(2) * quat.z() + m.get(3) * quat.w(),
                m.get(4) * quat.x() + m.get(5) * quat.y() + m.get(6) * quat.z() + m.get(7) * quat.w(),
                m.get(8) * quat.x() + m.get(9) * quat.y() + m.get(10) * quat.z() + m.get(11) * quat.w(),
                m.get(12) * quat.x() + m.get(13) * quat.y() + m.get(14) * quat.z() + m.get(15) * quat.w()
        );
    }
    private static Quaternionf quatToScreen(Quaternionf quat) {
        var w = 1f / quat.w() * 0.5f;

        return new Quaternionf(
                quat.x() * w + 0.5f,
                quat.y() * w + 0.5f,
                quat.z() * w + 0.5f,
                w);
    }
    public static void rotateZ(MatrixStack matrices, float theta) {
        var m = BufferUtils.createFloatBuffer(16);
        var m4f = new Matrix4f();

        m.put((float)Math.cos(theta)); m.put(-(float)Math.sin(theta)); m.put(0f); m.put(0f);
        m.put((float)Math.sin(theta)); m.put((float)Math.cos(theta)); m.put(0f); m.put(0f);
        m.put(0f); m.put(0f); m.put(1f); m.put(0f);
        m.put(0f); m.put(0f); m.put(0f); m.put(1f);

        //m4f.readRowMajor(m);
        matrices.multiplyPositionMatrix(m4f);
    }
}
