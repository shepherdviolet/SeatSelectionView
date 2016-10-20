package sviolet.seatselectionview.view;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

/**
 * 简易图片池
 *
 * Created by S.Violet on 2016/10/20.
 */

public class SeatImagePoolImpl implements SeatImagePool {

    private Map<SeatType, Map<SeatState, Bitmap>> images = new HashMap<>();
    private Map<SeatType, Map<SeatState, Rect>> rects = new HashMap<>();

    @Override
    public Bitmap getImage(SeatType type, SeatState state) {
        Map<SeatState, Bitmap> map = images.get(type);
        if (map == null){
            return null;
        }
        return map.get(state);
    }

    @Override
    public Rect getImageRect(SeatType type, SeatState state) {
        Map<SeatState, Rect> map = rects.get(type);
        if (map == null){
            return null;
        }
        return map.get(state);
    }

    public void setImage(SeatType type, SeatState state, Bitmap bitmap){
        if (type == null || state == null || bitmap == null){
            throw new RuntimeException("one of params is null");
        }

        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Map<SeatState, Bitmap> imageMap = images.get(type);
        if (imageMap == null){
            imageMap = new HashMap<>();
            images.put(type, imageMap);
        }
        imageMap.put(state, bitmap);

        Map<SeatState, Rect> rectMap = rects.get(type);
        if (rectMap == null){
            rectMap = new HashMap<>();
            rects.put(type, rectMap);
        }
        rectMap.put(state, rect);

    }

    @Override
    public void destroy() {
        for (Map<SeatState, Bitmap> map : images.values()){
            if (map != null){
                for (Bitmap bitmap : map.values()){
                    if (bitmap != null && !bitmap.isRecycled()){
                        bitmap.recycle();
                    }
                }
            }
        }
    }

}
