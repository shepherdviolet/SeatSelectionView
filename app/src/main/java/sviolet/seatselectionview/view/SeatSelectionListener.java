package sviolet.seatselectionview.view;

/**
 * Created by S.Violet on 2016/10/12.
 */

public interface SeatSelectionListener {

    /**
     * 选中座位时回调
     * @param seat 选中的座位
     * @return true:座位变为选中状态 false:座位状态保持不变
     */
    boolean onSeatSelect(Seat seat);

    /**
     * 取消选中座位时回调
     * @param seat 选中的座位
     * @return true:座位变为未选中状态 false:座位状态保持不变
     */
    boolean onSeatDeselect(Seat seat);

    /**
     * 选中不可选的座位时回调
     * @param seat 选中的座位
     */
    void onUnavailableSeatSelect(Seat seat);

    void onInvalidAreaClick();

}
