package sviolet.seatselectionview.view;

import java.util.ArrayList;
import java.util.List;

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
    private List<Seat> placeHolders;//多人座位场合有效, 实体座位持有占位座位
    private Seat host;//多人座位场合有效, 占位座位持有实体座位

    private int row;//行, 仅用于绘图
    private int column;//列, 仅用于绘图

    private String rowId;//行ID, 实际影厅里的行号, 用于与后端交互
    private String columnId;//列ID, 实际影厅里的座位号, 用于与后端交互

    /**
     * 请不要用构造函数实例化MULTI_SEAT_PLACEHOLDER类型的座位, 请使用实体座位的createMultiSeatPlaceholder方法创建MULTI_SEAT_PLACEHOLDER类型的座位
     * @param type 座位类型
     * @param state 座位状态
     */
    public Seat(SeatType type, SeatState state){
        if (type == SeatType.MULTI_SEAT_PLACEHOLDER){
            throw new RuntimeException("you can not new a MULTI_SEAT_PLACEHOLDER seat by constructor, you should call createMultiSeatPlaceholder method on host seat");
        }
        this.type = type;
        this.state = state;
    }

    /**
     * 请不要用构造函数实例化MULTI_SEAT_PLACEHOLDER类型的座位, 请使用实体座位的createMultiSeatPlaceholder方法创建MULTI_SEAT_PLACEHOLDER类型的座位
     * @param type 座位类型
     * @param state 座位状态
     * @param rowId 影院实际行号
     * @param columnId 影院实际座位号
     */
    public Seat(SeatType type, SeatState state, String rowId, String columnId){
        if (type == SeatType.MULTI_SEAT_PLACEHOLDER){
            throw new RuntimeException("you can not new a MULTI_SEAT_PLACEHOLDER seat by constructor, you should call createMultiSeatPlaceholder method on host seat");
        }
        this.type = type;
        this.state = state;
        this.rowId = rowId;
        this.columnId = columnId;
    }

    private Seat(Seat host, String rowId, String columnId){
        this.type = SeatType.MULTI_SEAT_PLACEHOLDER;
        this.state = SeatState.NULL;
        this.rowId = rowId;
        this.columnId = columnId;
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

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public List<Seat> getPlaceHolders(){
        return placeHolders;
    }

    /**
     * 创建联排座位的占位类型座位
     * @return
     */
    public Seat createMultiSeatPlaceholder(){
        Seat placeHolder = new Seat(this, null, null);
        if (placeHolders == null){
            placeHolders = new ArrayList<>();
        }
        placeHolders.add(placeHolder);
        return placeHolder;
    }

    /**
     * 创建联排座位的占位类型座位
     * @param rowId 占位类型座位的影院实际排号
     * @param columnId 占位类型座位的影院实际座位号
     * @return
     */
    public Seat createMultiSeatPlaceholder(String rowId, String columnId){
        Seat placeHolder = new Seat(this, rowId, columnId);
        if (placeHolders == null){
            placeHolders = new ArrayList<>();
        }
        placeHolders.add(placeHolder);
        return placeHolder;
    }

    @Override
    public String toString() {
        return "[Seat]type:" + String.valueOf(type) + " state:" + state + " row:" + row + " column:" + column + " rowId:" + rowId + " column:" + columnId;
    }
}
