package sviolet.seatselectionview.view;

/**
 * <p>多人座位以左上角的位子为作为实体, 绘图时, 仅绘制实体座位, 并根据实际作为占的长宽绘制, 占位类型的座位(MULTI_SEAT_PLACEHOLDER)
 * 不进行绘制. 点击时, 若点击到占位类型的座位, 则视为点击到实体座位, 占位类型的座位会持有实体座位(host).</p>
 *
 * <p>例如情侣座:左边的座位为实体座位, 右边的座位为占位类型的座位(MULTI_SEAT_PLACEHOLDER), 两个加起来表示一个情侣座.</p>
 *
 * Created by S.Violet on 2016/10/8.
 */

public class Seat {

    private SeatType type;//座位类型
    private SeatState state;//座位状态
    private Seat host;//多人座位场合有效, 占位座位持有实体座位
    private String seatId;//辅助参数, 可设置座位的ID, 用于发送交易

    private int row;
    private int column;

    public Seat(SeatType type, SeatState state, Seat host){
        this.type = type;
        this.state = state;
        this.host = host;
    }

    public Seat(SeatType type, SeatState state, Seat host, String seatId){
        this.type = type;
        this.state = state;
        this.host = host;
        this.seatId = seatId;
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

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    @Override
    public String toString() {
        return "[Seat]type:" + String.valueOf(type) + " state:" + state + " row:" + row + " column:" + column;
    }
}
