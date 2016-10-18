package sviolet.seatselectionview.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

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

    public ScreenBarImpl(int backgroundColor, int textColor, float barHeight, float screenWidthPercent, float trapezoidFactor, String text, float textSize) {
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
    public void draw(Canvas canvas, SimpleRectangleOutput output, SeatTable seatTable) {

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

        float left = (float) (leftPoint.getX() + (displayWidth - screenWidth) / 2);
        float right = left + screenWidth;

        backgroundPath.reset();
        backgroundPath.moveTo(left, 0);
        backgroundPath.lineTo(right, 0);
        backgroundPath.lineTo(right - screenWidth * trapezoidFactor, barHeight);
        backgroundPath.lineTo(left + screenWidth * trapezoidFactor, barHeight);

        paint.setColor(backgroundColor);
        canvas.drawPath(backgroundPath, paint);

        float baseline = (barHeight - fontMetrics.bottom - fontMetrics.top) / 2;
        float textLeft = (float) (leftPoint.getX() + displayWidth / 2f);

        paint.setColor(textColor);
        canvas.drawText(text, textLeft, baseline, paint);

    }

}
