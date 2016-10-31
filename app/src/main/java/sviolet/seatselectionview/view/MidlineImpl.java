package sviolet.seatselectionview.view;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * 影厅中线标记
 *
 * Created by S.Violet on 2016/10/26.
 */

public class MidLineImpl implements MidLine {

    private boolean isUnderSeatLayer;

    private Paint paint;

    private Path path = new Path();
    private SimpleRectangleOutput.Point point = new SimpleRectangleOutput.Point();

    /**
     * @param width 中线宽度
     * @param color 中线颜色
     * @param isUnderSeatLayer 中线是否绘制在座位层下方
     * @param dashEffect 中线效果
     */
    public MidLineImpl(float width, int color, boolean isUnderSeatLayer, float[] dashEffect) {

        this.isUnderSeatLayer = isUnderSeatLayer;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PathEffect effects = new DashPathEffect(dashEffect, 0);
        paint.setPathEffect(effects);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(width);
    }

    @Override
    public void draw(Canvas canvas, Rect srcRect, Rect dstRect, SimpleRectangleOutput output, SeatTable seatTable) {

        //将实际矩形的中线映射到显示矩形中
        float x = seatTable.getMatrixWidth() / 2f;
        output.mappingActualPointToDisplay(x, 0, point);

        //路径
        path.reset();
        path.moveTo((float)point.getX(), 0);
        path.lineTo((float)point.getX(), canvas.getHeight());

        canvas.drawPath(path, paint);
    }

    @Override
    public boolean isUnderSeatLayer() {
        return isUnderSeatLayer;
    }

}
