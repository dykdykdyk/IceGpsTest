package ice.rtk.utils.viewtools;

import static java.lang.Math.floor;

/**
 * Created by 111 on 2017/12/4.
 */

public class XYH {

    private final double RE_WGS84 = 6378137.0;     /* earth semimajor axis (WGS84) (m) */
    private final double FE_WGS84 = (1.0 / 298.257223563);/* earth flattening (WGS84) */
    private final double PI = 3.1415926535897932; /* pi */
    public final double D2R = (PI / 180.0);       /* deg to rad */
    public final double R2D = (180.0 / PI);      /* rad to deg */

    /* inner product ---------------------------------------------------------------
    * inner product of vectors
    * args   : double *a,*b     I   vector a,b (n x 1)
    *          int    n         I   size of vector a,b
    * return : a'*b
    *-----------------------------------------------------------------------------*/
    public double dot(double[] a, double[] b, int n) {
        double c = 0.0;

        while (--n >= 0)
            c += a[n] * b[n];
        return c;
    }

    /* euclid norm -----------------------------------------------------------------
* euclid norm of vector
* args   : double *a        I   vector a (n x 1)
*          int    n         I   size of vector a
* return : || a ||
*-----------------------------------------------------------------------------*/
    public double norm(double[] a, int n) {
        return Math.sqrt(dot(a, a, n));
    }

    /* multiply matrix (wrapper of blas dgemm) -------------------------------------
    * multiply matrix by matrix (C=alpha*A*B+beta*C)
    * args   : char   *tr       I  transpose flags ("N":normal,"T":transpose)
    *          int    n,k,m     I  size of (transposed) matrix A,B
    *          double alpha     I  alpha
    *          double *A,*B     I  (transposed) matrix A (n x m), B (m x k)
    *          double beta      I  beta
    *          double *C        IO matrix C (n x k)
    * return : none
    *-----------------------------------------------------------------------------*/
    private double[] matmul(char[] tr, int n, int k, int m, double alpha,
                            double[] A, double[] B, double beta) {
        double d;
        double[] C = new double[n * k];
        int i, j, x, f = tr[0] == 'N' ? (tr[1] == 'N' ? 1 : 2) : (tr[1] == 'N' ? 3 : 4);
        for (i = 0; i < n; i++)
            for (j = 0; j < k; j++) {
                d = 0.0;
                switch (f) {
                    case 1:
                        for (x = 0; x < m; x++)
                            d += A[i + x * n] * B[x + j * m];
                        break;
                    case 2:
                        for (x = 0; x < m; x++)
                            d += A[i + x * n] * B[j + x * k];
                        break;
                    case 3:
                        for (x = 0; x < m; x++)
                            d += A[x + i * m] * B[x + j * m];
                        break;
                    case 4:
                        for (x = 0; x < m; x++)
                            d += A[x + i * m] * B[j + x * k];
                        break;
                }
                if (beta == 0.0)
                    C[i + j * n] = alpha * d;
                else
                    C[i + j * n] = alpha * d + beta * C[i + j * n];
            }
        return C;
    }

    /* convert degree to deg-min-sec -----------------------------------------------
    * convert degree to degree-minute-second
    * args   : double deg       I   degree
    *          double *dms      O   degree-minute-second {deg,min,sec}
    * return : none
    *-----------------------------------------------------------------------------*/
    public double[] deg2dms(double deg) {
        double[] dms = new double[3];
        double sign = deg < 0.0 ? -1.0 : 1.0, a = Math.abs(deg);
        dms[0] = floor(a);
        a = (a - dms[0]) * 60.0;
        dms[1] = floor(a);
        a = (a - dms[1]) * 60.0;
        dms[2] = a;
        dms[0] *= sign;
        return dms;
    }

    /* convert deg-min-sec to degree -----------------------------------------------
    * convert degree-minute-second to degree
    * args   : double *dms      I   degree-minute-second {deg,min,sec}
    * return : degree
    *-----------------------------------------------------------------------------*/
    public double dms2deg(double[] dms) {
        double sign = dms[0] < 0.0 ? -1.0 : 1.0;
        return sign * (Math.abs(dms[0]) + dms[1] / 60.0 + dms[2] / 3600.0);
    }

    public double dmm2deg(double dmm) {
        return floor(dmm / 100.0) + (dmm % 100.0) / 60.0;
    }

    /* transform ecef to geodetic postion ------------------------------------------
* transform ecef position to geodetic position
* args   : double *r        I   ecef position {x,y,z} (m)
*          double *pos      O   geodetic position {lat,lon,h} (rad,m)
* return : none
* notes  : WGS84, ellipsoidal height
*-----------------------------------------------------------------------------*/
//#pragma CODE_SECTION(ecef2pos,"sect_ECODE_I");
    public double[] ecef2pos(double[] r) {
        double[] pos = new double[3];
        double e2 = FE_WGS84 * (2.0 - FE_WGS84), r2 = dot(r, r, 2), z, zk, v = RE_WGS84, sinp;

        for (z = r[2], zk = 0.0; Math.abs(z - zk) >= 1E-4; ) {
            zk = z;
            sinp = z / Math.sqrt(r2 + z * z);
            v = RE_WGS84 / Math.sqrt(1.0 - e2 * sinp * sinp);
            z = r[2] + v * e2 * sinp;
        }
        pos[0] = r2 > 1E-12 ? Math.atan(z / Math.sqrt(r2)) : (r[2] > 0.0 ? PI / 2.0 : -PI / 2.0);
        pos[1] = r2 > 1E-12 ? Math.atan2(r[1], r[0]) : 0.0;
        pos[2] = Math.sqrt(r2 + z * z) - v;
        return pos;
    }

    /* transform geodetic to ecef position -----------------------------------------
* transform geodetic position to ecef position
* args   : double *pos      I   geodetic position {lat,lon,h} (rad,m)
*          double *r        O   ecef position {x,y,z} (m)
* return : none
* notes  : WGS84, ellipsoidal height
*-----------------------------------------------------------------------------*/
//#pragma CODE_SECTION(pos2ecef,"sect_ECODE_I");
    public double[] pos2ecef(double[] pos) {
        double[] r = new double[3];
        double sinp = Math.sin(pos[0]), cosp = Math.cos(pos[0]), sin_l = Math.sin(pos[1]), cos_l = Math.cos(pos[1]);
        //double sinp,cosp,sin_l,cosl;
        //double tmp;
        double e2 = FE_WGS84 * (2.0 - FE_WGS84), v = RE_WGS84 / Math.sqrt(1.0 - e2 * sinp * sinp);

        //tmp = pos[0];
        //sinp = sin(tmp);

        r[0] = (v + pos[2]) * cosp * cos_l;
        r[1] = (v + pos[2]) * cosp * sin_l;
        r[2] = (v * (1.0 - e2) + pos[2]) * sinp;
        return r;
    }

    /* ecef to local coordinate transformation matrix ------------------------------
* compute ecef to local coordinate transformation matrix
* args   : double *pos      I   geodetic position {lat,lon} (rad)
*          double *E        O   ecef to local coord transformation matrix (3x3)
* return : none
* notes  : matirix stored by column-major order (fortran convention)
*-----------------------------------------------------------------------------*/
//#pragma CODE_SECTION(xyz2enu,"sect_ECODE_I");

    public double[] xyz2enu(double[] pos) {
        double[] E = new double[9];
        double sinp = Math.sin(pos[0]), cosp = Math.cos(pos[0]), sin_l = Math.sin(pos[1]), cos_l = Math.cos(pos[1]);
        //double sinp,cosp,sinl,cosl;

        E[0] = -sin_l;
        E[3] = cos_l;
        E[6] = 0.0;
        E[1] = -sinp * cos_l;
        E[4] = -sinp * sin_l;
        E[7] = cosp;
        E[2] = cosp * cos_l;
        E[5] = cosp * sin_l;
        E[8] = sinp;
        return E;
    }

    /* transform ecef vector to local tangental coordinate -------------------------
* transform ecef vector to local tangental coordinate
* args   : double *pos      I   geodetic position {lat,lon} (rad)
*          double *r        I   vector in ecef coordinate {x,y,z}
*          double *e        O   vector in local tangental coordinate {e,n,u}
* return : none
*-----------------------------------------------------------------------------*/
//#pragma CODE_SECTION(ecef2enu,"sect_ECODE_I");
    public double[] ecef2enu(double[] pos, double[] r) {
        double E[];

        E = xyz2enu(pos);

        return matmul("NN".toCharArray(), 3, 1, 3, 1.0, E, r, 0.0);
    }
/* transform local vector to ecef coordinate -----------------------------------
* transform local tangental coordinate vector to ecef
* args   : double *pos      I   geodetic position {lat,lon} (rad)
*          double *e        I   vector in local tangental coordinate {e,n,u}
*          double *r        O   vector in ecef coordinate {x,y,z}
* return : none
*-----------------------------------------------------------------------------*/
//#pragma CODE_SECTION(enu2ecef,"sect_ECODE_I");


    public double[] enu2ecef(double[] pos, double[] e) {
        double E[];

        E = xyz2enu(pos);
        return matmul("TN".toCharArray(), 3, 1, 3, 1.0, E, e, 0.0);
    }
}
