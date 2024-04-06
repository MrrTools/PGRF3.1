package solids;

import lwjglutils.OGLBuffers;

public class Grid extends Solid {
    private float[] vb;
    private int[] ib;
    private int[] ibs;

    public Grid(int m, int n, GridEnum gridEnum) {
        VertexBuffer(m, n);
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2)
        };

        switch (gridEnum) {
            case STRIP -> {
                IndexBufferStrip(m, n);
                buffers = new OGLBuffers(vb, attributes, ibs);
            }
            case LIST -> {
                IndexBuffer(m, n);
                buffers = new OGLBuffers(vb, attributes, ib);
            }
        }

    }
    private void VertexBuffer(final int n, final int m) {
        // vb
        vb = new float[2 * m * n];
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                vb[index++] = j / (float) (m - 1);
                vb[index++] = i / (float) (n - 1);
            }
        }
    }

    private void IndexBuffer(int m, int n) {
        ib = new int[2 * 3 * (m - 1) * (n -1)];

        int index = 0;
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
    }

    private void IndexBufferStrip(int m, int n) {
        ibs = new int[2 * m * (n - 1) + (n - 2)];

        int index = 0;
        for (int i = 0; i < m - 1; i++) {
            int offset = i * m;
            for (int j = 0; j < n - 1; j++) {
                if (j == 0) {
                    ibs[index++] = j + offset;
                    ibs[index++] = (j + n) + offset;
                }
                ibs[index++] = (j + 1) + offset;
                ibs[index++] = (j + n + 1) + offset;
            }
            if (i != m - 2)
                ibs[index++] = 65535;
        }
    }
}




