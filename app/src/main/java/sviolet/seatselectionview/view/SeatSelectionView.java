package sviolet.seatselectionview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import sviolet.seatselectionview.R;
import sviolet.turquoise.ui.util.ViewCommonUtils;
import sviolet.turquoise.uix.slideengine.abs.SlideView;
import sviolet.turquoise.uix.slideengine.impl.LinearFlingEngine;
import sviolet.turquoise.uix.slideengine.impl.LinearGestureDriver;
import sviolet.turquoise.uix.viewgesturectrl.ViewGestureControllerImpl;
import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;
import sviolet.turquoise.util.common.BitmapUtils;
import sviolet.turquoise.util.droid.MeasureUtils;

/**
 * Created by S.Violet on 2016/9/21.
 */

public class SeatSelectionView extends View implements ViewCommonUtils.InitListener {

    //触摸控制器
    private ViewGestureControllerImpl viewGestureController;
    //触摸输出
    private SimpleRectangleOutput output;

    //座位表
    private SeatTable seatTable;
    //座位图片池
    private SeatImagePool imagePool;

    public SeatSelectionView(Context context) {
        super(context);
        init(context, null, -1);
    }

    public SeatSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);
    }

    public SeatSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        //初始化
        ViewCommonUtils.setInitListener(this, this);
    }

    @Override
    public void onInit() {

        //手势控制器实例化
        viewGestureController = new ViewGestureControllerImpl(getContext());
        //简单的矩形输出, 图片长宽作为实际矩形, 控件长宽作为显示矩形, 最大放大率10
        output = new SimpleRectangleOutput(getContext());
        output.setMultiTouchMoveEnabled(true);
        //必须实现刷新接口, 调用postInvalidate()刷新
        output.setRefreshListener(new SimpleRectangleOutput.RefreshListener() {
            @Override
            public void onRefresh() {
                SeatSelectionView.this.postInvalidate();
            }
        });
//        //点击事件
//        output.setClickListener(new SimpleRectangleOutput.ClickListener() {
//            @Override
//            public void onClick(float actualX, float actualY, float displayX, float displayY) {
//                Canvas bitmapCanvas = new Canvas(bitmap);
//                bitmapCanvas.drawCircle(actualX, actualY, 30, clickPaint);
//                SeatSelectionView.this.postInvalidate();
//            }
//        });
//        //长按时间
//        output.setLongClickListener(new SimpleRectangleOutput.LongClickListener() {
//            @Override
//            public void onLongClick(float actualX, float actualY, float displayX, float displayY) {
//                Canvas bitmapCanvas = new Canvas(bitmap);
//                bitmapCanvas.drawCircle(actualX, actualY, 80, longClickPaint);
//                SeatSelectionView.this.postInvalidate();
//            }
//        });

        //给手势控制器设置简单矩形输出
        viewGestureController.addOutput(output);

        setData();//TODO 测试代码

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        //接收触摸事件
        viewGestureController.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (seatTable != null){
            seatTable.draw(canvas, output, imagePool);
        }

        //必须:继续刷新
        if (output.isActive())
            postInvalidate();
    }

    Bitmap bitmap = BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat);
    Rect rect;

    public void setData(){

        seatTable = new SeatTable(10, 20, 150, 150, 1);
        output.reset(seatTable.getMatrixWidth(), seatTable.getMatrixHeight(), getWidth(), getHeight(), SimpleRectangleOutput.AUTO_MAGNIFICATION_LIMIT, SimpleRectangleOutput.InitScaleType.FIT_TOP);


        for (int row = 0 ; row < 10 ; row++){
            for (int column = 0 ; column < 20 ; column++){
                if (row == 1 || row == 8){
                    seatTable.setSeat(row, column, new Seat(SeatType.SINGLE, SeatState.NULL, null));
                }else if (column == 2 || column == 18){
                    seatTable.setSeat(row, column, new Seat(SeatType.SINGLE, SeatState.NULL, null));
                } else {
                    seatTable.setSeat(row, column, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, null));
                }
            }
        }

        rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        imagePool = new SeatImagePool() {

            @Override
            public Bitmap getImage(SeatType type, SeatState state) {
                switch (type){
                    case SINGLE:
                        switch (state){
                            case AVAILABLE:
                                return bitmap;
                            default:
                                break;
                        }
                    default:
                        break;
                }
                return null;
            }

            @Override
            public Rect getImageRect(SeatType type, SeatState state) {
                switch (type){
                    case SINGLE:
                        switch (state){
                            case AVAILABLE:
                                return rect;
                            default:
                                break;
                        }
                    default:
                        break;
                }
                return null;
            }
        };

    }

}