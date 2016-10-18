package sviolet.seatselectionview.view;

/**
 * <p>多人座位以左上角的位子为作为实体, 绘图时, 仅绘制实体座位, 并根据实际作为占的长宽绘制, 占位类型的座位(MULTI_SEAT_PLACEHOLDER)
 * 不进行绘制. 点击时, 若点击到占位类型的座位, 则视为点击到实体座位, 占位类型的座位会持有实体座位(host).</p>
 *
 * <p>例如情侣座:左边的座位为实体座位, 右边的座位为占位类型的座位(MULTI_SEAT_PLACEHOLDER), 两个加起来表示一个情侣座.</p>
 *
 * Created by S.Violet on 2016/10/8.
 */

public enum SeatType {

    SINGLE(1, 1),//单人座
    COUPLE(1, 2),//双人座
    MULTI_SEAT_PLACEHOLDER(0, 0);//多人座占位类型

    private int row;//座位占用的行数
    private int column;//座位占用的列数

    SeatType(int row, int column){
        this.row = row;
        this.column = column;
    }

    public int getRow(){
        return row;
    }

    public int getColumn(){
        return column;
    }

}
