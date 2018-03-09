package ice.rtk.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.Arrays;

import ice.rtk.App;
import ice.rtk.R;
import ice.rtk.utils.LogUtil;
import ice.rtk.utils.viewtools.Tools;

import static ice.rtk.utils.viewtools.Tools.getIndex;

/**
 * 默认显示的比例尺是以p1p2两条线段之间的距离来计算的
 */
public class LinesLoftingView extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {

    private int mHeight, mWidth;
    //绘制圆的个数，貌似没啥用
    //圆点半径
    private int circleradius = 6;
    // 画普通线段
    Paint paintLines;
    //当前位置点画笔
    Paint paintcurrent;
    //中心线段画笔
    Paint paintCenter;
    //绘制圆的画笔
    Paint paintCircle;
    //绘制延长线虚线画笔
    Paint paintdashed;
    float x;
    float y;
    private SurfaceHolder mHolder;
    //画布
    private Canvas mCanvas;
    //flag 是否绘制
    private boolean mIsDrawing;

    //单击双击平移监听
    private GestureDetector mGestureDetector;

    //手势缩放
    private ScaleGestureDetector mScaleGestureDetector;
    //测试背景
    Bitmap bitmap;
    //背景颜色
    private int bgColor;
    //标志位
    int flag = 0;

    //数组的标志位
    int counts = 0;

    //标志位
    Status status;
    //设置画笔虚线
    PathEffect effect;
    //起点和终点南北偏差
    double S_N_Distance_S_E = 0;
    //东西偏差
    double E_W_Distance_S_E = 0;
    private int flg_s_n_S_E;
    private int flg_e_w_S_E;

    // //起点和当前点南北偏差距离和方向变量
    double S_N_Distance_S_C = 0;
    //东西偏差
    double E_W_Distance_S_C = 0;
    private int flg_s_n_S_C;
    private int flg_e_w_S_C;

    public LinesLoftingView(Context context) {
        super(context);
        init(context);
    }

    private enum Status {
        circle, Lines
    }

    public LinesLoftingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LinesLoftingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        bgColor = App.getInstance().getResources().getColor(R.color.mapbg);
        paintcurrent = new Paint();
        paintcurrent.setStyle(Paint.Style.FILL);
        paintcurrent.setAntiAlias(true);
        paintcurrent.setStrokeWidth(10);
        paintcurrent.setColor(Color.RED);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mGestureDetector = new GestureDetector(context, simpleOnGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        //设置画笔虚线
        effect = new DashPathEffect(new float[]{5, 5}, 1);
        //线段
        paintLines = new Paint();
        paintLines.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLines.setAntiAlias(true);
        paintLines.setColor(Color.BLACK);
        paintLines.setStrokeWidth(1);

        //中心点
        paintCenter = new Paint();
        paintCenter.setStyle(Paint.Style.FILL);
        paintCenter.setAntiAlias(true);
        paintCenter.setColor(Color.BLUE);
        paintCenter.setStrokeWidth(3);
        status = Status.Lines;
        //初始化初始位置
        Log.i("TAG", "Tools.Temp:" + Arrays.toString(Tools.Temp));

        //初始化绘制圆的画笔
        paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setAntiAlias(true);
        paintCircle.setStrokeWidth(3);
        paintCircle.setColor(Color.BLACK);

        //初始化绘制虚线的画笔
        paintdashed = new Paint();
        paintdashed.setAntiAlias(true);
        paintdashed.setStyle(Paint.Style.STROKE);
        paintdashed.setStrokeWidth(3);
        paintdashed.setColor(Color.BLACK);
        paintdashed.setPathEffect(effect);
    }

    //初始化左下方显示的距离
    public void initLocation(int distance) {
        counts = getIndex(distance);
//        scale(Tools.Temp[counts]);
    }

    ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        float focusX;
        float focusY;
        float flag_temp = 0;

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            /**
             * scaleFactor 两手指缩笼(缩小画布) 1-0 距离放大
             * scaleFactor 两手指扩大(放大画布) 1-5？ 距离缩小
             */
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            if (flag_temp == 0) {
                flag_temp = scaleFactor;
            } else if (Math.abs(scaleFactor - flag_temp) > 0.1) {
                if (scaleFactor >= 1) {
                    counts--;
                } else {
                    counts++;
                }
                if (counts < 0) {
                    counts = 0;
                } else if (counts >= Tools.Temp.length) {
                    counts = Tools.Temp.length - 1;
                } else {
                    flag++;
                    scale(Tools.Temp[counts]);
                    flag_temp = scaleFactor;
                }
            }
                Log.i("TAG", "flag_temp:" + flag_temp + ",counts:" + counts);
                Log.i("TAG", "onScale" + ",scalex:" + scalex);
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            Log.i("TAG", "onScaleBegin");
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            focusX = 0;
            focusY = 0;
            flag_temp = 0;
            Log.i("TAG", "onScaleEnd");
        }
    };
    float scalex = 50;
    float transx = 0;
    float transy = 0;
    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            transx = transx - distanceX;
            transy = transy - distanceY;
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         *
         * @param e 监听双击事件
         * @return 处理结果
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap:" + e.getAction());
            transx = 0;
            transy = 0;
            return super.onDoubleTap(e);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取宽高参数
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        x = mWidth / 2;
        y = mHeight / 2;
    }
    //假设他们相距200m,200m

    private void DrawT(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.save();
        canvas.drawColor(bgColor);
        canvas.translate(transx, transy);
        //绘制中心线段以及中心点
        //绘制其他线段 以及动态绘制线段 以及绘制当前位置
        getModeLines(canvas, transx, transy, status);
        canvas.restore();
        //画当前位置
        scale(Tools.Temp[counts]);
    }

    /**
     * 起点与终点之间的距离和方向赋值
     *
     * @param S_N_Distance 南北
     * @param E_W_Distance
     * @param flg_s_n      1正 0 负 -1 轴
     * @param flg_e_w
     */
    public void setData_S_E(double S_N_Distance, double E_W_Distance, int flg_s_n, int flg_e_w) {
        LogUtil.i("TAG", "S_N_Distance:" + S_N_Distance + ",E_W_Distance:" + E_W_Distance +
                ",flg_s_n:" + flg_s_n + ",flg_e_w:" + flg_e_w);
        this.S_N_Distance_S_E = S_N_Distance;//南北 Y轴
        this.E_W_Distance_S_E = E_W_Distance;//东西 X轴
        this.flg_s_n_S_E = flg_s_n;//东西 X轴
        this.flg_e_w_S_E = flg_e_w;//东西 X轴
    }

    /**
     * 起点与当前点之间的距离和方向赋值
     *
     * @param S_N_Distance 南北
     * @param E_W_Distance
     * @param flg_s_n      1正 0 负 -1 轴
     * @param flg_e_w
     */
    public void setData_S_C(double S_N_Distance, double E_W_Distance, int flg_s_n, int flg_e_w) {
        LogUtil.i("TAG", "S_N_Distance:" + S_N_Distance + ",E_W_Distance:" + E_W_Distance +
                ",flg_s_n:" + flg_s_n + ",flg_e_w:" + flg_e_w);
        this.S_N_Distance_S_C = S_N_Distance;//南北 Y轴
        this.E_W_Distance_S_C = E_W_Distance;//东西 X轴
        this.flg_s_n_S_C = flg_s_n;//东西 X轴
        this.flg_e_w_S_C = flg_e_w;//东西 X轴
    }

    /**
     * @param canvas
     * @param temp   当前试图 每一格代表的距离
     */
    public void getLength(Canvas canvas, int temp) {
        //当前比例尺转换成距离 mm单位
        int tempS = Tools.changeStringtometer(Tools.Temp[counts]);
        //P2 p1点距离计算等比例尺的距离
        //x轴 y轴
        float p2p1X = ((float) E_W_Distance_S_E / tempS);
        float p2p1Y = ((float) S_N_Distance_S_E / tempS);
        p2p1X =p2p1X*temp;
        p2p1Y =p2p1Y*temp;

        //P2坐标的临时变量
        float P2X =0;
        float P2Y =0;
        // P2原始坐标
        if (flg_s_n_S_E == 1 && flg_e_w_S_E == 1) {
            //一 东北
//            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 + p2p1X), (mHeight / 2 - p2p1Y), paintCenter);
//            canvas.drawCircle((mWidth / 2 + p2p1X), mHeight / 2 - p2p1Y, circleradius, paintcurrent);
            P2X =(mWidth / 2 + p2p1X);
            P2Y =(mHeight / 2 - p2p1Y);
        } else if (flg_s_n_S_E == 1 && flg_e_w_S_E == 0) {
            //二 北西
//            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 - p2p1X), (mHeight / 2 - p2p1Y), paintCenter);
//            canvas.drawCircle(mWidth / 2 - p2p1X, mHeight / 2 - p2p1Y, circleradius, paintcurrent);
            P2X =(mWidth / 2 - p2p1X);
            P2Y =(mHeight / 2 - p2p1Y);
        } else if (flg_s_n_S_E == 0 && flg_e_w_S_E == 0) {
            //三 西南
//            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 - p2p1X), (mHeight / 2 + p2p1Y), paintCenter);
//            canvas.drawCircle((mWidth / 2 - p2p1X), (mHeight / 2 + p2p1Y), circleradius, paintcurrent);
            P2X =(mWidth / 2 - p2p1X);
            P2Y =(mHeight / 2 + p2p1Y);
        } else if (flg_s_n_S_E == 0 && flg_e_w_S_E == 1) {
            //四 东南
//            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 + p2p1X), (mHeight / 2 + p2p1Y), paintCenter);
//            canvas.drawCircle((mWidth / 2 + p2p1X), (mHeight / 2 + p2p1Y), circleradius, paintcurrent);
            P2X =(mWidth / 2 + p2p1X);
            P2Y =(mHeight / 2 + p2p1Y);
        }

        //S1坐标的临时变量
        float S1X =0;
        float S1Y =0;
        //x轴 y轴   S_N_Distance_S_C
        float Sp1X = ((float) E_W_Distance_S_C / tempS);
        float Sp1Y = ((float) S_N_Distance_S_C / tempS);
        Sp1X =Sp1X*temp;
        Sp1Y =Sp1Y*temp;
        // P2原始坐标
        if (flg_s_n_S_C == 1 && flg_e_w_S_C == 1) {
            //一 东北
//            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 + Sp1X), (mHeight / 2 - Sp1Y), paintCenter);
//            canvas.drawCircle((mWidth / 2 + Sp1X), mHeight / 2 - Sp1Y, circleradius, paintcurrent);
            S1X =(mWidth / 2 + Sp1X);
            S1Y =(mHeight / 2 - Sp1Y);
        } else if (flg_s_n_S_C == 1 && flg_e_w_S_C == 0) {
            //二 北西
//            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 - Sp1X), (mHeight / 2 - Sp1Y), paintCenter);
//            canvas.drawCircle(mWidth / 2 - Sp1X, mHeight / 2 - Sp1Y, circleradius, paintcurrent);
            S1X =(mWidth / 2 - Sp1X);
            S1Y =(mHeight / 2 - Sp1Y);
        } else if (flg_s_n_S_C == 0 && flg_e_w_S_C == 0) {
            //三 西南
//            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 - Sp1X), (mHeight / 2 + Sp1Y), paintCenter);
//            canvas.drawCircle((mWidth / 2 - Sp1X), (mHeight / 2 + Sp1Y), circleradius, paintcurrent);
            S1X =(mWidth / 2 - Sp1X);
            S1Y =(mHeight / 2 + Sp1Y);
        } else if (flg_s_n_S_C == 0 && flg_e_w_S_C == 1) {
            //四 东南
//            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 + Sp1X), (mHeight / 2 + Sp1Y), paintCenter);
//            canvas.drawCircle((mWidth / 2 + Sp1X), (mHeight / 2 + Sp1Y), circleradius, paintcurrent);
            S1X =(mWidth / 2 + Sp1X);
            S1Y =(mHeight / 2 + Sp1Y);
        }
//        float f2 = ((float) 50 * 1000 / tempS);
//        float length = f * temp;
//        float length2 = f2 * temp;
////        //一 东北 p2 原始坐标
//        canvas.drawCircle((mWidth / 2 + length2), mHeight / 2 - length2, circleradius, paintcurrent);
////        //二 北西 p1 原始坐标
//        canvas.drawCircle(mWidth / 2 - length, mHeight / 2 - length, circleradius, paintcurrent);
//
//        //模拟当前的坐标位置
//        canvas.drawCircle(mWidth / 2 + length * 2, mHeight / 2, circleradius, paintcurrent);
        //原始坐标
        /**
         *      P2X,P2Y;
         */
        float originalP1x = (mWidth / 2 );
        float originalP1y = (mHeight / 2);
        float originalP2x = P2X;
        float originalP2y = P2Y;

        //当前位置坐标
        float originalCx = S1X;
        float originalCy = S1Y;
        canvas.drawLine(originalP1x, originalP1y, originalP2x, originalP2y, paintCenter);

        //绘制起点位置距离
        canvas.drawLine(originalP1x, originalP1y, originalCx, originalCy, paintCenter);
        //绘制终点位置距离
        canvas.drawLine(originalP2x, originalP2y, originalCx, originalCy, paintCenter);

        /**
         * //计算点到线段之间的距离用矢量叉乘计算
         * u p1 p2向量
         * v p1 s向量
         */
        double[] u = {originalCx - originalP1x, originalCy - originalP1y};
        double[] v = {originalP2x - originalP1x, originalP2y - originalP1y};
        double l = calculateDotDistance(u, v);
        float x = 0;
//        Log.i("TAG", "distance:" + l);
        //计算p1p2两点之间的距离
        double distance = Math.sqrt((originalP2x - originalP1x) * (originalP2x - originalP1x) + (originalP2y - originalP1y) * (originalP2y - originalP1y));
        double X = (originalP2x - originalP1x) / (distance) * l;
        double Y = (originalP2y - originalP1y) / (distance) * l;
        //投影点坐标
        float S2x = (float) (X + originalP1x);
        float S2y = (float) (Y + originalP1y);

        //绘制原始投影点
        canvas.drawCircle(S2x, S2y, circleradius, paintcurrent);
        //绘制原始的距离
        canvas.drawLine(originalCx, originalCy, S2x, S2y, paintCenter);
//        (originalCx-x)*(originalCx-x)+(originalCy-(k*x+b))*(originalCy-(k*x+b))=distance *distance;
        //    原始点之间的虚线
        Path path = new Path();
        path.moveTo(originalP1x, originalP1y);
        path.lineTo(S2x, S2y);
        canvas.drawPath(path, paintdashed);
        //绘制延长线(如果投影点不在p1p2线段上)
//        canvas.drawLines(new float[]{originalP1x,originalP1y,S2x,S2y}, paintCenter);

        //计算当前线段偏差(注意计算顺序p2-p1)-当前屏幕中心坐标
        float offsetX = (originalP2x - originalP1x) / 2;
        float offsetY = (originalP2y - originalP1y) / 2;
        //计算中心点坐标 (p2-offsetX,p2-offsetY);
        float centerX = originalP2x - offsetX;
        float centerY = originalP2y - offsetY;
        //具体每个点所需要偏移的距离(屏幕中心位置-当前线段的中心坐标)
        float publicX = mWidth / 2 - centerX;
        float publicY = mHeight / 2 - centerY;
//        Log.i("TAG", "offsetX:" + offsetX);
//        Log.i("TAG", "offsetY:" + offsetY);
        //绘制p1，p2点 以及之间的线段
        //转换下坐标
//        Log.i("TAG", "p1:" + originalP1x + ",p1:" + originalP1y);
//        Log.i("TAG", "p2:" + originalP2x + ",p2:" + originalP2y);
//        Log.i("TAG", "publicX:" + publicX + ",publicY:" + publicY);
        float p1x = originalP1x + publicX;
        float p1y = originalP1y + publicY;
        float p2x = originalP2x + publicX;
        float p2y = originalP2y + publicY;
        float Cx = originalCx + publicX;
        float Cy = originalCy + publicY;

        float Projectionx = S2x + publicX;
        float Projectiony = S2y + publicY;
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(10);
        //p2
        canvas.drawCircle(p1x, p1y, circleradius, p);
        canvas.drawCircle(p2x, p2y, circleradius, p);

        //绘制偏移后的投影点
        canvas.drawCircle(Projectionx, Projectiony, circleradius, p);


        //p1
//        Log.i("TAG", "p1x:" + p1x + ",p1y:" + p1y);
//        Log.i("TAG", "p2x:" + p2x + ",p2y:" + p2y);
//        Log.i("TAG", "mWidth / 2:" + mWidth / 2 + ",mHeight / 2:" + mHeight / 2);
//        //线段
        canvas.drawLine(p1x, p1y, p2x, p2y, p);
        canvas.drawLine(p1x, p1y, Cx, Cy, p);
        canvas.drawLine(p2x, p2y, Cx, Cy, p);
        //绘制偏移后的线段距离
        canvas.drawLine(Projectionx, Projectiony, Cx, Cy, p);

        Path path2 = new Path();
        path2.moveTo(p1x, p1y);
        path2.lineTo(Projectionx, Projectiony);
        canvas.drawPath(path2, paintdashed);
        //
        drawCenterLines(canvas, transx, transy);
//        //三 西南
    }

    /**
     * 计算偏差 点乘(cos)
     * :当前位置到航线之间的偏移距离
     *
     * @param u
     * @param v
     * @return
     */
    public static double calculateDotDistance(double[] u, double[] v) {
        //1.(x1y2-x2y1)/Math.sqrt(x2 * x2 + y2 * y2);
        if (v[0] * v[0] + v[1] * v[1] == 0) {
            return 0;
        }
        double temp = (u[0] * v[0] + u[1] * v[1]) / Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        return temp;
    }

    public void getModeLines(Canvas canvas, float transx, float transy, Status status) {
        if (flag >= 4) {
            flag = 1;
        }
        if (flag % 3 == 0) {
            if (status == Status.Lines)
                drawLines1(canvas, transx, transy);
            else
                drawCircle1(canvas, transx, transy);
        } else if (flag % 3 == 1) {
            if (status == Status.Lines)
                drawLines2(canvas, transx, transy);
            else
                drawCircle2(canvas, transx, transy);
        } else if (flag % 3 == 2) {
            if (status == Status.Lines)
                drawLines5(canvas, transx, transy);
            else
                drawCircle5(canvas, transx, transy);
        }
//        Log.i("TAG","(int)scalex%3:"+(int)scalex%3);
    }

    public void drawCenterLines(Canvas canvas, float transx, float transy) {
        canvas.drawLine(mWidth / 2, transy, mWidth / 2, mHeight - transy, paintCenter);
        canvas.drawLine(mWidth / 2, -transy, mWidth / 2, mHeight + transy, paintCenter);
        canvas.drawLine(transx, mHeight / 2, mWidth - transx, mHeight / 2, paintCenter);
        canvas.drawLine(-transx, mHeight / 2, mWidth + transx, mHeight / 2, paintCenter);
        canvas.drawCircle(mWidth / 2, mHeight / 2, circleradius, paintCenter);
    }

    public void drawLines5(Canvas canvas, float transx, float transy) {
        //5CM
//        for (int i = 1; i < 7; i++) {
//            canvas.drawLine(mWidth / 6 * i, 0, mWidth / 6 * i, mHeight, paintLines);
//        }
//        int temp = mWidth / 6;
//        for (int i = 1; i < temp + 2; i++) {
//            canvas.drawLine(0, mHeight / 2 - temp * i, mWidth, mHeight / 2 - temp * i, paintLines);
//            canvas.drawLine(0, mHeight / 2 + temp * i, mWidth, mHeight / 2 + temp * i, paintLines);
//        }
        int temp = mWidth / 6;
        int offsetX = (int) Math.abs((transx / temp));
        int offsetY = (int) Math.abs((transy / temp));
        //竖线
        for (int i = 0; i < 6 + 2 + offsetX; i++) {
            //下方向
            canvas.drawLine(temp * i, transy, temp * i, mHeight - transy, paintLines);
            //上方向
            canvas.drawLine(temp * i, -transy, temp * i, mHeight + transy, paintLines);
            //左
            canvas.drawLine(-temp * i, transy, -temp * i, mHeight - transy, paintLines);
            //右
            canvas.drawLine(-temp * i, -transy, -temp * i, mHeight + transy, paintLines);
        }
        //横线
        for (int i = 0; i < temp + 2 + offsetY; i++) {
            canvas.drawLine(transx, mHeight / 2 - temp * i, mWidth - transx, mHeight / 2 - temp * i, paintLines);
            canvas.drawLine(transx, mHeight / 2 + temp * i, mWidth - transx, mHeight / 2 + temp * i, paintLines);
            canvas.drawLine(-transx, (mHeight / 2 + temp * i), mWidth + transx, (mHeight / 2 + temp * i), paintLines);
            canvas.drawLine(-transx, (mHeight / 2 - temp * i), mWidth + transx, (mHeight / 2 - temp * i), paintLines);
        }
        getLength(canvas, temp);
//        Log.i("drawLines5","transx:"+transx+",transy:"+transy);

    }

    public void drawLines1(Canvas canvas, float transx, float transy) {
        //1CM
        int temp = mWidth / 10;
        int offsetX = (int) Math.abs((transx / temp));
        int offsetY = (int) Math.abs((transy / temp));
        //竖线
        for (int i = 0; i < 12 + offsetX; i++) {
            //下方向
            canvas.drawLine(temp * i, transy, temp * i, mHeight - transy, paintLines);
            //上方向
            canvas.drawLine(temp * i, -transy, temp * i, mHeight + transy, paintLines);
            //左
            canvas.drawLine(-temp * i, transy, -temp * i, mHeight - transy, paintLines);
            //右
            canvas.drawLine(-temp * i, -transy, -temp * i, mHeight + transy, paintLines);
        }
        //横线
        for (int i = 0; i < temp + 2 + offsetY; i++) {
            canvas.drawLine(transx, mHeight / 2 - temp * i, mWidth - transx, mHeight / 2 - temp * i, paintLines);
            canvas.drawLine(transx, mHeight / 2 + temp * i, mWidth - transx, mHeight / 2 + temp * i, paintLines);
            canvas.drawLine(-transx, (mHeight / 2 + temp * i), mWidth + transx, (mHeight / 2 + temp * i), paintLines);
            canvas.drawLine(-transx, (mHeight / 2 - temp * i), mWidth + transx, (mHeight / 2 - temp * i), paintLines);
        }
        getLength(canvas, temp);
    }

    public void drawCircle1(Canvas canvas, float transx, float transy) {
        //1CM
//        int temp = mWidth / 10;
        int temp = mWidth / 10;
        int offsetX = (int) Math.abs((transx / temp));
        int offsetY = (int) Math.abs((transy / temp));
        //竖线
        for (int i = 0; i < 12 + offsetX + offsetY; i++) {
            canvas.drawCircle(mWidth / 2, mHeight / 2, temp * i, paintCircle);
        }
        getLength(canvas, temp);
    }

    public void drawCircle2(Canvas canvas, float transx, float transy) {
        //1CM
//        int temp = mWidth / 10;
        int temp = mWidth / 8;
        int offsetX = (int) Math.abs((transx / temp));
        int offsetY = (int) Math.abs((transy / temp));
        //竖线
        for (int i = 0; i < 12 + offsetX + offsetY; i++) {
            canvas.drawCircle(mWidth / 2, mHeight / 2, temp * i, paintCircle);
        }
        Log.i("drawCircle1", "transx:" + transx + ",transy:" + transy);
        getLength(canvas, temp);
    }

    public void drawCircle5(Canvas canvas, float transx, float transy) {
        //1CM
//        int temp = mWidth / 10;
        int temp = mWidth / 6;
        int offsetX = (int) Math.abs((transx / temp));
        int offsetY = (int) Math.abs((transy / temp));
        //竖线
        for (int i = 0; i < 12 + offsetX + offsetY; i++) {
            canvas.drawCircle(mWidth / 2, mHeight / 2, temp * i, paintCircle);
        }
        Log.i("drawCircle1", "transx:" + transx + ",transy:" + transy);
        getLength(canvas, temp);
    }

    public void drawLines2(Canvas canvas, float transx, float transy) {
        //2CM
//        for (int i = 1; i < 9; i++) {
//            canvas.drawLine(mWidth / 8 * i, 0, mWidth / 8 * i, mHeight, paintLines);
//        }
//        int temp = mWidth / 8;
//        for (int i = 1; i < temp + 2; i++) {
//            canvas.drawLine(0, mHeight / 2 - temp * i, mWidth, mHeight / 2 - temp * i, paintLines);
//            canvas.drawLine(0, mHeight / 2 + temp * i, mWidth, mHeight / 2 + temp * i, paintLines);
//        }

        //1CM
        int temp = mWidth / 8;
        int offsetX = (int) Math.abs((transx / temp));
        int offsetY = (int) Math.abs((transy / temp));
        //竖线
        for (int i = 0; i < 8 + 2 + offsetX; i++) {
            //下方向
            canvas.drawLine(temp * i, transy, temp * i, mHeight - transy, paintLines);
            //上方向
            canvas.drawLine(temp * i, -transy, temp * i, mHeight + transy, paintLines);
            //左
            canvas.drawLine(-temp * i, transy, -temp * i, mHeight - transy, paintLines);
            //右
            canvas.drawLine(-temp * i, -transy, -temp * i, mHeight + transy, paintLines);
        }
        //横线
        for (int i = 0; i < temp + 2 + offsetY; i++) {
            canvas.drawLine(transx, mHeight / 2 - temp * i, mWidth - transx, mHeight / 2 - temp * i, paintLines);
            canvas.drawLine(transx, mHeight / 2 + temp * i, mWidth - transx, mHeight / 2 + temp * i, paintLines);
            canvas.drawLine(-transx, (mHeight / 2 + temp * i), mWidth + transx, (mHeight / 2 + temp * i), paintLines);
            canvas.drawLine(-transx, (mHeight / 2 - temp * i), mWidth + transx, (mHeight / 2 - temp * i), paintLines);
        }
//        Log.i("drawLines2","transx:"+transx+",transy:"+transy);
        getLength(canvas, temp);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            draw();
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            DrawT(mCanvas);
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }


    public void enlarge() {
        counts--;
        ScaleMap();
    }

    public void ScaleMap() {
        if (counts < 0) {
            counts = 0;
        } else if (counts >= Tools.Temp.length) {
            counts = Tools.Temp.length - 1;
        } else {
            flag++;
            scale(Tools.Temp[counts]);
        }
    }

    public void narrow() {
        counts++;
        ScaleMap();
    }

    public void changeView() {
        if (status == Status.Lines) {
            status = Status.circle;
        } else {
            status = Status.Lines;
        }
    }

    //添加对外部的接口
    public interface OnMapStatusChangeListener {
        void onScaleChange(String multiple);
    }

    OnMapStatusChangeListener mapStatusChangeListener;

    public void setOnMapStatusChangeListener(OnMapStatusChangeListener onMapStatusChangeListener) {
        mapStatusChangeListener = onMapStatusChangeListener;
    }

    public void scaleChange(String scal) {
        if (mapStatusChangeListener != null) {
            mapStatusChangeListener.onScaleChange(scal);
        }
    }


    private void scale(String scale) {
        scaleChange(scale);
    }
}
