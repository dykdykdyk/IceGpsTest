package ice.rtk.view.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import ice.rtk.R;

/**
 * Created by Administrator on 2018/1/18.
 */

public class ScaleView extends View {
    private  Path path;
    private  Paint linePaint;
    public ScaleView(Context context, Paint linePaint) {
        super(context);
        init();
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public ScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        path = new Path();
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(getResources().getDimension(R.dimen.lineWidth));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(0, getHeight() / 2 - 10);
        path.lineTo(0, getHeight() / 2);
        path.lineTo(getWidth(), getHeight() / 2);
        path.lineTo(getWidth(), getHeight() / 2 - 10);
        canvas.drawPath(path, linePaint);
    }

}
