package sviolet.seatselectionview.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sviolet.seatselectionview.R;
import sviolet.seatselectionview.view.Seat;
import sviolet.seatselectionview.view.SeatSelectionView;
import sviolet.seatselectionview.view.SelectedSeats;

/**
 * 选中座位控制器强化, 增加对底部栏的刷新
 *
 * Created by S.Violet on 2016/12/6.
 */
public class MySelectedSeats extends SelectedSeats {

    private AuditoriumInfo auditoriumInfo;
    private View bottomBar;
    private LinearLayout selectedItemContainer;
    private TextView totalPriceTextView;
    private TextView priceDetailTextView;
    private List<View> selectedItemViews;
    private List<TextView> selectedItemTextViews;

    private Animation bottomBarInAnimation;//底部栏动画
    private Animation bottomBarOutAnimation;//底部栏动画

    private boolean isBottomBarShown = false;

    public MySelectedSeats(SeatSelectionView seatSelectionView, AuditoriumInfo auditoriumInfo,
                           Context context, View bottomBar, LinearLayout selectedItemContainer, TextView totalPriceTextView, TextView priceDetailTextView) {

        super(seatSelectionView, auditoriumInfo.getMaxSeatNum());

        if (bottomBar == null){
            throw new RuntimeException("[MySelectedSeats]bottomBar is null");
        }
        if (selectedItemContainer == null){
            throw new RuntimeException("[MySelectedSeats]selectedItemContainer is null");
        }
        if (totalPriceTextView == null){
            throw new RuntimeException("[MySelectedSeats]totalPriceTextView is null");
        }
        if (priceDetailTextView == null){
            throw new RuntimeException("[MySelectedSeats]priceDetailTextView is null");
        }

        this.auditoriumInfo = auditoriumInfo;
        this.bottomBar = bottomBar;
        this.selectedItemContainer = selectedItemContainer;
        this.totalPriceTextView = totalPriceTextView;
        this.priceDetailTextView = priceDetailTextView;
        this.selectedItemViews = new ArrayList<>(auditoriumInfo.getMaxSeatNum());
        this.selectedItemTextViews = new ArrayList<>(auditoriumInfo.getMaxSeatNum());

        //初始化动画
        bottomBarInAnimation = AnimationUtils.loadAnimation(context, R.anim.seat_selection_bottom_bar_in);//加载
        bottomBarOutAnimation = AnimationUtils.loadAnimation(context, R.anim.seat_selection_bottom_bar_out);//加载

        //底部栏当前是否显示
        isBottomBarShown = bottomBar.getVisibility() == View.VISIBLE;

        //实例化底部栏的选中项View
        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0 ; i < auditoriumInfo.getMaxSeatNum() ; i++){
            View itemView = inflater.inflate(R.layout.seat_selection_bottom_bar_item, null);
            TextView textView = (TextView) itemView.findViewById(R.id.seat_selection_bottom_bar_item);
            itemView.setTag(i);
            itemView.setOnClickListener(onSelectedItemClickListener);
            selectedItemViews.add(itemView);
            selectedItemTextViews.add(textView);
        }
    }

    @Override
    public boolean onSelect(Seat seat) {
        boolean result = super.onSelect(seat);
        //刷新数据
        if (result) {
            refreshBottomBar();
        }
        return result;
    }

    @Override
    public boolean onDeselect(Seat seat) {
        boolean result = super.onDeselect(seat);
        //刷新数据
        refreshBottomBar();
        return result;
    }

    public void refreshBottomBar(){
        refreshBottomBarSelectedItems();
        refreshBottomBarPrice();
    }

    public void refreshBottomBarSelectedItems(){
        //先移除全部View
        selectedItemContainer.removeAllViews();
        if (getSeatNum() > getMaxSeatNum()){
            throw new RuntimeException("[MySelectedSeats]size of selected seats is larger than max");
        }
        //根据座位情况塞入View
        for (int i = 0 ; i < getSeatNum() ; i++){
            Seat seat = getSeat(i);
            selectedItemTextViews.get(i).setText(seat.getRowId() + "排" + seat.getColumnId() + "座");
            selectedItemContainer.addView(selectedItemViews.get(i));
        }

        if (!isBottomBarShown && getSeatNum() > 0){
            //显示底边栏
            isBottomBarShown = true;
            bottomBar.startAnimation(bottomBarInAnimation);
            bottomBar.setVisibility(View.VISIBLE);
        } else if (isBottomBarShown && getSeatNum() <= 0){
            //隐藏底边栏
            isBottomBarShown = false;
            bottomBar.startAnimation(bottomBarOutAnimation);
            bottomBar.setVisibility(View.GONE);
        }
    }

    public void refreshBottomBarPrice(){
        int seatNum = getSeatNum();
        float totalPrice = seatNum * auditoriumInfo.getPrice();
        totalPriceTextView.setText(totalPrice + "元");
        priceDetailTextView.setText(auditoriumInfo.getPrice() + "元 X " + seatNum);
    }

    private final View.OnClickListener onSelectedItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Seat seat = getSeat((int) v.getTag());
            //移除座位
            removeSeat(seat);
            //更新座位状态为未选中
            deselectSeat(seat);
            //刷新底部栏
            refreshBottomBar();
        }
    };

}
