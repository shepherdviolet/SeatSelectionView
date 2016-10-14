package sviolet.seatselectionview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import sviolet.turquoise.ui.util.ViewCommonUtils;
import sviolet.turquoise.uix.viewgesturectrl.ViewGestureControllerImpl;
import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;
import sviolet.turquoise.utilx.tlogger.TLogger;

/**
 *
 * Created by S.Violet on 2016/9/21.
 */

public class SeatSelectionView extends View implements ViewCommonUtils.InitListener {

    private TLogger logger = TLogger.get(this, SeatSelectionView.class.getSimpleName());

    //触摸控制器
    private ViewGestureControllerImpl viewGestureController;
    //触摸输出
    private SimpleRectangleOutput output;

    //座位表
    private SeatTable seatTable;
    //座位图片池
    private SeatImagePool imagePool;

    //选座监听器
    private OnSeatSelectionStateChangeListener listener;

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
        //点击事件
        output.setClickListener(new SimpleRectangleOutput.ClickListener() {
            @Override
            public void onClick(float actualX, float actualY, float displayX, float displayY) {
                if (seatTable == null){
                    return;
                }
                Seat seat = seatTable.getSeatByCoordinate(actualX, actualY);
                if(seat == null){
                    callbackInvalidAreaClick();
                    return;
                }
                if (seat.getType() == SeatType.MULTI_SEAT_PLACEHOLDER){
                    seat = seat.getHost();
                }
                if(seat == null || seat.getType() == SeatType.MULTI_SEAT_PLACEHOLDER){
                    logger.e("illegal seatTable data, the host of placeholder is null or another placeholder");
                    callbackInvalidAreaClick();
                    return;
                }
                switch (seat.getState()){
                    case AVAILABLE:
                        if (callbackSeatSelect(seat.getRow(), seat.getColumn())){
                            seat.setState(SeatState.SELECTED);
                        }
                        break;
                    case SELECTED:
                        if (callbackSeatDeselect(seat.getRow(), seat.getColumn())){
                            seat.setState(SeatState.AVAILABLE);
                        }
                        break;
                    case UNAVAILABLE:
                        callbackUnavailableSeatSelect(seat.getRow(), seat.getColumn());
                        break;
                    default:
                        callbackInvalidAreaClick();
                        break;
                }
                postInvalidate();
            }
        });

        //给手势控制器设置简单矩形输出
        viewGestureController.addOutput(output);

        setData(seatTable);

    }

    private boolean callbackSeatSelect(int row, int column){
        if (listener == null){
            return true;
        }
        return listener.onSeatSelect(row, column);
    }

    private boolean callbackSeatDeselect(int row, int column){
        if (listener == null){
            return true;
        }
        return listener.onSeatDeselect(row, column);
    }

    private void callbackUnavailableSeatSelect(int row, int column){
        if (listener == null){
            return;
        }
        listener.onUnavailableSeatSelect(row, column);
    }

    private void callbackInvalidAreaClick(){
        if (listener == null){
            return;
        }
        listener.onInvalidAreaClick();
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
            if (imagePool == null){
                logger.e("imagePool is null, can not draw seats");
                return;
            }

            //绘制座位
            seatTable.draw(canvas, output, imagePool);

            //必须:继续刷新
            if (output.isActive())
                postInvalidate();
        }

    }

    public void setImagePool(SeatImagePool imagePool){
        this.imagePool = imagePool;
    }

    public void setOnSeatSelectionStateChangeListener(OnSeatSelectionStateChangeListener listener){
        this.listener = listener;
    }

    public void setData(SeatTable seatTable){
        if (seatTable == null){
            return;
        }

        this.seatTable = seatTable;

        if (output != null) {
            output.reset(seatTable.getMatrixWidth(), seatTable.getMatrixHeight(), getWidth(), getHeight(), SimpleRectangleOutput.AUTO_MAGNIFICATION_LIMIT, SimpleRectangleOutput.InitScaleType.FIT_TOP);
            postInvalidate();
        }

    }


}