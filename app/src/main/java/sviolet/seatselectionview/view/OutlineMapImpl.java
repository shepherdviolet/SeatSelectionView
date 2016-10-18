package sviolet.seatselectionview.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * 概要图
 *
 * Created by S.Violet on 2016/10/18.
 */
public class OutlineMapImpl implements OutlineMap {

    private float outlineWidth;
    private int backgroundColor;
    private int availableColor;
    private int unavailableColor;
    private int selectedColor;
    private int areaColor;
    private float areaStrokeWidth;

    private boolean visible = false;

    private Paint paint;

    protected SimpleRectangleOutput.Point backgroundCoordinate = new SimpleRectangleOutput.Point();

    private Rect canvasClipRect = new Rect();
    private Rect backgroundRect = new Rect();
    private Rect seatRect = new Rect();
    private RectF outputSrcRect = new RectF();

    public OutlineMapImpl(float outlineWidth, int backgroundColor, int availableColor, int unavailableColor, int selectedColor, int areaColor, float areaStrokeWidth) {
        this.outlineWidth = outlineWidth;
        this.backgroundColor = backgroundColor;
        this.availableColor = availableColor;
        this.unavailableColor = unavailableColor;
        this.selectedColor = selectedColor;
        this.areaColor = areaColor;
        this.areaStrokeWidth = areaStrokeWidth;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        paint.setStrokeWidth(areaStrokeWidth);
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void draw(Canvas canvas, SimpleRectangleOutput output, SeatTable seatTable) {
        if (!visible){
            return;
        }

        canvas.getClipBounds(canvasClipRect);

        //显示的行列数
        int displayColumnNum = seatTable.getColumnNum() + seatTable.getPadding() * 2;
        int displayRowNum = seatTable.getRowNum() + seatTable.getPadding() * 2;

        //计算指定的概要图宽度分配到每个座位单元的宽度
        float unitWidth = outlineWidth / (float)displayColumnNum;

        //计算座位宽度
        int seatWidth = (int) Math.floor(unitWidth * 0.6f);//80%分配给座位
        if (seatWidth < 2){
            seatWidth = 2;
        }

        //计算座位间距
        int seatSpacing = (int) Math.floor(unitWidth * 0.4f);//20%分配给间隔
        if (seatSpacing < 1){
            seatSpacing = 1;
        }

        //重新计算矩形宽高
        float backgroundWidth = displayColumnNum * seatWidth + (displayColumnNum + 1) * seatSpacing;
        float backgroundHeight = displayRowNum * seatWidth + (displayRowNum + 1) * seatSpacing;

        //计算矩形左上角坐标
        calculateBackgroundCoordinate(canvasClipRect, backgroundWidth, backgroundHeight);

        backgroundRect.left = (int) backgroundCoordinate.getX();
        backgroundRect.top = (int) backgroundCoordinate.getY();
        backgroundRect.right = (int) (backgroundRect.left + backgroundWidth);
        backgroundRect.bottom = (int) (backgroundRect.top + backgroundHeight);

        //绘制背景
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(backgroundRect, paint);

        //绘制座位
        for (int r = 0 ; r < displayRowNum ; r++){
            for (int c = 0 ; c < displayColumnNum ; c++){
                Seat seat = seatTable.getSeat(r - seatTable.getPadding(), c - seatTable.getPadding());
                if (seat == null || seat.getType() == SeatType.MULTI_SEAT_PLACEHOLDER || seat.getState() == SeatState.NULL){
                    continue;
                }
                seatRect.left = (int) (backgroundCoordinate.getX()) + (c + 1) * seatSpacing + c * seatWidth;
                seatRect.top = (int) (backgroundCoordinate.getY()) + (r + 1) * seatSpacing + r * seatWidth;
                seatRect.right = seatRect.left + seatWidth * seat.getType().getColumn() + seatSpacing * (seat.getType().getColumn() - 1);
                seatRect.bottom = seatRect.top + seatWidth * seat.getType().getRow() + seatSpacing * (seat.getType().getRow() - 1);

                switch (seat.getState()){
                    case AVAILABLE:
                        paint.setColor(availableColor);
                        break;
                    case UNAVAILABLE:
                        paint.setColor(unavailableColor);
                        break;
                    case SELECTED:
                        paint.setColor(selectedColor);
                        break;
                    default:
                        break;
                }
                canvas.drawRect(seatRect, paint);
            }
        }

        //从output获得实际矩形
        output.getSrcDstRectF(outputSrcRect, null);

        outputSrcRect.left = (float) backgroundCoordinate.getX() + outputSrcRect.left * (backgroundWidth / seatTable.getMatrixWidth()) + areaStrokeWidth / 2;
        outputSrcRect.right = (float) backgroundCoordinate.getX() + outputSrcRect.right * (backgroundWidth / seatTable.getMatrixWidth()) - areaStrokeWidth / 2;
        outputSrcRect.top = (float) backgroundCoordinate.getY() + outputSrcRect.top * (backgroundHeight / seatTable.getMatrixHeight()) + areaStrokeWidth / 2;
        outputSrcRect.bottom = (float) backgroundCoordinate.getY() + outputSrcRect.bottom * (backgroundHeight / seatTable.getMatrixHeight()) - areaStrokeWidth / 2;

        //绘制显示区域
        paint.setColor(areaColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(outputSrcRect, paint);
    }

    protected void calculateBackgroundCoordinate(Rect canvasClipRect, float backgroundWidth, float backgroundHeight){
        backgroundCoordinate.setX(canvasClipRect.right - backgroundWidth);
        backgroundCoordinate.setY(canvasClipRect.top);
    }

}
