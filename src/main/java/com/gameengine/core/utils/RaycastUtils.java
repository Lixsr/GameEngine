package com.gameengine.core.utils;

import com.gameengine.core.entity.Entity;
import org.joml.Vector3f;

import java.util.List;

public class RaycastUtils {
    public static Vector3f raycastBlockHitPosition(Vector3f origin, Vector3f rayDirection, float rayLength, float stepSize, List<Entity> entities) {
        Entity closestEntity = null;
        float closestTMin = Float.POSITIVE_INFINITY;
        String closestFace = null;

        for (Entity entity : entities) {
            Vector3f entityPos = entity.getPos();
            float blockSize = 1.0f;
            Vector3f min = new Vector3f(entityPos).sub(blockSize / 2.0f, blockSize / 2.0f, blockSize / 2.0f);
            Vector3f max = new Vector3f(entityPos).add(blockSize / 2.0f, blockSize / 2.0f, blockSize / 2.0f);

            // Quick AABB check to skip non-intersecting blocks
            if (rayIntersectsAABB(origin, rayDirection, min, max, rayLength) < 0) {
                continue;
            }
            // Define the six faces of the AABB
            Vector3f[] faceNormals = {
                    new Vector3f(1, 0, 0),  // Right (+x)
                    new Vector3f(-1, 0, 0), // Left (-x)
                    new Vector3f(0, 1, 0),  // Top (+y)
                    new Vector3f(0, -1, 0), // Bottom (-y)
                    new Vector3f(0, 0, 1),  // Front (+z)
                    new Vector3f(0, 0, -1)  // Back (-z)
            };
            Vector3f[] facePoints = {
                    new Vector3f(max.x, entityPos.y, entityPos.z), // Right
                    new Vector3f(min.x, entityPos.y, entityPos.z), // Left
                    new Vector3f(entityPos.x, max.y, entityPos.z), // Top
                    new Vector3f(entityPos.x, min.y, entityPos.z), // Bottom
                    new Vector3f(entityPos.x, entityPos.y, max.z), // Front
                    new Vector3f(entityPos.x, entityPos.y, min.z)  // Back
            };
            String[] faceNames = {"right", "left", "top", "bottom", "front", "back"};
            // Check each face
            for (int i = 0; i < 6; i++) {
                Vector3f normal = faceNormals[i];
                Vector3f pointOnPlane = facePoints[i];
                String faceName = faceNames[i];

                float t = rayPlaneIntersection(origin, rayDirection, normal, pointOnPlane);
                if (t >= 0 && t <= rayLength && t < closestTMin) {
                    Vector3f hitPoint = new Vector3f(rayDirection).mul(t).add(origin);
                    if (isPointOnFace(hitPoint, min, max, i)) {
                        closestTMin = t;
                        closestEntity = entity;
                        closestFace = faceName;
                    }
                }
            }
        }

        if (closestEntity != null) {
            System.out.println("Hit entity at: " + closestEntity.getPos() + ", face: " + closestFace);
            return new Vector3f(closestEntity.getPos());
        }

        return null;
    }
    public static float rayIntersectsAABB(Vector3f rayOrigin, Vector3f rayDirection, Vector3f min, Vector3f max, float rayLength) {
        Vector3f dir = new Vector3f(rayDirection).normalize();
        Vector3f invDir = new Vector3f(
                Math.abs(dir.x) > 1e-6 ? 1.0f / dir.x : (dir.x >= 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY),
                Math.abs(dir.y) > 1e-6 ? 1.0f / dir.y : (dir.y >= 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY),
                Math.abs(dir.z) > 1e-6 ? 1.0f / dir.z : (dir.z >= 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY)
        );

        float tMin = (min.x - rayOrigin.x) * invDir.x;
        float tMax = (max.x - rayOrigin.x) * invDir.x;
        if (tMin > tMax) {
            float temp = tMin;
            tMin = tMax;
            tMax = temp;
        }

        float tYMin = (min.y - rayOrigin.y) * invDir.y;
        float tYMax = (max.y - rayOrigin.y) * invDir.y;
        if (tYMin > tYMax) {
            float temp = tYMin;
            tYMin = tYMax;
            tYMax = temp;
        }

        if (tMin > tYMax || tYMin > tMax) {
            return -1;
        }

        tMin = Math.max(tMin, tYMin);
        tMax = Math.min(tMax, tYMax);

        float tZMin = (min.z - rayOrigin.z) * invDir.z;
        float tZMax = (max.z - rayOrigin.z) * invDir.z;
        if (tZMin > tZMax) {
            float temp = tZMin;
            tZMin = tZMax;
            tZMax = temp;
        }

        if (tMin > tZMax || tZMin > tMax) {
            return -1;
        }

        tMin = Math.max(tMin, tZMin);
        tMax = Math.min(tMax, tZMax);

        if (tMin >= 0 && tMin <= rayLength) {
            return tMin;
        }

        return -1;
    }
    public static float rayPlaneIntersection(Vector3f rayOrigin, Vector3f rayDirection, Vector3f planeNormal, Vector3f pointOnPlane) {
        Vector3f dir = new Vector3f(rayDirection).normalize();
        float denom = planeNormal.dot(dir);
        if (Math.abs(denom) < 1e-4) { // Increase tolerance
            return -1;
        }
        Vector3f vec = new Vector3f(pointOnPlane).sub(rayOrigin);
        float t = vec.dot(planeNormal) / denom;
        if (t >= 0) {
            return t;
        }
        return -1;
    }
    public static boolean isPointOnFace(Vector3f hitPoint, Vector3f min, Vector3f max, int faceIndex) {
        float epsilon = 1e-4f; // Increase tolerance

        return switch (faceIndex) {
            case 0 -> // Right (+x)
                    Math.abs(hitPoint.x - max.x) < epsilon &&
                            hitPoint.y >= min.y - epsilon && hitPoint.y <= max.y + epsilon &&
                            hitPoint.z >= min.z - epsilon && hitPoint.z <= max.z + epsilon;
            case 1 -> // Left (-x)
                    Math.abs(hitPoint.x - min.x) < epsilon &&
                            hitPoint.y >= min.y - epsilon && hitPoint.y <= max.y + epsilon &&
                            hitPoint.z >= min.z - epsilon && hitPoint.z <= max.z + epsilon;
            case 2 -> // Top (+y)
                    Math.abs(hitPoint.y - max.y) < epsilon &&
                            hitPoint.x >= min.x - epsilon && hitPoint.x <= max.x + epsilon &&
                            hitPoint.z >= min.z - epsilon && hitPoint.z <= max.z + epsilon;
            case 3 -> // Bottom (-y)
                    Math.abs(hitPoint.y - min.y) < epsilon &&
                            hitPoint.x >= min.x - epsilon && hitPoint.x <= max.x + epsilon &&
                            hitPoint.z >= min.z - epsilon && hitPoint.z <= max.z + epsilon;
            case 4 -> // Front (+z)
                    Math.abs(hitPoint.z - max.z) < epsilon &&
                            hitPoint.x >= min.x - epsilon && hitPoint.x <= max.x + epsilon &&
                            hitPoint.y >= min.y - epsilon && hitPoint.y <= max.y + epsilon;
            case 5 -> // Back (-z)
                    Math.abs(hitPoint.z - min.z) < epsilon &&
                            hitPoint.x >= min.x - epsilon && hitPoint.x <= max.x + epsilon &&
                            hitPoint.y >= min.y - epsilon && hitPoint.y <= max.y + epsilon;
            default -> false;
        };
    }
}
