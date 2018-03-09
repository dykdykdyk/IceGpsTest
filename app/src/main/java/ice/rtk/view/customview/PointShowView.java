package ice.rtk.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import ice.rtk.utils.LogUtil;
import ice.rtk.utils.viewtools.Tools;

import static ice.rtk.utils.viewtools.Tools.getIndex;


public class PointShowView extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {

    private int mHeight, mWidth;
    //绘制圆的个数，貌似没啥用
    //圆点半径
    private int circleradius = 6;
    //中心线段画笔
    Paint paintCenter;
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
    Bitmap originalbitmap,showbitmap;
    //背景颜色
    private int bgColor;
    //标志位
    int flag = 0;

    //数组的标志位
    int counts = 0;


    public PointShowView(Context context) {
        super(context);
        init(context);
    }

    public PointShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PointShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    private void init(Context context) {
        bgColor = App.getInstance().getResources().getColor(R.color.mapbg);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mGestureDetector = new GestureDetector(context, simpleOnGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);
        originalbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location);
        showbitmap =getBitmap(originalbitmap);
        //中心点
        paintCenter = new Paint();
        paintCenter.setStyle(Paint.Style.FILL);
        paintCenter.setAntiAlias(true);
        paintCenter.setColor(Color.BLUE);
        paintCenter.setStrokeWidth(3);
        //初始化初始位置
        Log.i("TAG", "Tools.Temp:" + Arrays.toString(Tools.Temp));
    }

    public Bitmap getBitmap(Bitmap originalbitmap){
        Bitmap bitmap;
        int width =originalbitmap.getWidth();
        int height =originalbitmap.getHeight();

        int newWidth =32;
        int newHeight=48;
        float scaleWidth =((float)newWidth)/width;
        float scaleHeight =((float)newHeight)/height;

        Matrix matrix =new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        bitmap =Bitmap.createBitmap(originalbitmap,0,0,width,height,matrix,true);
        return bitmap;
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
//        getModeLines(canvas, transx, transy, status);
        canvas.restore();
        //画当前位置
        //画中心线以及中心圆点
        scale(Tools.Temp[counts]);
    }





    public void drawCenterLines(Canvas canvas, float transx, float transy) {
        canvas.drawCircle(mWidth / 2, mHeight / 2, circleradius, paintCenter);
        canvas.drawBitmap(showbitmap,mWidth / 2-showbitmap.getWidth()/2, mHeight / 2-showbitmap.getHeight(),paintCenter);
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
