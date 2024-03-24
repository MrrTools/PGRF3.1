package solids;

import lwjglutils.OGLBuffers;

public class Grid extends Solid {

    public Grid(int m, int n) {
        // vb
        float[] vb = new float[2 * m * n];

        // ib
        int[] ib = new int[2 * 3 * (m - 1) * (n -1)];

        // naplnit vb
        // rozsah <0; 1>
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                vb[index++] = j / (float) (m - 1);
                vb[index++] = i / (float) (n - 1);
            }
        }

        // naplnit ib
        index = 0;
        for (int i = 0; i < n - 1; i++) {
            int offset = i * n;
            for (int j = 0; j < m - 1; j++) {
                ib[index++] = j + offset;
                ib[index++] = j + m + offset;
                ib[index++] = j + 1 + offset;

                ib[index++] = j + 1 + offset;
                ib[index++] = j + m + offset;
                ib[index++] = j + m + 1 + offset;
            }

        }

        // attributes
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2)
        };

        // buffers
        buffers = new OGLBuffers(vb, attributes, ib);
    }
}
