package sviolet.seatselectionview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import sviolet.turquoise.enhance.common.WeakHandler;
import sviolet.turquoise.ui.util.ViewCommonUtils;
import sviolet.turquoise.uix.viewgesturectrl.ViewGestureControllerImpl;
import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;
import sviolet.turquoise.utilx.tlogger.TLogger;

/**
 *
 * <p>XML中使用SeatSelectionView:</p>
 * <pre>{@code
 *     <sviolet.seatselectionview.view.SeatSelectionView
 *          android:id="@+id/seat_selection_selection_view"
 *          android:layout_width="match_parent"
 *          android:layout_height="match_parent"
 *          android:clickable="true" />
 * }</pre>
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
    //背景色
    private int backgroundColor = 0xFFF0F0F0;

    //座位行标识
    private RowBar rowBar;
    //屏幕标识
    private ScreenBar screenBar;
    //概览图
    private OutlineMap outlineMap;
    //中线
    private MidLine midLine;

    //概览图在滑动停止后的显示延迟
    private long outlineDelay = 500;

    //选座监听器
    private SeatSelectionListener listener;

    //控件尺寸变化控制
    private boolean isMeasured = false;//进行了一次measure
    private int viewWidth;//记录上次控件尺寸
    private int viewHeight;//记录上次控件尺寸
    private double lastClickPointX = 0;//上次点击点
    private double lastClickPointY = 0;//上次点击点

    //优化性能
    private Rect srcRect = new Rect();
    private Rect dstRect = new Rect();

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
        output.setOverMoveResistance(2);
        output.setOverZoomResistance(2);
        output.setScrollDuration(250);
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

                //记录点击坐标
                lastClickPointX = actualX;
                lastClickPointY = actualY;
                //座位太小时, 先放大
                if (output.getCurrZoomMagnification() < output.getMaxZoomMagnification() && output.getCurrZoomMagnification() < (output.getMaxZoomMagnification() - 1) / 4 + 1){
                    output.manualZoom(displayX, displayY, (output.getMaxZoomMagnification() - 1) / 2 + 1, 500);
                    return;
                }
                //通知概要图, 座位状态可能变更, 需要重新绘图
                if (outlineMap != null){
                    outlineMap.outlineChanged();
                }
                //根据触点的实际坐标, 获得座位
                Seat seat = seatTable.getSeatByCoordinate(actualX, actualY);
                if(seat == null){
                    callbackInvalidAreaClick();
                    return;
                }
                //如果是占位类型的作为, 则获取他的主座位
                if (seat.getType() == SeatType.MULTI_SEAT_PLACEHOLDER){
                    seat = seat.getHost();
                }
                //若座位为空, 或主座位还是占位类型的, 则打印错误日志
                if(seat == null || seat.getType() == SeatType.MULTI_SEAT_PLACEHOLDER){
                    logger.e("[SeatSelectionView] illegal seatTable data, the host of placeholder is null or another placeholder, host:" + seat);
                    callbackInvalidAreaClick();
                    return;
                }
                switch (seat.getState()){
                    case AVAILABLE:
                        if (callbackSeatSelect(seat)){
                            seat.setState(SeatState.SELECTED);
                        }
                        break;
                    case SELECTED:
                        if (callbackSeatDeselect(seat)){
                            seat.setState(SeatState.AVAILABLE);
                        }
                        break;
                    case UNAVAILABLE:
                        callbackUnavailableSeatSelect(seat);
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

    private boolean callbackSeatSelect(Seat seat){
        if (listener == null){
            return true;
        }
        return listener.onSeatSelect(seat);
    }

    private boolean callbackSeatDeselect(Seat seat){
        if (listener == null){
            return true;
        }
        return listener.onSeatDeselect(seat);
    }

    private void callbackUnavailableSeatSelect(Seat seat){
        if (listener == null){
            return;
        }
        listener.onUnavailableSeatSelect(seat);
    }

    private void callbackInvalidAreaClick(){
        if (listener == null){
            return;
        }
        listener.onInvalidAreaClick();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        isMeasured = true;
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

        //绘制背景色
        canvas.drawColor(backgroundColor);

        if (output == null || seatTable == null) {
            return;
        }

        if (imagePool == null) {
            logger.e("imagePool is null, can not draw seats");
            return;
        }

        if (seatTable.getMatrixWidth() <= 0 || seatTable.getMatrixHeight() <= 0) {
            logger.w("matrixWidth/matrixHeight of seatTable <= 0");
            return;
        }

        //控件发生Measure
        if (isMeasured) {
            isMeasured = false;
            //判断控件尺寸是否变化
            if(viewWidth != 0 && viewHeight != 0){
                if (viewWidth != getWidth() || viewHeight != getHeight()){
                    //重置显示矩形尺寸
                    output.resetDisplayDimension(getWidth(), getHeight());
                    //保持最后触点位置可见
                    output.manualMoveToShow(lastClickPointX, lastClickPointY, seatTable.getSeatWidth(), 300);
                }
            }
            //记录控件宽高
            viewWidth = getWidth();
            viewHeight = getHeight();
        }

        //从手势控制器的矩形输出中, 获得当前的源矩阵和目标矩阵
        //源矩阵表示座位表中, 该显示到界面上的部分, 坐标系为座位表的坐标系
        //目标矩阵表示界面上绘制图形的部分, 坐标系为显示坐标系
        output.getSrcDstRect(srcRect, dstRect);

        //绘制中线
        if (midLine != null && midLine.isUnderSeatLayer()) {
            midLine.draw(canvas, srcRect, dstRect, output, seatTable);
        }

        //绘制座位
        seatTable.draw(canvas, srcRect, dstRect, output, imagePool);

        //绘制中线
        if (midLine != null && !midLine.isUnderSeatLayer()) {
            midLine.draw(canvas, srcRect, dstRect, output, seatTable);
        }

        //绘制行标识
        if (rowBar != null) {
            rowBar.draw(canvas, srcRect, dstRect, output, seatTable);
        }

        //绘制屏幕
        if (screenBar != null) {
            screenBar.draw(canvas, srcRect, dstRect, output, seatTable);
        }

        boolean isActive = output.isActive();

        //绘制概览图
        if (outlineMap != null) {
            if (isActive) {
                //显示概览图
                outlineMap.setVisible(true);
            } else if (outlineMap.isVisible()) {
                //一定时间后隐藏概览图
                handler.removeMessages(MyHandler.HANDLER_SET_OUTLINE_MAP_INVISIBLE);
                handler.sendEmptyMessageDelayed(MyHandler.HANDLER_SET_OUTLINE_MAP_INVISIBLE, outlineDelay);
            }
            //绘制概览图
            outlineMap.draw(canvas, srcRect, dstRect, output, seatTable);
        }

        //必须:继续刷新
        if (isActive)
            postInvalidate();

    }

    public void setImagePool(SeatImagePool imagePool){
        this.imagePool = imagePool;
    }

    public void setSeatSelectionListener(SeatSelectionListener listener){
        this.listener = listener;
    }

    public void setBackground(int backgroundColor){
        this.backgroundColor = backgroundColor;
    }

    public void setRowBar(RowBar rowBar){
        this.rowBar = rowBar;
    }

    public void setScreenBar(ScreenBar screenBar){
        this.screenBar = screenBar;
    }

    public void setOutlineMap(OutlineMap outlineMap) {
        this.outlineMap = outlineMap;
    }

    public void setMidLine(MidLine midLine){
        this.midLine = midLine;
    }

    public void setData(SeatTable seatTable){
        if (seatTable == null){
            return;
        }

        this.seatTable = seatTable;

        if (output != null) {
            //初始化输出矩形
            output.init(seatTable.getMatrixWidth(), seatTable.getMatrixHeight(), getWidth(), getHeight(), SimpleRectangleOutput.AUTO_MAGNIFICATION_LIMIT, SimpleRectangleOutput.InitScaleType.FIT_TOP);
            output.manualZoom(getWidth() / 2, 0, (output.getMaxZoomMagnification() - 1) / 3 + 1, 0);
            postInvalidate();
        }

    }

    public void setOutlineDelay(long outlineDelay) {
        this.outlineDelay = outlineDelay;
    }

    /***************************************************************************************8
     * handler
     */

    private MyHandler handler = new MyHandler(Looper.getMainLooper(), this);

    private static class MyHandler extends WeakHandler<SeatSelectionView>{

        private static final int HANDLER_SET_OUTLINE_MAP_INVISIBLE = 1;

        public MyHandler(Looper looper, SeatSelectionView host) {
            super(looper, host);
        }

        @Override
        protected void handleMessageWithHost(Message message, SeatSelectionView seatSelectionView) {
            switch (message.what){
                case HANDLER_SET_OUTLINE_MAP_INVISIBLE:
                    seatSelectionView.setOutlineVisible(false);
                    break;
                default:
                    break;
            }
        }

    }

    private void setOutlineVisible(boolean visible){
        if (outlineMap != null){
            outlineMap.setVisible(visible);
            postInvalidate();
        }
    }


}