package me.x150.renderer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;

import java.util.Stack;

public class ClipStack {
    static final Stack<Rectangle> clipStack = new Stack<>();

    /**
     * <p>Adds a clipping window to the stack</p>
     * <p>All new rendered elements will only be rendered if they conform to this rectangle and the others above it</p>
     * <strong>Always call {@link #popWindow()} after you're done rendering with this</strong>
     *
     * @param stack The context MatrixStack
     * @param rect  The new clipping rectangle to enlist
     */
    public static void addWindow(MatrixStack stack, Rectangle r1) {
        Matrix4f matrix = stack.peek().getPositionMatrix();
        Vector4f coord = new Vector4f((float) r1.getX(), (float) r1.getY(), 0, 1);
        Vector4f end = new Vector4f((float) r1.getX1(), (float) r1.getY1(), 0, 1);
        coord.transform(matrix);
        end.transform(matrix);
        double x = coord.getX();
        double y = coord.getY();
        double endX = end.getX();
        double endY = end.getY();
        Rectangle r = new Rectangle(x, y, endX, endY);
        if (clipStack.empty()) {
            clipStack.push(r);
            Renderer2d.beginScissor(r.getX(), r.getY(), r.getX1(), r.getY1());
        } else {
            Rectangle lastClip = clipStack.peek();
            double lsx = lastClip.getX();
            double lsy = lastClip.getY();
            double lstx = lastClip.getX1();
            double lsty = lastClip.getY1();
            double nsx = MathHelper.clamp(r.getX(), lsx, lstx);
            double nsy = MathHelper.clamp(r.getY(), lsy, lsty);
            double nstx = MathHelper.clamp(r.getX1(), nsx, lstx);
            double nsty = MathHelper.clamp(r.getY1(), nsy, lsty); // totally intended varname
            clipStack.push(new Rectangle(nsx, nsy, nstx, nsty));
            Renderer2d.beginScissor(nsx, nsy, nstx, nsty);
        }
    }

    /**
     * Adds a window using {@link #addWindow(MatrixStack, Rectangle)}, calls renderAction, then removes the previously added window automatically.
     * <p></p>
     * You can replace this by separate {@link #addWindow(MatrixStack, Rectangle)} and {@link #popWindow()} calls, although using this method will do that for you.
     *
     * @param stack        The context MatrixStack
     * @param clippingRect The clipping rectangle that should be applied to the renderAction
     * @param renderAction The actual render method, that renders the content
     */
    public static void use(MatrixStack stack, Rectangle clippingRect, Runnable renderAction) {
        addWindow(stack, clippingRect);
        renderAction.run();
        popWindow();
    }

    /**
     * <p>Pops the latest added window from the stack</p>
     */
    public static void popWindow() {
        clipStack.pop();
        if (clipStack.empty()) {
            Renderer2d.endScissor();
        } else {
            Rectangle r = clipStack.peek();
            Renderer2d.beginScissor(r.getX(), r.getY(), r.getX1(), r.getY1());
        }
    }

}
