package sviolet.seatselectionview;

import android.os.Bundle;
import android.os.Message;

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
import sviolet.turquoise.enhance.common.WeakHandler;
import sviolet.turquoise.util.common.BitmapUtils;
import sviolet.turquoise.util.droid.MeasureUtils;

@ResourceId(R.layout.activity_main)
public class MainActivity extends TAppCompatActivity {

    @ResourceId(R.id.seat_selection_view)
    private SeatSelectionView seatSelectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    private MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends WeakHandler<MainActivity> {

        public MyHandler(MainActivity host) {
            super(host);
        }

        @Override
        protected void handleMessageWithHost(Message message, MainActivity mainActivity) {

        }
    }

    private int selectedCount = 0;

    private void initData(){

        SeatTable seatTable = new SeatTable(10, 20, MeasureUtils.dp2px(getApplicationContext(), 40), MeasureUtils.dp2px(getApplicationContext(), 40), 2);

        for (int row = 0 ; row < 9 ; row++){
            for (int column = 0 ; column < 20 ; column++){
                if (row == 1 || row == 7){
                    seatTable.setSeat(row, column, new Seat(SeatType.SINGLE, SeatState.NULL, null));
                }else if (column == 2 || column == 18){
                    seatTable.setSeat(row, column, new Seat(SeatType.SINGLE, SeatState.NULL, null));
                } else {
                    seatTable.setSeat(row, column, new Seat(SeatType.SINGLE, SeatState.AVAILABLE, null));
                }
            }
        }

        seatTable.getSeat(3, 4).setState(SeatState.UNAVAILABLE);
        seatTable.getSeat(3, 5).setState(SeatState.UNAVAILABLE);
        seatTable.getSeat(3, 6).setState(SeatState.UNAVAILABLE);
        seatTable.getSeat(3, 7).setState(SeatState.UNAVAILABLE);
        seatTable.getSeat(6, 6).setState(SeatState.UNAVAILABLE);
        seatTable.getSeat(6, 7).setState(SeatState.UNAVAILABLE);

        Seat coupleSeat = new Seat(SeatType.COUPLE, SeatState.AVAILABLE, null);
        seatTable.setSeat(9, 0, coupleSeat);
        seatTable.setSeat(9, 1, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));
        coupleSeat = new Seat(SeatType.COUPLE, SeatState.AVAILABLE, null);
        seatTable.setSeat(9, 2, coupleSeat);
        seatTable.setSeat(9, 3, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));
        coupleSeat = new Seat(SeatType.COUPLE, SeatState.UNAVAILABLE, null);
        seatTable.setSeat(9, 4, coupleSeat);
        seatTable.setSeat(9, 5, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));
        coupleSeat = new Seat(SeatType.COUPLE, SeatState.UNAVAILABLE, null);
        seatTable.setSeat(9, 6, coupleSeat);
        seatTable.setSeat(9, 7, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));
        coupleSeat = new Seat(SeatType.COUPLE, SeatState.AVAILABLE, null);
        seatTable.setSeat(9, 8, coupleSeat);
        seatTable.setSeat(9, 9, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));
        coupleSeat = new Seat(SeatType.COUPLE, SeatState.AVAILABLE, null);
        seatTable.setSeat(9, 10, coupleSeat);
        seatTable.setSeat(9, 11, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));
        coupleSeat = new Seat(SeatType.COUPLE, SeatState.AVAILABLE, null);
        seatTable.setSeat(9, 12, coupleSeat);
        seatTable.setSeat(9, 13, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));
        coupleSeat = new Seat(SeatType.COUPLE, SeatState.UNAVAILABLE, null);
        seatTable.setSeat(9, 14, coupleSeat);
        seatTable.setSeat(9, 15, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));
        coupleSeat = new Seat(SeatType.COUPLE, SeatState.AVAILABLE, null);
        seatTable.setSeat(9, 16, coupleSeat);
        seatTable.setSeat(9, 17, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));
        coupleSeat = new Seat(SeatType.COUPLE, SeatState.AVAILABLE, null);
        seatTable.setSeat(9, 18, coupleSeat);
        seatTable.setSeat(9, 19, new Seat(SeatType.MULTI_SEAT_PLACEHOLDER, SeatState.NULL, coupleSeat));

        seatTable.setRowId(0, "1");
        seatTable.setRowId(2, "2");
        seatTable.setRowId(3, "3");
        seatTable.setRowId(4, "4");
        seatTable.setRowId(5, "5");
        seatTable.setRowId(6, "6");
        seatTable.setRowId(8, "7");
        seatTable.setRowId(9, "8");

        SeatImagePoolImpl imagePool = new SeatImagePoolImpl();
        imagePool.setImage(SeatType.SINGLE, SeatState.AVAILABLE, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_available));
        imagePool.setImage(SeatType.SINGLE, SeatState.UNAVAILABLE, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_unavailable));
        imagePool.setImage(SeatType.SINGLE, SeatState.SELECTED, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_selected));
        imagePool.setImage(SeatType.COUPLE, SeatState.AVAILABLE, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_couple_available));
        imagePool.setImage(SeatType.COUPLE, SeatState.UNAVAILABLE, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_couple_unavailable));
        imagePool.setImage(SeatType.COUPLE, SeatState.SELECTED, BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_couple_selected));

        seatSelectionView.setImagePool(imagePool);
        seatSelectionView.setData(seatTable);
        seatSelectionView.setRowBar(new RowBarImpl(0x80000000, 0xFFF0F0F0, MeasureUtils.dp2px(getApplicationContext(), 16), 10, MeasureUtils.dp2px(getApplicationContext(), 20)));
        seatSelectionView.setScreenBar(new ScreenBarImpl(0xFFD0D0D0, 0xFFFFFFFF, MeasureUtils.dp2px(getApplicationContext(), 22), 0.5f, 0.02f, "大屏幕啊啊啊啊", MeasureUtils.dp2px(getApplicationContext(), 16)));
        seatSelectionView.setOutlineMap(new OutlineMapImpl(MeasureUtils.getScreenWidth(getApplicationContext()) / 3, 0x80000000, 0xFFFFFFFF, 0xFFFF0000, 0xFF00FF00, 0xE0FF0000, MeasureUtils.dp2px(getApplicationContext(), 1)));

        seatSelectionView.setSeatSelectionListener(new SeatSelectionListener() {
            @Override
            public boolean onSeatSelect(int row, int column, Seat seat) {
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
            public boolean onSeatDeselect(int row, int column, Seat seat) {
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
            public void onUnavailableSeatSelect(int row, int column, Seat seat) {

            }

            @Override
            public void onInvalidAreaClick() {

            }
        });

    }

}
