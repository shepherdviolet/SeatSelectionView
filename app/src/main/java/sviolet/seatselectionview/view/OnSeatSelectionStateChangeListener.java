package sviolet.seatselectionview.view;

/**
 * Created by S.Violet on 2016/10/12.
 */

public interface OnSeatSelectionStateChangeListener {

    boolean onSeatSelect(int row, int column, Seat seat);

    boolean onSeatDeselect(int row, int column, Seat seat);

    void onUnavailableSeatSelect(int row, int column, Seat seat);

    void onInvalidAreaClick();

}
