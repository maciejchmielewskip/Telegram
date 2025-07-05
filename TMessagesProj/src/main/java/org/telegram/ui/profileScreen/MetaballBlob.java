package org.telegram.ui.profileScreen;

import android.graphics.Path;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

public class MetaballBlob {

    public static class Ball {
        public float x, y, r;
        public Ball(float x, float y, float r) { this.x = x; this.y = y; this.r = r; }
    }

    public final List<Ball> balls = new ArrayList<>();
    public float threshold = 1.5f;
    public int gridStep = 5;

    public List<Path> computePaths(int w, int h) {
        final int offsetY = 200;

        // 1. Shift balls DOWN by offsetY
        for (Ball ball : balls) {
            ball.y += offsetY;
        }

        int nx = w / gridStep + 2, ny = h / gridStep + 2;
        float[][] grid = new float[nx][ny];
        boolean[][] visited = new boolean[nx][ny];
        List<Path> contours = new ArrayList<>();

        // 2. Compute field grid (no offset, already adjusted balls)
        for (int ix = 0; ix < nx; ix++)
            for (int iy = 0; iy < ny; iy++)
                grid[ix][iy] = field(ix * gridStep, iy * gridStep);

        // 3. Marching Squares for all contours
        for (int iy = 0; iy < ny - 1; iy++) {
            for (int ix = 0; ix < nx - 1; ix++) {
                int state = 0;
                if (grid[ix][iy] > threshold)     state |= 1;
                if (grid[ix+1][iy] > threshold)   state |= 2;
                if (grid[ix+1][iy+1] > threshold) state |= 4;
                if (grid[ix][iy+1] > threshold)   state |= 8;
                if (state == 0 || state == 15 || visited[ix][iy]) continue;

                Path contour = traceContour(ix, iy, grid, visited, nx, ny);
                if (contour != null) {
                    // Move path back UP by offsetY!
                    contour.offset(0, -offsetY);
                    contours.add(contour);
                }
            }
        }

        // 4. Restore Ball positions
        for (Ball ball : balls) {
            ball.y -= offsetY;
        }

        return contours;
    }

    // Metaball field (no change)
    private float field(float x, float y) {
        float s = 0.0f;
        for (Ball ball : balls) {
            float dx = x - ball.x, dy = y - ball.y;
            s += ball.r * ball.r / (dx * dx + dy * dy + 1f);
        }
        return s;
    }

    // Marching Squares - single blob
    private Path traceContour(int ix, int iy, float[][] grid, boolean[][] visited, int nx, int ny) {
        int[] dx = {1, 0, -1, 0}, dy = {0, 1, 0, -1};
        float[] gx = new float[]{0, 1, 1, 0};
        float[] gy = new float[]{0, 0, 1, 1};

        ArrayList<PointF> points = new ArrayList<>();
        int x = ix, y = iy;
        int safety = 0, maxSafety = nx * ny * 4;

        do {
            visited[x][y] = true;

            int state = 0;
            if (grid[x][y] > threshold)     state |= 1;
            if (grid[x+1][y] > threshold)   state |= 2;
            if (grid[x+1][y+1] > threshold) state |= 4;
            if (grid[x][y+1] > threshold)   state |= 8;

            for (int edge = 0; edge < 4; edge++) {
                int i = edge, j = (edge + 1) % 4;
                boolean inside1 = ((state >> i) & 1) != 0;
                boolean inside2 = ((state >> j) & 1) != 0;
                if (inside1 != inside2) {
                    float fx1 = (x + gx[i]) * gridStep;
                    float fy1 = (y + gy[i]) * gridStep;
                    float fx2 = (x + gx[j]) * gridStep;
                    float fy2 = (y + gy[j]) * gridStep;
                    float v1 = grid[x + (int)gx[i]][y + (int)gy[i]];
                    float v2 = grid[x + (int)gx[j]][y + (int)gy[j]];
                    float alpha = (threshold - v1) / (v2 - v1 + 1e-12f);
                    float px = fx1 + alpha * (fx2 - fx1);
                    float py = fy1 + alpha * (fy2 - fy1);
                    if (points.isEmpty() || Math.abs(points.get(0).x - px) > 1e-2 || Math.abs(points.get(0).y - py) > 1e-2)
                        points.add(new PointF(px, py));
                }
            }

            boolean moved = false;
            for (int ndir = 0; ndir < 4; ndir++) {
                int nx_ = x + dx[ndir], ny_ = y + dy[ndir];
                if (nx_ < 0 || nx_ >= nx-1 || ny_ < 0 || ny_ >= ny-1) continue;
                if (visited[nx_][ny_]) continue;
                int st = 0;
                if (grid[nx_][ny_] > threshold) st |= 1;
                if (grid[nx_+1][ny_] > threshold) st |= 2;
                if (grid[nx_+1][ny_+1] > threshold) st |= 4;
                if (grid[nx_][ny_+1] > threshold) st |= 8;
                if (st != 0 && st != 15) {
                    x = nx_; y = ny_;
                    moved = true;
                    break;
                }
            }
            if (!moved) break;
        } while (++safety < maxSafety && !(x == ix && y == iy && points.size() > 2));

        if (points.size() < 3) return null;
        Path path = new Path();
        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); ++i)
            path.lineTo(points.get(i).x, points.get(i).y);
        path.close();
        return path;
    }
}