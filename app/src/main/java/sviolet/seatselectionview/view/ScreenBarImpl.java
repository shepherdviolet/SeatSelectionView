package sviolet.seatselectionview.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * 屏幕标识
 * Created by S.Violet on 2016/10/18.
 */

public class ScreenBarImpl implements ScreenBar {

    private int backgroundColor;
    private int textColor;
    private float barHeight;
    private float screenWidthPercent;
    private float trapezoidFactor;
    private String text;
    private float textSize;

    private Paint paint;
    private float textWidth;
    private float textHeight;
    private Paint.FontMetrics fontMetrics;

    private Path backgroundPath = new Path();
    private SimpleRectangleOutput.Point leftPoint = new SimpleRectangleOutput.Point();
    private SimpleRectangleOutput.Point rightPoint = new SimpleRectangleOutput.Point();

    /**
     * @param screenWidthPercent 屏幕占影厅宽度的比例
     * @param barHeight 屏幕条高度
     * @param trapezoidFactor 屏幕梯型梯度因子
     * @param backgroundColor 背景色
     * @param textColor 字体颜色
     * @param text 屏幕名称
     * @param textSize 字体大小
     */
    public ScreenBarImpl(float screenWidthPercent, float barHeight, float trapezoidFactor, int backgroundColor, int textColor, String text, float textSize) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.barHeight = barHeight;
        this.screenWidthPercent = screenWidthPercent;
        this.trapezoidFactor = trapezoidFactor;
        this.text = text;
        this.textSize = textSize;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backgroundColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        textWidth = paint.measureText(text);//测量文本宽度
        fontMetrics = paint.getFontMetrics();//测量字体属性: top:baseline到最高的距离, bottom:baseline到最低的距离
        textHeight = fontMetrics.bottom - fontMetrics.top;//字体高度

    }

    @Override
    public void draw(Canvas canvas, Rect srcRect, Rect dstRect, SimpleRectangleOutput output, SeatTable seatTable) {

        //通过将座位矩形的点映射到显示矩形, 来获得绘制坐标和宽度
        output.mappingActualPointToDisplay(0, 0, leftPoint);
        output.mappingActualPointToDisplay(seatTable.getMatrixWidth(), 0, rightPoint);

        //显示矩形中整个影厅的宽度
        float displayWidth = (float) (rightPoint.getX() - leftPoint.getX());
//        float displaySeatWidth = displayWidth / (seatTable.getColumnNum() + 2 * seatTable.getPadding());
        //屏幕宽度
        float screenWidth = displayWidth * screenWidthPercent;

        //保证屏幕宽度大于等于文字宽度
        if (screenWidth < textWidth * 1.2f){
            screenWidth = textWidth * 1.2f;
        }

        //计算屏幕的绘图路径
        float left = (float) (leftPoint.getX() + (displayWidth - screenWidth) / 2);
        float right = left + screenWidth;
        float bottomLeft = left + screenWidth * trapezoidFactor;
        float bottomRight = right - screenWidth * trapezoidFactor;

        //路径超过显示范围的, 调整路径, 防止路径过长
        if (bottomLeft < 0){
            left = 0;
            bottomLeft = 0;
        }
        if (bottomRight > canvas.getWidth()){
            right = canvas.getWidth();
            bottomRight = canvas.getWidth();
        }

        //背景路径
        backgroundPath.reset();
        backgroundPath.moveTo(left, 0);
        backgroundPath.lineTo(right, 0);
        backgroundPath.lineTo(bottomRight, barHeight);
        backgroundPath.lineTo(bottomLeft, barHeight);

        //绘制背景
        paint.setColor(backgroundColor);
        canvas.drawPath(backgroundPath, paint);

        //文字位置
        float baseline = (barHeight - fontMetrics.bottom - fontMetrics.top) / 2;
        float textLeft = (float) (leftPoint.getX() + displayWidth / 2f);

        //绘制文字
        paint.setColor(textColor);
        canvas.drawText(text, textLeft, baseline, paint);

    }

}
