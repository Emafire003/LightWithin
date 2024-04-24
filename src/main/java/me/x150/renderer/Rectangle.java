package me.x150.renderer;

import java.util.Locale;

//Removed unused methods

/**
 * Describes a rectangle
 */
public class Rectangle {

    private final double x, y, x1, y1;

    /**
     * Constructs a new rectangle. The coordinates provided will be normalized.
     *
     * @param minX Min X coordinate (left)
     * @param minY Min Y coordinate (top)
     * @param maxX Max X coordinate (right)
     * @param maxY Max Y coordinate (bottom)
     */
    public Rectangle(double minX, double minY, double maxX, double maxY) {
        double nx0 = Math.min(minX, maxX);
        double nx1 = Math.max(minX, maxX);
        double ny0 = Math.min(minY, maxY);
        double ny1 = Math.max(minY, maxY);
        this.x = nx0;
        this.y = ny0;
        this.x1 = nx1;
        this.y1 = ny1;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s{x=%f, y=%f, x1=%f, y1=%f}", getClass().getSimpleName(), this.x, this.y,
                this.x1, this.y1);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getX1() {
        return this.x1;
    }

    public double getY1() {
        return this.y1;
    }
}