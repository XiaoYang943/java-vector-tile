package no.ecc.vectortile;

import org.locationtech.jts.geom.*;

/**
 * @program: java-vector-tile
 * @description:
 * @author: hyy
 * @create: 2024-05-17
 **/
public class Bbox {
    public final double xmin, ymin, xmax, ymax;

    public Bbox(double xmin, double ymin, double xmax, double ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    public Bbox(Geometry geometry) {
        double xmin, ymin, xmax, ymax;
        Geometry envelope = geometry.getEnvelope();
        if (envelope instanceof LineString) {
            Coordinate[] coords = envelope.getCoordinates();
            Coordinate coord = coords[0];
            xmin = coord.x;
            ymin = coord.y;
            xmax = coord.x;
            ymax = coord.y;
            coord = coords[1];
            if (coord.x > xmax) {
                xmax = coord.x;
            } else {
                xmin = coord.x;
            }
            if (coord.y > ymax) {
                ymax = coord.y;
            } else {
                ymin = coord.y;
            }
        } else {
            Coordinate[] coords = envelope.getCoordinates();
            if (coords.length == 1) {
                Coordinate c = coords[0];
                xmin = c.x;
                ymin = c.y;
                xmax = c.x;
                ymax = c.y;
            } else {
                Coordinate low = coords[0];
                xmin = low.x;
                ymin = low.y;
                Coordinate up = coords[2];
                xmax = up.x;
                ymax = up.y;
            }
        }
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    /**
     * 判断bbox是否与geometry的bbox
     *
     * @param geometry geometry
     * @return 是否相交
     */
    public boolean envIntersects( Geometry geometry) {
        Bbox bbox = new Bbox(geometry);
        return intersects(bbox);
    }

    /**
     * 判断bbox是否与另一个bbox相交
     *
     * @param bbox bbox
     * @return 是否相交
     */
    public boolean intersects( Bbox bbox) {
        return intersects(bbox.xmin, bbox.ymin, bbox.xmax, bbox.ymax);
    }

    /**
     * 判断bbox是否与范围相交
     *
     * @param xmin xmin
     * @param ymin ymin
     * @param xmax xmax
     * @param ymax ymax
     * @return 是否相交
     */
    public boolean intersects(double xmin, double ymin, double xmax, double ymax) {
        if (this.xmin > xmax) {
            return false;
        }
        if (this.xmax < xmin) {
            return false;
        }
        if (this.ymin > ymax) {
            return false;
        }
        return !(this.ymax < ymin);
    }

    /**
     * 判断bbox是否与点相交
     *
     * @param x x
     * @param y y
     * @return 是否相交
     */
    public boolean intersects(double x, double y) {
        return x >= xmin && x <= xmax && y >= ymin && y <= ymax;
    }


    public Polygon toPolygon( GeometryFactory gf) {
        return gf.createPolygon(new Coordinate[]{
                new Coordinate(xmin, ymin),
                new Coordinate(xmax, ymin),
                new Coordinate(xmax, ymax),
                new Coordinate(xmin, ymax),
                new Coordinate(xmin, ymin)
        });
    }
}

