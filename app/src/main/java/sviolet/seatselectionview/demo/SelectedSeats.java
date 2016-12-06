package sviolet.seatselectionview.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sviolet.seatselectionview.R;
import sviolet.seatselectionview.view.Seat;
import sviolet.seatselectionview.view.SeatSelectionView;
import sviolet.seatselectionview.view.SeatType;

/**
 * Created by S.Violet on 2016/12/6.
 */

public class SelectedSeats {

    private int maxSeatNum;

    private List<Seat> seats;

    private SeatSelectionView seatSelectionView;
    private LinearLayout selectedItemContainer;
    private List<View> selectedItemViews;
    private List<TextView> selectedItemTextViews;

    public SelectedSeats(Context context, SeatSelectionView seatSelectionView, LinearLayout selectedItemContainer, int maxSeatNum) {
        if (seatSelectionView == null){
            throw new RuntimeException("[SelectedSeats]seatSelectionView is null");
        }
        if (selectedItemContainer == null){
            throw new RuntimeException("[SelectedSeats]selectedItemContainer is null");
        }
        if (maxSeatNum < 0){
            throw new RuntimeException("[SelectedSeats]maxSeatNum must >= 0");
        }

        this.seatSelectionView = seatSelectionView;
        this.selectedItemContainer = selectedItemContainer;
        this.maxSeatNum = maxSeatNum;
        this.seats = new ArrayList<>(maxSeatNum);

        this.selectedItemViews = new ArrayList<>(maxSeatNum);
        this.selectedItemTextViews = new ArrayList<>(maxSeatNum);

        //实例化底部栏的选中项View
        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0 ; i < maxSeatNum ; i++){
            View itemView = inflater.inflate(R.layout.seat_selection_bottom_bar_item, null);
            TextView textView = (TextView) itemView.findViewById(R.id.seat_selection_bottom_bar_item);
            itemView.setTag(i);
            itemView.setOnClickListener(onSelectedItemClickListener);
            selectedItemViews.add(itemView);
            selectedItemTextViews.add(textView);
        }
    }

    public boolean onSelect(Seat seat){
        if (seat == null){
            return true;
        }
        switch (seat.getType()){
            case SINGLE:
                if (getSeatNum() + 1 > maxSeatNum){
                    return false;
                }
                seats.add(seat);
                break;
            case COUPLE:
                List<Seat> placeHolders = seat.getPlaceholders();
                int placeHolderNum = placeHolders == null ? 0 : placeHolders.size();
                if (getSeatNum() + 1 + placeHolderNum > maxSeatNum){
                    return false;
                }
                seats.add(seat);
                if (placeHolders != null) {
                    seats.addAll(placeHolders);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    public boolean onDeselect(Seat seat){
        return removeSeat(seat);
    }

    public boolean removeSeat(Seat seat){
        if (seat == null){
            return true;
        }
        Seat host = null;
        //占位类型获取主座位
        if (seat.getType() == SeatType.MULTI_SEAT_PLACEHOLDER){
            host = seat.getHost();
        }
        //默认为本身
        if (host == null){
            host = seat;
        }
        //移除主座位
        remove(host);
        //占位类型座位
        List<Seat> placeHolders = host.getPlaceholders();
        if (placeHolders == null){
            return true;
        }
        //移除所有占位类型座位
        for (Seat placeHolder : placeHolders){
            remove(placeHolder);
        }
        return true;
    }

    private boolean remove(Seat seat){
        if (seat == null){
            return false;
        }
        boolean removed = false;
        for (int i = 0 ; i < seats.size() ; i++){
            if (seats.get(i) == seat){
                seats.remove(i);
                i--;
                removed = true;
            }
        }
        return removed;
    }

    public Seat getSeat(int index){
        if (index < 0 || index >= seats.size()){
            return null;
        }
        return seats.get(index);
    }

    public int getSeatNum(){
        return seats.size();
    }

    public void refreshBottomBarSelectedItems(){
        selectedItemContainer.removeAllViews();
        if (seats.size() > maxSeatNum){
            throw new RuntimeException("[SelectedSeats]size of selected seats is larger than max");
        }
        for (int i = 0 ; i < seats.size() ; i++){
            Seat seat = seats.get(i);
            selectedItemTextViews.get(i).setText(seat.getRowId() + "排" + seat.getColumnId() + "座");
            selectedItemContainer.addView(selectedItemViews.get(i));
        }
    }

    private final View.OnClickListener onSelectedItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Seat seat = getSeat((int) v.getTag());
            removeSeat(seat);
            seatSelectionView.deselectSeat(seat);
            refreshBottomBarSelectedItems();
        }
    };

}
