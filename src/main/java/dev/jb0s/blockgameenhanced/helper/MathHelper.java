package dev.jb0s.blockgameenhanced.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
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
        Quaternion y = new Quaternion((float) x.x, (float) x.y, (float) x.z, 1.0f);
        Quaternion z = quatProduct(projMatrix, quatProduct(modelViewMatrix, y));

        if(z.getW() <= 0f) {
            return null;
        }

        Window w = minecraft.getWindow();
        Quaternion sp = quatToScreen(z);
        float a = sp.getX() * w.getWidth();
        float b = sp.getY() * w.getHeight();

        if(Float.isInfinite(a) || Float.isInfinite(b)) {
            return null;
        }

        return new Vector4f(a, w.getHeight() - b, sp.getZ(), 1.0f / (sp.getW() * 2.0f));
    }
    private static Quaternion quatProduct(Matrix4f m4f, Quaternion quat) {
        var m = BufferUtils.createFloatBuffer(16);
        m4f.writeRowMajor(m);

        return new Quaternion(
                m.get(0) * quat.getX() + m.get(1) * quat.getY() + m.get(2) * quat.getZ() + m.get(3) * quat.getW(),
                m.get(4) * quat.getX() + m.get(5) * quat.getY() + m.get(6) * quat.getZ() + m.get(7) * quat.getW(),
                m.get(8) * quat.getX() + m.get(9) * quat.getY() + m.get(10) * quat.getZ() + m.get(11) * quat.getW(),
                m.get(12) * quat.getX() + m.get(13) * quat.getY() + m.get(14) * quat.getZ() + m.get(15) * quat.getW()
        );
    }
    private static Quaternion quatToScreen(Quaternion quat) {
        var w = 1f / quat.getW() * 0.5f;

        return new Quaternion(
                quat.getX() * w + 0.5f,
                quat.getY() * w + 0.5f,
                quat.getZ() * w + 0.5f,
                w);
    }
    public static void rotateZ(MatrixStack matrices, float theta) {
        var m = BufferUtils.createFloatBuffer(16);
        var m4f = new Matrix4f();

        m.put((float)Math.cos(theta)); m.put(-(float)Math.sin(theta)); m.put(0f); m.put(0f);
        m.put((float)Math.sin(theta)); m.put((float)Math.cos(theta)); m.put(0f); m.put(0f);
        m.put(0f); m.put(0f); m.put(1f); m.put(0f);
        m.put(0f); m.put(0f); m.put(0f); m.put(1f);

        m4f.readRowMajor(m);
        matrices.multiplyPositionMatrix(m4f);
    }
}
