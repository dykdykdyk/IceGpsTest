package ice.rtk.view.PointView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import ice.rtk.Utils.LogUtil;
import ice.rtk.view.LinesView.Tools;

import static ice.rtk.view.LinesView.Tools.getIndex;


public class MapView extends SurfaceView
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
    //点坐标的位置
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
    //南北偏差
    double S_N_Distance =0;
    //东西偏差
    double E_W_Distance=0;
    private int flg_s_n;
    private int flg_e_w;

    public MapView(Context context) {
        super(context);
        init(context);
    }

    private enum Status {
        circle, Lines
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    }

    //初始化左下方显示的距离
    public void initLocation(int distance) {
        counts = getIndex(distance);
        LogUtil.e(getClass(), "counts:" + counts);
    }


    ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
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
//                Log.i("TAG", "flag_temp:" + flag_temp + ",scaleFactor:" + scaleFactor);
            Log.i("TAG", ",counts:" + counts);
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            Log.i("TAG", "onScaleBegin");
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            flag_temp = 0;
            Log.i("TAG", "onScaleEnd");
        }
    };
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
        drawCenterLines(canvas, transx, transy);
        //绘制其他线段 以及动态绘制线段 以及绘制当前位置
        getModeLines(canvas, transx, transy, status);
        canvas.restore();
        //画当前位置
        //画中心线以及中心圆点
        scale(Tools.Temp[counts]);
    }

    /**
     * @param S_N_Distance 南北
     * @param E_W_Distance
     * @param flg_s_n      1正 0 负 -1 轴
     * @param flg_e_w
     */
    public void setData(double S_N_Distance, double E_W_Distance, int flg_s_n, int flg_e_w) {
        LogUtil.i("TAG","S_N_Distance:"+S_N_Distance+",E_W_Distance:"+E_W_Distance+
                ",flg_s_n:"+flg_s_n+",flg_e_w:"+flg_e_w);
        this.S_N_Distance = S_N_Distance;//南北 Y轴
        this.E_W_Distance = E_W_Distance;//东西 X轴
        this.flg_s_n = flg_s_n;//东西 X轴
        this.flg_e_w = flg_e_w;//东西 X轴

    }

    public void getLength(Canvas canvas, int temp) {
        //当前比例尺转换成距离 mm单位
        int tempS = Tools.changeStringtometer(Tools.Temp[counts]);
        //测试 假如都是300米的偏差 单位已经进行了处理
        float fx = ((float) E_W_Distance / tempS);

        float fy = ((float) S_N_Distance / tempS);
//        Log.i("TAG", "F:" + f);
        float lengthX = fx * temp;
        float lengthY = fy * temp;
        if (flg_s_n == 1 && flg_e_w == 1) {
            //一 东北
            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 + lengthX), (mHeight / 2 - lengthY), paintCenter);
            canvas.drawCircle((mWidth / 2 + lengthX), mHeight / 2 - lengthY, circleradius, paintcurrent);
        } else if (flg_s_n == 1 && flg_e_w == 0) {
            //二 北西
            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 - lengthX), (mHeight / 2 - lengthY), paintCenter);
            canvas.drawCircle(mWidth / 2 - lengthX, mHeight / 2 - lengthY, circleradius, paintcurrent);
        }else if(flg_s_n == 0 && flg_e_w == 0){
            //三 西南
        canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 - lengthX), (mHeight / 2 + lengthY), paintCenter);
        canvas.drawCircle((mWidth / 2 - lengthX), (mHeight / 2 + lengthY), circleradius, paintcurrent);
        }else if(flg_s_n == 0 && flg_e_w == 1){
            //四 东南
        canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 + lengthX), (mHeight / 2 + lengthY), paintCenter);
        canvas.drawCircle((mWidth / 2 + lengthX), (mHeight / 2 + lengthY), circleradius, paintcurrent);
        }
    }

    public void getLengthCircle(Canvas canvas, int temp) {
        //当前比例尺转换成距离 mm单位
        int tempS = Tools.changeStringtometer(Tools.Temp[counts]);
        //测试 假如都是300米的偏差 单位已经进行了处理
        float fx = ((float) E_W_Distance / tempS);

        float fy = ((float) S_N_Distance / tempS);
//        Log.i("TAG", "F:" + f);
        float lengthX = fx * temp;
        float lengthY = fy * temp;
        if (flg_s_n == 1 && flg_e_w == 1) {
            //一 东北
            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 + lengthX), (mHeight / 2 - lengthY), paintCenter);
            canvas.drawCircle((mWidth / 2 + lengthX), mHeight / 2 - lengthY, circleradius, paintcurrent);
        } else if (flg_s_n == 1 && flg_e_w == 0) {
            //二 北西
            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 - lengthX), (mHeight / 2 - lengthY), paintCenter);
            canvas.drawCircle(mWidth / 2 - lengthX, mHeight / 2 - lengthY, circleradius, paintcurrent);
        }else if(flg_s_n == 0 && flg_e_w == 0){
            //三 西南
            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 - lengthX), (mHeight / 2 + lengthY), paintCenter);
            canvas.drawCircle((mWidth / 2 - lengthX), (mHeight / 2 + lengthY), circleradius, paintcurrent);
        }else if(flg_s_n == 0 && flg_e_w == 1){
            //四 东南
            canvas.drawLine(mWidth / 2, mHeight / 2, (mWidth / 2 + lengthX), (mHeight / 2 + lengthY), paintCenter);
            canvas.drawCircle((mWidth / 2 + lengthX), (mHeight / 2 + lengthY), circleradius, paintcurrent);
        }
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
//        canvas.drawLine(mWidth / 2, 0,mWidth / 2, mHeight, paintCenter);
//        canvas.drawLine(0, mHeight / 2,mWidth, mHeight / 2, paintCenter);
//        canvas.drawCircle(mWidth / 2, mHeight / 2, circleradius, paintCenter);

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
        getLengthCircle(canvas, temp);
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
        getLengthCircle(canvas, temp);
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
        getLengthCircle(canvas, temp);
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

    /**
     * 移除监听
     */
    public void removeListener(){
        if(null !=mapStatusChangeListener){
            this.mapStatusChangeListener =null;
        }
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeListener();
    }
}
