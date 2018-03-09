package ice.rtk.view.customview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by Administrator on 2018/1/25.
 */

public class IHorizontalScrollView extends HorizontalScrollView {

    private Runnable scrollerTask;
    private int intitPosition;
    private int newCheck = 100;
    private int childWidth = 0;

    public interface OnScrollStopListner {

        void onScrollStoped();

        void onScrollToLeftEdge();

        void onScrollToRightEdge();

        void onScrollToMiddle();
    }

    private OnScrollStopListner onScrollstopListner;

    public IHorizontalScrollView(Context context) {
        super(context);
    }

    public IHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrollerTask = new Runnable() {
            @Override
            public void run() {
                int newPosition = getScrollX();
                if (newPosition != 0) {
                    if (onScrollstopListner == null ) {
                        return;
                    }
//                    onScrollstopListner.onScrollStoped();
                    Rect outRect = new Rect();
                    getDrawingRect(outRect);
                     if (childWidth + getPaddingLeft() + getPaddingRight() == outRect.right) {
                        onScrollstopListner.onScrollToRightEdge();
                    } else {
                        onScrollstopListner.onScrollToMiddle();
                    }
                } else  if (getScrollX() == 0)  {
                    if (onScrollstopListner == null) {
                        return;
                    }
                    onScrollstopListner.onScrollToLeftEdge();
                    postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public IHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollStopListner(OnScrollStopListner listner) {
        onScrollstopListner = listner;
    }

    public void startScrollerTask() {
        intitPosition = getScrollX();
        postDelayed(scrollerTask, newCheck);
        checkTotalWidth();
    }

    private void checkTotalWidth() {
        if (childWidth > 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            childWidth += getChildAt(i).getWidth();
        }
    }

}
