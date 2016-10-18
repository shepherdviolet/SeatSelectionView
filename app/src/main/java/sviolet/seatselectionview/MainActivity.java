package sviolet.seatselectionview;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;

import sviolet.seatselectionview.view.SeatSelectionListener;
import sviolet.seatselectionview.view.RowBarImpl;
import sviolet.seatselectionview.view.Seat;
import sviolet.seatselectionview.view.SeatImagePool;
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

    private Bitmap bitmapAvailable;
    private Bitmap bitmapUnavailable;
    private Bitmap bitmapSelected;
    private Rect rectAvailable;
    private Rect rectUnavailable;
    private Rect rectSelected;
    private Bitmap bitmapCoupleAvailable;
    private Bitmap bitmapCoupleUnavailable;
    private Bitmap bitmapCoupleSelected;
    private Rect rectCoupleAvailable;
    private Rect rectCoupleUnavailable;
    private Rect rectCoupleSelected;
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

        bitmapAvailable = BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_available);
        rectAvailable = new Rect(0, 0, bitmapAvailable.getWidth(), bitmapAvailable.getHeight());
        bitmapUnavailable = BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_unavailable);
        rectUnavailable = new Rect(0, 0, bitmapUnavailable.getWidth(), bitmapUnavailable.getHeight());
        bitmapSelected = BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_selected);
        rectSelected = new Rect(0, 0, bitmapSelected.getWidth(), bitmapSelected.getHeight());

        bitmapCoupleAvailable = BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_couple_available);
        rectCoupleAvailable = new Rect(0, 0, bitmapCoupleAvailable.getWidth(), bitmapCoupleAvailable.getHeight());
        bitmapCoupleUnavailable = BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_couple_unavailable);
        rectCoupleUnavailable = new Rect(0, 0, bitmapCoupleUnavailable.getWidth(), bitmapCoupleUnavailable.getHeight());
        bitmapCoupleSelected = BitmapUtils.decodeFromResource(getResources(), R.mipmap.seat_couple_selected);
        rectCoupleSelected = new Rect(0, 0, bitmapCoupleSelected.getWidth(), bitmapCoupleSelected.getHeight());

        SeatImagePool imagePool = new SeatImagePool() {

            @Override
            public Bitmap getImage(SeatType type, SeatState state) {
                switch (type){
                    case SINGLE:
                        switch (state){
                            case AVAILABLE:
                                return bitmapAvailable;
                            case UNAVAILABLE:
                                return bitmapUnavailable;
                            case SELECTED:
                                return bitmapSelected;
                            default:
                                break;
                        }
                        break;
                    case COUPLE:
                        switch (state){
                            case AVAILABLE:
                                return bitmapCoupleAvailable;
                            case UNAVAILABLE:
                                return bitmapCoupleUnavailable;
                            case SELECTED:
                                return bitmapCoupleSelected;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                return null;
            }

            @Override
            public Rect getImageRect(SeatType type, SeatState state) {
                switch (type){
                    case SINGLE:
                        switch (state){
                            case AVAILABLE:
                                return rectAvailable;
                            case UNAVAILABLE:
                                return rectUnavailable;
                            case SELECTED:
                                return rectSelected;
                            default:
                                break;
                        }
                        break;
                    case COUPLE:
                        switch (state){
                            case AVAILABLE:
                                return rectCoupleAvailable;
                            case UNAVAILABLE:
                                return rectCoupleUnavailable;
                            case SELECTED:
                                return rectCoupleSelected;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                return null;
            }
        };

        seatSelectionView.setImagePool(imagePool);
        seatSelectionView.setData(seatTable);
        seatSelectionView.setRowBar(new RowBarImpl(0x80000000, 0xFFFFFFFF, MeasureUtils.dp2px(getApplicationContext(), 16), 10, MeasureUtils.dp2px(getApplicationContext(), 20)));

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
