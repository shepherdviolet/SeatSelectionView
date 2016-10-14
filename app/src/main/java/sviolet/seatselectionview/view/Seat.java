package sviolet.seatselectionview.view;

/**
 * <p>多人座位以左上角的位子为作为实体, 绘图时, 仅绘制实体座位, 并根据实际作为占的长宽绘制, 占位类型的座位(MULTI_SEAT_PLACEHOLDER)
 * 不进行绘制. 点击时, 若点击到占位类型的座位, 则视为点击到实体座位, 占位类型的座位会持有实体座位(host).</p>
 *
 * Created by S.Violet on 2016/10/8.
 */

public class Seat {

    private SeatType type;//座位类型
    private SeatState state;//座位状态
    private Seat host;//多人座位场合有效, 占位座位持有实体座位

    private int row;
    private int column;

    public Seat(SeatType type, SeatState state, Seat host){
        this.type = type;
        this.state = state;
        this.host = host;
    }

    public SeatState getState() {
        return state;
    }

    public void setState(SeatState state) {
        this.state = state;
    }

    public SeatType getType() {
        return type;
    }

    public Seat getHost() {
        return host;
    }

    void setRowColumn(int row, int column){
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "[Seat]type:" + String.valueOf(type) + " state:" + state;
    }
}
