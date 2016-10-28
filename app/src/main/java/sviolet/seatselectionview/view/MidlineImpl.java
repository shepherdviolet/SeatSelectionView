package sviolet.seatselectionview.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * 影厅中线标记
 *
 * Created by S.Violet on 2016/10/26.
 */

public class MidLineImpl implements MidLine {

    private Paint paint;
    private Path path = new Path();

    private SimpleRectangleOutput.Point point = new SimpleRectangleOutput.Point();

    public MidLineImpl() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PathEffect effects = new DashPathEffect(new float[]{10, 10, 10, 10}, 1);
        paint.setPathEffect(effects);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);


    }

    @Override
    public void draw(Canvas canvas, SimpleRectangleOutput output, SeatTable seatTable) {

        //将实际矩形的中线映射到显示矩形中
        float x = seatTable.getMatrixWidth() / 2f;
        output.mappingActualPointToDisplay(x, 0, point);

        //路径
        path.reset();
        path.moveTo((float) point.getX(), 0);
        path.lineTo((float)point.getX(), canvas.getHeight());

        canvas.drawPath(path, paint);
    }

    @Override
    public boolean isUnderSeatLayer() {
        return false;
    }

}
