package sviolet.seatselectionview.view;

import android.graphics.Canvas;
import android.graphics.Rect;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * 座位的行标识
 *
 * Created by S.Violet on 2016/10/17.
 */

public interface RowBar {

    void draw(Canvas canvas, Rect srcRect, Rect dstRect, SimpleRectangleOutput output, SeatTable seatTable);

}
