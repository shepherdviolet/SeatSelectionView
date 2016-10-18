package sviolet.seatselectionview.view;

import android.graphics.Canvas;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * 概要图
 *
 * Created by S.Violet on 2016/10/18.
 */

public interface OutlineMap {

    void setVisible(boolean visible);

    boolean isVisible();

    void draw(Canvas canvas, SimpleRectangleOutput output, SeatTable seatTable);

}
