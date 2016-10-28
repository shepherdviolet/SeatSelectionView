package sviolet.seatselectionview.view;

import android.graphics.Canvas;
import android.graphics.Rect;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * 概要图
 *
 * Created by S.Violet on 2016/10/18.
 */

public interface OutlineMap {

    /**
     * 设置概要图是否显示
     */
    void setVisible(boolean visible);

    /**
     * @return 返回概要图是否显示
     */
    boolean isVisible();

    /**
     * 通知概要图刷新(座位状态变化, 需要重新绘图)
     */
    void outlineChanged();

    /**
     * 绘制
     */
    void draw(Canvas canvas, Rect srcRect, Rect dstRect, SimpleRectangleOutput output, SeatTable seatTable);

}
