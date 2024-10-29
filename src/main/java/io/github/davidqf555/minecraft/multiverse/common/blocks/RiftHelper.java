package io.github.davidqf555.minecraft.multiverse.common.blocks;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class RiftHelper {

    private RiftHelper() {
    }

    public static void placeExplosion(WorldGenLevel world, RandomSource rand, BlockState state, Optional<Integer> target, Optional<Pair<Vec3, Float>> rotation, Vec3 center, boolean drop) {
        world.levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, BlockPos.containing(center), 0);
        place(world, rand, state, target, rotation, center, drop);
    }

    public static void placeExplosion(WorldGenLevel world, RandomSource rand, BlockState state, Optional<Integer> target, Optional<Pair<Vec3, Float>> rotation, Vec3 center, double width, double height, boolean drop) {
        world.levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, BlockPos.containing(center), 0);
        place(world, rand, state, target, rotation, center, width, height, drop);
    }

    public static void place(WorldGenLevel world, RandomSource rand, BlockState state, Optional<Integer> target, Optional<Pair<Vec3, Float>> rotation, Vec3 center, boolean drop) {
        place(world, rand, state, target, rotation, center, ServerConfigs.INSTANCE.minRiftWidth.get(), ServerConfigs.INSTANCE.maxRiftWidth.get(), ServerConfigs.INSTANCE.minRiftHeight.get(), ServerConfigs.INSTANCE.maxRiftHeight.get(), drop);
    }

    public static void place(WorldGenLevel world, RandomSource rand, BlockState state, Optional<Integer> target, Optional<Pair<Vec3, Float>> rotation, Vec3 center, double minWidth, double maxWidth, double minHeight, double maxHeight, boolean drop) {
        double width = minWidth + rand.nextDouble() * (maxWidth - minWidth);
        double height = minHeight + rand.nextDouble() * (maxHeight - minHeight);
        place(world, rand, state, target, rotation, center, width, height, drop);
    }

    public static void place(WorldGenLevel world, RandomSource rand, BlockState state, Optional<Integer> target, Optional<Pair<Vec3, Float>> rotation, Vec3 center, double width, double height, boolean drop) {
        int t = target.orElseGet(() -> {
            int current = DimensionHelper.getIndex(world.getLevel().dimension());
            int dim = rand.nextInt(ServerConfigs.INSTANCE.maxDimensions.get());
            return dim < current ? dim : dim + 1;
        });
        Pair<Vec3, Float> r = rotation.orElseGet(() -> {
            Vec3 normal = new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()).normalize();
            float angle = rand.nextFloat() * 180;
            return Pair.of(normal, angle);
        });
        Vec3 normal = r.getFirst();
        if (normal.lengthSqr() == 0) {
            normal = new Vec3(0, 1, 0);
        }
        float angle = r.getSecond();
        place(world, world, state, t, center, normal, angle, width, height, drop);
    }

    public static void place(LevelWriter writer, LevelReader reader, BlockState state, int target, Vec3 center, Vec3 normal, float angle, double width, double height, boolean drop) {
        Vec3[][] vertices = calculateVertices(center, normal, angle, width, height);
        iterate(vertices, pos -> {
            if (canReplace(reader, pos)) {
                Vec3[] polygon = calculateSectionPolygon(vertices, normal, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos)));
                if (polygon.length >= 3) {
                    BlockState base = state;
                    Fluid fluid = reader.getFluidState(pos).getType();
                    if (drop) {
                        writer.destroyBlock(pos, true);
                    }
                    if (fluid == Fluids.WATER) {
                        base = base.setValue(RiftBlock.FLUID, RiftBlock.LoggedFluid.WATER);
                    } else if (fluid == Fluids.LAVA) {
                        base = base.setValue(RiftBlock.FLUID, RiftBlock.LoggedFluid.LAVA);
                    }
                    writer.setBlock(pos, base, 3);
                    BlockEntity tile = reader.getBlockEntity(pos);
                    if (tile instanceof RiftTileEntity) {
                        ((RiftTileEntity) tile).setTarget(target);
                        ((RiftTileEntity) tile).setVertices(polygon);
                        ((RiftTileEntity) tile).setNormal(normal);
                    }
                }
            }
        });
    }

    private static boolean canReplace(LevelReader reader, BlockPos pos) {
        return !reader.isOutsideBuildHeight(pos) && reader.getBlockState(pos).getDestroySpeed(reader, pos) != -1;
    }

    private static Vec3[][] calculateVertices(Vec3 center, Vec3 normal, float angle, double width, double height) {
        Vec3[] axis = new Vec3[2];
        axis[0] = normal.cross(new Vec3(0, 1, 0)).normalize();
        if (axis[0].lengthSqr() == 0) {
            axis[0] = new Vec3(1, 0, 0);
        }
        axis[1] = normal.cross(axis[0]).normalize();
        for (int i = 0; i < 2; i++) {
            Vec3 parallel = normal.scale(normal.dot(axis[i]) / normal.lengthSqr());
            Vec3 perp = axis[i].subtract(parallel);
            Vec3 cross = normal.cross(perp).normalize();
            Vec3 rotate = perp.scale(Mth.cos(angle * Mth.DEG_TO_RAD)).add(cross.scale(Mth.sin(angle * Mth.DEG_TO_RAD) * perp.length()));
            axis[i] = rotate.add(parallel);
        }
        Vec3[][] vertices = new Vec3[2][4];
        vertices[0][0] = center.add(axis[0].scale(width / 2));
        vertices[0][1] = center.add(axis[1].scale(height / 2));
        vertices[0][2] = center.add(axis[0].scale(-width / 2));
        vertices[0][3] = center.add(axis[1].scale(-height / 2));
        vertices[1][1] = vertices[0][0];
        vertices[1][2] = vertices[0][1];
        vertices[1][3] = vertices[0][2];
        vertices[1][0] = vertices[0][3];
        return vertices;
    }

    private static void iterate(Vec3[][] vertices, Consumer<BlockPos> effect) {
        double[] allY = Arrays.stream(vertices[0]).mapToDouble(Vec3::y).sorted().toArray();
        int i = 0;
        double minY = allY[0];
        double maxY = allY[allY.length - 1];
        for (int y = Mth.floor(minY); y <= maxY; y++) {
            List<Double> crit = new ArrayList<>();
            if (y >= minY) {
                crit.add((double) y);
            }
            int high = y + 1;
            while (i < allY.length && allY[i] < high) {
                if (crit.isEmpty() || crit.get(crit.size() - 1) != allY[i]) {
                    crit.add(allY[i]);
                }
                i++;
            }
            if (high <= maxY && (crit.isEmpty() || crit.get(crit.size() - 1) != high)) {
                crit.add((double) high);
            }
            List<Point2D> points = new ArrayList<>();
            for (double val : crit) {
                for (int j = 0; j < vertices[0].length; j++) {
                    Vec3 dir = vertices[1][j].subtract(vertices[0][j]);
                    if (dir.y() == 0) {
                        points.add(new Point2D(vertices[0][j].x(), vertices[0][j].z()));
                        points.add(new Point2D(vertices[1][j].x(), vertices[1][j].z()));
                    } else {
                        double t = (val - vertices[0][j].y()) / dir.y();
                        if (t >= 0 && t <= 1) {
                            points.add(new Point2D(vertices[0][j].x() + dir.x() * t, vertices[0][j].z() + dir.z() * t));
                        }
                    }
                }
            }
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            pos.setY(y);
            iterate2D(points, (x, z) -> {
                pos.setX(x);
                pos.setZ(z);
                effect.accept(pos);
            });
        }
    }

    private static void iterate2D(List<Point2D> points, BiConsumer<Integer, Integer> effect) {
        if (points.isEmpty()) {
            return;
        } else if (points.size() == 1) {
            Point2D point = points.get(0);
            effect.accept(Mth.floor(point.x()), Mth.floor(point.y()));
            return;
        }
        List<Pair<Point2D, Point2D>> lines = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            for (int j = i + 1; j < points.size(); j++) {
                lines.add(Pair.of(points.get(i), points.get(j)));
            }
        }
        double[] xVals = points.stream().mapToDouble(Point2D::x).sorted().toArray();
        int i = 0;
        for (int x = Mth.floor(xVals[0]); x <= xVals[xVals.length - 1]; x++) {
            List<Double> crit = new ArrayList<>();
            if (x >= xVals[0]) {
                crit.add((double) x);
            }
            int high = x + 1;
            while (i < xVals.length && xVals[i] < high) {
                if (crit.isEmpty() || crit.get(crit.size() - 1) != xVals[i]) {
                    crit.add(xVals[i]);
                }
                i++;
            }
            if (high <= xVals[xVals.length - 1] && (crit.isEmpty() || crit.get(crit.size() - 1) != high)) {
                crit.add((double) high);
            }
            // for extra security in case of floating point calculation issues or less than 2 points
            boolean found = false;
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            for (Pair<Point2D, Point2D> line : lines) {
                for (double val : crit) {
                    Point2D from = line.getFirst();
                    Point2D to = line.getSecond();
                    if (Math.min(from.x(), to.x()) <= val && Math.max(from.x(), to.x()) >= val) {
                        double yMin;
                        double yMax;
                        double dif = to.x() - from.x();
                        if (dif == 0) {
                            yMin = Math.min(from.y(), to.y());
                            yMax = Math.max(from.y(), to.y());
                        } else {
                            double slope = (to.y() - from.y()) / dif;
                            yMin = yMax = from.y() + slope * (val - from.x());
                        }
                        if (yMin < min) {
                            min = yMin;
                        }
                        if (yMax > max) {
                            max = yMax;
                        }
                        found = true;
                    }
                }
            }
            // should always be true
            if (found) {
                for (int y = Mth.floor(min); y <= max; y++) {
                    effect.accept(x, y);
                }
            }
        }
    }

    private static Vec3[] calculateSectionPolygon(Vec3[][] vertices, Vec3 normal, AABB bounds) {
        Set<Vec3> points = new HashSet<>();
        for (int i = 0; i < vertices[0].length; i++) {
            if (containsInclusive(vertices[0][i], bounds)) {
                points.add(vertices[0][i]);
            }
            bounds.clip(vertices[0][i], vertices[1][i]).ifPresent(points::add);
            bounds.clip(vertices[1][i], vertices[0][i]).ifPresent(points::add);
        }
        for (Vec3[] line : getLines(bounds)) {
            points.addAll(Arrays.asList(intersection(vertices, normal, line[0], line[1])));
        }
        if (points.isEmpty()) {
            return new Vec3[0];
        }
        Vec3[] out = points.toArray(Vec3[]::new);
        reorderConvex(out, normal);
        return out;
    }

    private static Vec3[] intersection(Vec3[][] vertices, Vec3 normal, Vec3 from, Vec3 to) {
        Vec3 dir = to.subtract(from);
        double dot = normal.dot(dir);
        if (dot == 0) {
            if (normal.dot(vertices[0][0]) != normal.dot(from)) {
                return new Vec3[0];
            }
            List<Vec3> points = new ArrayList<>();
            if (isCoplanarPointInside(vertices[0], from)) {
                points.add(from);
            } else {
                Vec3 closest = null;
                double lowDist = Double.MAX_VALUE;
                for (int i = 0; i < 4; i++) {
                    Vec3[] all = getCoplanarIntersection(vertices[0][i], vertices[1][i], from, to);
                    for (Vec3 intersection : all) {
                        double dist = intersection.distanceToSqr(from);
                        if (dist <= lowDist) {
                            closest = intersection;
                            lowDist = dist;
                        }
                    }
                }
                if (closest != null) {
                    points.add(closest);
                }
            }
            if (isCoplanarPointInside(vertices[0], to)) {
                points.add(to);
            } else {
                Vec3 closest = null;
                double lowDist = Double.MAX_VALUE;
                for (int i = 0; i < 4; i++) {
                    Vec3[] all = getCoplanarIntersection(vertices[0][i], vertices[1][i], to, from);
                    for (Vec3 intersection : all) {
                        double dist = intersection.distanceToSqr(to);
                        if (dist <= lowDist) {
                            closest = intersection;
                            lowDist = dist;
                        }
                    }
                }
                if (closest != null) {
                    points.add(closest);
                }
            }
            return points.toArray(Vec3[]::new);
        } else {
            double t = (normal.dot(vertices[0][0]) - normal.dot(from)) / dot;
            if (t >= 0 && t <= 1) {
                Vec3 intersection = from.add(dir.scale(t));
                if (isCoplanarPointInside(vertices[0], intersection)) {
                    return new Vec3[]{intersection};
                }
            }
            return new Vec3[0];
        }
    }

    // multiple intersection vertices when lines on top of each other
    private static Vec3[] getCoplanarIntersection(Vec3 start1, Vec3 end1, Vec3 start2, Vec3 end2) {
        Vec3 dir1 = end1.subtract(start1);
        Vec3 dir2 = end2.subtract(start2);
        double len1 = dir1.lengthSqr();
        double len2 = dir2.lengthSqr();
        if (len1 == 0 && len2 == 0) {
            return start1.equals(start2) ? new Vec3[]{start1} : new Vec3[0];
        } else if (len1 == 0) {
            return isPointOnLine(start1, start2, end2) ? new Vec3[]{start1} : new Vec3[0];
        } else if (len2 == 0) {
            return isPointOnLine(start2, start1, end1) ? new Vec3[]{start2} : new Vec3[0];
        }
        double dot = dir2.dot(dir1);
        double dif = len1 * len2 - dot * dot;
        if (dif == 0) {
            List<Vec3> valid = new ArrayList<>();
            if (isPointOnLine(start1, start2, end2)) {
                valid.add(start1);
            }
            if (isPointOnLine(start2, start2, end2)) {
                valid.add(start2);
            }
            if (isPointOnLine(end1, start1, end1)) {
                valid.add(end1);
            }
            if (isPointOnLine(end2, start1, end1)) {
                valid.add(end2);
            }
            switch (valid.size()) {
                case 0:
                    return new Vec3[0];
                case 1:
                    return new Vec3[]{valid.get(0)};
                case 2:
                    return valid.get(0).equals(valid.get(1)) ? new Vec3[]{valid.get(0)} : new Vec3[]{valid.get(0), valid.get(1)};
                default:
                    Vec3[] longest = new Vec3[2];
                    double maxDist = 0;
                    for (int i = 0; i < valid.size() - 1; i++) {
                        for (int j = i + 1; j < valid.size(); j++) {
                            Vec3 p1 = valid.get(i);
                            Vec3 p2 = valid.get(j);
                            double dist = p1.distanceToSqr(p2);
                            if (dist >= maxDist) {
                                longest[0] = p1;
                                longest[1] = p2;
                                maxDist = dist;
                            }
                        }
                    }
                    return longest;
            }
        } else {
            double start2Dot = start1.subtract(start2).dot(end2.subtract(start2));
            double start1Dot = start1.subtract(start2).dot(end1.subtract(start1));
            double factorA = (start2Dot * dot - start1Dot * len2) / dif;
            double factorB = (start2Dot + factorA * dot) / len2;
            if (factorA > 1 || factorA < 0 || factorB > 1 || factorB < 0) {
                return new Vec3[0];
            }
            return new Vec3[]{start1.add(dir1.scale(factorA))};
        }
    }

    private static boolean isCoplanarPointInside(Vec3[] vertices, Vec3 point) {
        if (vertices.length == 0) {
            return false;
        }
        if (vertices.length == 1) {
            return vertices[0].equals(point);
        }
        Vec3[] v = new Vec3[vertices.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = vertices[i].subtract(point);
        }
        Vec3 base = v[v.length - 1].cross(v[0]);
        for (int i = 1; i < v.length; i++) {
            Vec3 cross = v[i - 1].cross(v[i]);
            if (base.dot(cross) < 0) {
                return false;
            }
            base = cross;
        }
        return true;
    }

    private static boolean isPointOnLine(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 dir = end.subtract(start);
        double len = dir.lengthSqr();
        if (len == 0) {
            return start.equals(point);
        }
        double factor = (point.dot(dir) - start.dot(dir)) / len;
        Vec3 intersection = start.add(dir.scale(factor));
        return factor >= 0 && factor <= 1 && point.equals(intersection);
    }

    private static boolean containsInclusive(Vec3 point, AABB bounds) {
        return point.x() >= bounds.minX && point.x() <= bounds.maxX && point.y() >= bounds.minY && point.y() <= bounds.maxY && point.z() >= bounds.minZ && point.z() <= bounds.maxZ;
    }

    private static void reorderConvex(Vec3[] points, Vec3 normal) {
        if (points.length <= 1) {
            return;
        }
        Vec3 n = normal.normalize();
        Vec3 center = Arrays.stream(points).reduce(Vec3.ZERO, Vec3::add).scale(1.0 / points.length);
        Vec3 dir = points[0].subtract(center).normalize();
        Arrays.sort(points, (p1, p2) -> {
            Vec3 v1 = p1.subtract(center).normalize();
            Vec3 v2 = p2.subtract(center).normalize();
            double dot1 = v1.dot(dir);
            double dot2 = v2.dot(dir);
            double det1 = v1.cross(dir).dot(n);
            double det2 = v2.cross(dir).dot(n);
            // TODO: don't use atan2 to compare angles
            double angle1 = Mth.atan2(det1, dot1);
            double angle2 = Mth.atan2(det2, dot2);
            return Double.compare(angle1, angle2);
        });
    }

    public static boolean intersects(Vec3[][] points, Vec3 normal, AABB bounds) {
        for (Vec3 point : points[0]) {
            if (containsInclusive(point, bounds)) {
                return true;
            }
        }
        for (Vec3[] line : getLines(bounds)) {
            if (intersection(points, normal, line[0], line[1]).length != 0) {
                return true;
            }
        }
        return false;
    }

    private static Vec3[][] getLines(AABB bounds) {
        return new Vec3[][]{
                {new Vec3(bounds.minX, bounds.minY, bounds.minZ), new Vec3(bounds.maxX, bounds.minY, bounds.minZ)},
                {new Vec3(bounds.minX, bounds.minY, bounds.maxZ), new Vec3(bounds.maxX, bounds.minY, bounds.maxZ)},
                {new Vec3(bounds.minX, bounds.maxY, bounds.minZ), new Vec3(bounds.maxX, bounds.maxY, bounds.minZ)},
                {new Vec3(bounds.minX, bounds.maxY, bounds.maxZ), new Vec3(bounds.maxX, bounds.maxY, bounds.maxZ)},
                {new Vec3(bounds.minX, bounds.minY, bounds.minZ), new Vec3(bounds.minX, bounds.maxY, bounds.minZ)},
                {new Vec3(bounds.minX, bounds.minY, bounds.maxZ), new Vec3(bounds.minX, bounds.maxY, bounds.maxZ)},
                {new Vec3(bounds.maxX, bounds.minY, bounds.minZ), new Vec3(bounds.maxX, bounds.maxY, bounds.minZ)},
                {new Vec3(bounds.maxX, bounds.minY, bounds.maxZ), new Vec3(bounds.maxX, bounds.maxY, bounds.maxZ)},
                {new Vec3(bounds.minX, bounds.minY, bounds.minZ), new Vec3(bounds.minX, bounds.minY, bounds.maxZ)},
                {new Vec3(bounds.minX, bounds.maxY, bounds.minZ), new Vec3(bounds.minX, bounds.maxY, bounds.maxZ)},
                {new Vec3(bounds.maxX, bounds.minY, bounds.minZ), new Vec3(bounds.maxX, bounds.minY, bounds.maxZ)},
                {new Vec3(bounds.maxX, bounds.maxY, bounds.minZ), new Vec3(bounds.maxX, bounds.maxY, bounds.maxZ)}
        };
    }

    private record Point2D(double x, double y) {
    }

}
