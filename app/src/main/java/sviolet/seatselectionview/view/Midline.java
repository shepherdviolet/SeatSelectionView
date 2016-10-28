package sviolet.seatselectionview.view;

import android.graphics.Canvas;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * 影厅中线标记
 *
 * Created by S.Violet on 2016/10/26.
 */

public interface MidLine {

    void draw(Canvas canvas, SimpleRectangleOutput output, SeatTable seatTable);

    boolean isUnderSeatLayer();

}
