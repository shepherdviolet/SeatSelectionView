package sviolet.seatselectionview;

import android.os.Bundle;

import sviolet.seatselectionview.parser.SeatDataParseException;
import sviolet.seatselectionview.parser.SimpleSeatDataParser;
import sviolet.seatselectionview.view.OutlineMapImpl;
import sviolet.seatselectionview.view.RowBarImpl;
import sviolet.seatselectionview.view.ScreenBarImpl;
import sviolet.seatselectionview.view.Seat;
import sviolet.seatselectionview.view.SeatImagePoolImpl;
import sviolet.seatselectionview.view.SeatSelectionListener;
import sviolet.seatselectionview.view.SeatSelectionView;
import sviolet.seatselectionview.view.SeatState;
import sviolet.seatselectionview.view.SeatTable;
import sviolet.seatselectionview.view.SeatType;
import sviolet.turquoise.enhance.app.TAppCompatActivity;
import sviolet.turquoise.enhance.app.annotation.inject.ResourceId;
import sviolet.turquoise.util.common.BitmapUtils;
import sviolet.turquoise.util.droid.MeasureUtils;

@ResourceId(R.layout.activity_main)
public class MainActivity extends TAppCompatActivity {

    @ResourceId(R.id.seat_selection_view)
    private SeatSelectionView seatSelectionView;

    private int selectedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        initView(initSeatTable1());
        initView(initSeatTable2());

    }

    /**
     * 代码方式配置座位状态, 为了解释配置方法, 没有采用循环方式填充, 一个座位一个座位配置, 可以解释的比较清楚
     */
    private SeatTable initSeatTable1(){

        //座位5行5列, 座位宽高40dp, 内间距1座位
        SeatTable seatTable = new SeatTable(5, 5, MeasureUtils.dp2px(getApplicationContext(), 40), MeasureUtils.dp2px(getApplicationContext(), 40), 1);

        //第一行

        //给显示的第0行配置影院行号1, 用于行标识的显示
        seatTable.setRowId(0, "1");
        //在显示中(0,0)的位置添加一个单人座, 可选状态, 影院行号1, 座位号1
        seatTable.setSeat(0, 0, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, "1", "1"));
        //在显示中(0,2)的位置添加一个单人座, 可选状态, 影院行号1, 座位号2, 也就是空了一列
        seatTable.setSeat(0, 2, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, "1", "2"));
        //在显示中(0,3)的位置添加一个单人座, 可选状态, 影院行号1, 座位号3
        seatTable.setSeat(0, 3, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, "1", "3"));
        //在显示中(0,4)的位置添加一个单人座, 可选状态, 影院行号1, 座位号4
        seatTable.setSeat(0, 4, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, "1", "4"));

        //第二行

        //给显示的第1行配置影院行号2, 用于行标识的显示
        seatTable.setRowId(1, "2");
        //在显示中(1,1)的位置添加一个单人座, 可选状态, 影院行号2, 座位号1, 前面空了一个位子
        seatTable.setSeat(1, 1, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, "2", "1"));
        //在显示中(1,2)的位置添加一个单人座, 不可选状态, 影院行号2, 座位号2
        seatTable.setSeat(1, 2, new Seat(SeatType.SINGLE, SeatState.UNAVAILABLE, "2", "2"));
        //在显示中(1,3)的位置添加一个单人座, 不可选状态, 影院行号2, 座位号3
        seatTable.setSeat(1, 3, new Seat(SeatType.SINGLE, SeatState.UNAVAILABLE, "2", "3"));
        //在显示中(1,4)的位置添加一个单人座, 可选状态, 影院行号2, 座位号4
        seatTable.setSeat(1, 4, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, "2", "4"));

        //显示第三行留空

        //第四行

        //给显示的第3行配置影院行号3, 用于行标识的显示
        seatTable.setRowId(3, "3");
        //在显示中(3,1)的位置添加一个单人座, 不可选状态, 影院行号3, 座位号1, 前面空了一个位子
        seatTable.setSeat(3, 1, new Seat(SeatType.SINGLE, SeatState.UNAVAILABLE, "3", "1"));
        //在显示中(3,2)的位置添加一个单人座, 可选状态, 影院行号3, 座位号2
        seatTable.setSeat(3, 2, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, "3", "2"));
        //在显示中(3,3)的位置添加一个单人座, 可选状态, 影院行号3, 座位号3
        seatTable.setSeat(3, 3, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, "3", "3"));
        //在显示中(3,4)的位置添加一个单人座, 可选状态, 影院行号3, 座位号4
        seatTable.setSeat(3, 4, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, "3", "4"));

        //第五行, 情侣座

        //给显示的第4行配置影院行号4, 用于行标识的显示
        seatTable.setRowId(4, "4");
        //因为情侣座是两个位置连座, 且同时选中或取消, 我们定义左边的座位为实体座位, 右边为占位座位, 两者相互持有
        //座位的选择状态由实体座位决定, 绘图时也仅绘制实体座位, 占位座位不绘制, 在点选占位座位时, 会映射到实体座位触发回调
        //实体座位的getPlaceholders()方法可获得它对应的占位座位, 用来获得所有座位的影院行号座位号
        //先实例化一个情侣座, 可选状态, 影院行号4, 座位号1, 情侣座左边的座位视为座位实体
        Seat seat = new Seat(SeatType.COUPLE, SeatState.AVAILABLE, "4", "1");
        //利用实体座位创建一个占位座位, 即为情侣座右边的座位, 并配置行号座位号
        Seat placeholder = seat.createMultiSeatPlaceholder("4", "2");
        //放入实体座位
        seatTable.setSeat(4, 0, seat);
        //在实体座位右侧放入对应的占位座位
        seatTable.setSeat(4, 1, placeholder);
        //配置第二个情侣座, 不可选状态
        seat = new Seat(SeatType.COUPLE, SeatState.UNAVAILABLE, "4", "3");
        placeholder = seat.createMultiSeatPlaceholder("4", "4");
        seatTable.setSeat(4, 2, seat);
        seatTable.setSeat(4, 3, placeholder);

        return seatTable;
    }

    private SeatTable initSeatTable2(){

        try {
            SimpleSeatDataParser parser = new SimpleSeatDataParser(10, 20, MeasureUtils.dp2px(getApplicationContext(), 40), MeasureUtils.dp2px(getApplicationContext(), 40), 2);
            parser.addRow(0, "1",
                    "N|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|N",
                    "N|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|N",
                    "N|A|A|A|A|A|A|A|A|A|A|A|A|A|A|A|A|A|A|N");
            parser.addRow(1, "2",
                    "N|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|N",
                    "N|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|N",
                    "N|A|A|A|A|A|A|A|A|A|A|A|A|A|A|A|A|A|A|N");
            parser.addRow(3, "3",
                    "N|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|N|N|N|N",
                    "N|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|N|N|N|N",
                    "N|A|A|A|A|U|U|U|A|A|A|A|U|U|A|A|N|N|N|N");
            parser.addRow(4, "4",
                    "N|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|N|N|N|N",
                    "N|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|N|N|N|N",
                    "N|A|A|A|A|A|A|A|A|A|A|A|A|A|A|A|N|N|N|N");
            parser.addRow(5, "5",
                    "N|N|2|3|4|5|6|7|8|9|10|11|12|13|14|15|N|N|N|N",
                    "N|N|S|S|S|S|S|S|S|S|S|S|S|S|S|S|N|N|N|N",
                    "N|N|A|A|A|A|U|U|U|U|U|U|A|A|A|A|N|N|N|N");
            parser.addRow(6, "6",
                    "N|N|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|N",
                    "N|N|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|N",
                    "N|N|A|A|A|A|A|A|U|U|U|U|A|A|A|A|A|A|A|N");
            parser.addRow(8, "7",
                    "N|N|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|N",
                    "N|N|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|S|N",
                    "N|N|A|A|A|A|A|A|A|A|A|A|A|U|U|A|A|A|A|N");
            parser.addRow(9, "8",
                    "N|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|N",
                    "N|C|H|C|H|C|H|C|H|C|H|C|H|C|H|C|H|C|H|N",
                    "N|A|A|A|A|U|U|A|A|A|A|A|A|U|U|A|A|A|A|N");
            return parser.parse();
        } catch (SeatDataParseException e) {
            //解析异常处理
            e.printStackTrace();
        }

        return null;
    }

    private void initView(SeatTable seatTable){

        //配置座位数据
        seatSelectionView.setData(seatTable);
        //配置行标记
        seatSelectionView.setRowBar(new RowBarImpl(0x80000000, 0xFFF0F0F0, MeasureUtils.dp2px(getApplicationContext(), 16), 10, MeasureUtils.dp2px(getApplicationContext(), 20)));
        //配置屏幕标记
        seatSelectionView.setScreenBar(new ScreenBarImpl(0xFFD0D0D0, 0xFFFFFFFF, MeasureUtils.dp2px(getApplicationContext(), 22), 0.5f, 0.02f, "大屏幕啊啊啊啊", MeasureUtils.dp2px(getApplicationContext(), 16)));
        //配置概要图
        seatSelectionView.setOutlineMap(new OutlineMapImpl(MeasureUtils.getScreenWidth(getApplicationContext()) / 3, 0x80000000, 0xFFFFFFFF, 0xFFFF0000, 0xFF00FF00, 0xE0FF0000, MeasureUtils.dp2px(getApplicationContext(), 1)));

        //配置座位各种状态的图片
        SeatImagePoolImpl imagePool = new SeatImagePoolImpl();
        imagePool.setImage(SeatType.SINGLE, SeatState.AVAILABLE, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_available));
        imagePool.setImage(SeatType.SINGLE, SeatState.UNAVAILABLE, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_unavailable));
        imagePool.setImage(SeatType.SINGLE, SeatState.SELECTED, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_selected));
        imagePool.setImage(SeatType.COUPLE, SeatState.AVAILABLE, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_couple_available));
        imagePool.setImage(SeatType.COUPLE, SeatState.UNAVAILABLE, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_couple_unavailable));
        imagePool.setImage(SeatType.COUPLE, SeatState.SELECTED, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_couple_selected));
        seatSelectionView.setImagePool(imagePool);

        //配置座位选择监听器
        seatSelectionView.setSeatSelectionListener(new SeatSelectionListener() {
            @Override
            public boolean onSeatSelect(Seat seat) {
                int seatNum = 0;
                switch (seat.getType()){
                    case SINGLE:
                        seatNum = 1;
                        break;
                    case COUPLE:
                        seatNum = 2;
                        break;
                    default:
                        break;
                }
                if (selectedCount + seatNum > 4){
                    return false;
                }
                selectedCount += seatNum;

                return true;
            }

            @Override
            public boolean onSeatDeselect(Seat seat) {
                int seatNum = 0;
                switch (seat.getType()){
                    case SINGLE:
                        seatNum = 1;
                        break;
                    case COUPLE:
                        seatNum = 2;
                        break;
                    default:
                        break;
                }
                selectedCount -= seatNum;
                return true;
            }

            @Override
            public void onUnavailableSeatSelect(Seat seat) {

            }

            @Override
            public void onInvalidAreaClick() {

            }
        });

    }

}
