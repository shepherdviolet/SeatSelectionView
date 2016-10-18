package sviolet.seatselectionview.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * <p>座位行标记图</p>
 *
 * Created by S.Violet on 2016/10/17.
 */

public class RowBarImpl implements RowBar {

    private int backgroundColor;
    private int textColor;
    private float leftPadding;
    private float barWidth;

    private RectF backgroundRect = new RectF();
    private SimpleRectangleOutput.Point topPoint = new SimpleRectangleOutput.Point();
    private SimpleRectangleOutput.Point bottomPoint = new SimpleRectangleOutput.Point();

    private Paint paint;
    private float textWidth;
    private float textHeight;
    private Paint.FontMetrics fontMetrics;

    public RowBarImpl(int backgroundColor, int textColor, int textSize, float leftPadding, float barWidth) {

        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.leftPadding = leftPadding;
        this.barWidth = barWidth;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        textWidth = paint.measureText("4");//测量文本宽度
        fontMetrics = paint.getFontMetrics();//测量字体属性: top:baseline到最高的距离, bottom:baseline到最低的距离
        textHeight = fontMetrics.bottom - fontMetrics.top;//字体高度

    }

    @Override
    public void draw(Canvas canvas, SimpleRectangleOutput output, SeatTable seatTable) {

        //通过将座位矩形的点映射到显示矩形, 来获得绘制坐标和高度
        output.mappingActualPointToDisplay(0, 0, topPoint);
        output.mappingActualPointToDisplay(0, seatTable.getMatrixHeight(), bottomPoint);

        int rowNum = seatTable.getRowNum();
        int padding = seatTable.getPadding();
        //显示矩形中一个座位的高度
        float displaySeatHeight = (float) ((bottomPoint.getY() - topPoint.getY()) / (rowNum + padding * 2));

        //背景矩形
        backgroundRect.left = leftPadding;
        backgroundRect.right = barWidth + leftPadding;
        backgroundRect.top = (float) (topPoint.getY() + padding * displaySeatHeight - textHeight / 4);
        backgroundRect.bottom = (float) (bottomPoint.getY() - padding * displaySeatHeight + textHeight / 4);

        paint.setColor(backgroundColor);
        canvas.drawRoundRect(backgroundRect, barWidth / 2, barWidth / 2, paint);//第二个参数是x半径，第三个参数是y半径

        //绘制行号
        paint.setColor(textColor);
        for (int i = 0 ; i < rowNum ; i++){

            float top = (float) (topPoint.getY() + (padding + i) * displaySeatHeight);
            float bottom = top + displaySeatHeight;
            float baseline = (bottom + top - fontMetrics.bottom - fontMetrics.top) / 2;

            canvas.drawText(Integer.toString(i + 1), leftPadding + barWidth / 2, baseline, paint);
        }

    }

}
