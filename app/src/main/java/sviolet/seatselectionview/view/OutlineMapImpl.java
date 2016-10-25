package sviolet.seatselectionview.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;
import sviolet.turquoise.utilx.tlogger.TLogger;

/**
 * 概要图, 背景图(背景颜色+座位)采用缓存方式, 绘制到Bitmap中, 当座位状态不变时, 始终绘制Bitmap到画布上,
 * 优化性能, 当座位状态变化时, 重新绘制背景图到Bitmap上
 *
 * Created by S.Violet on 2016/10/18.
 */
public class OutlineMapImpl implements OutlineMap {

    private TLogger logger = TLogger.get(this, SeatSelectionView.class.getSimpleName());

    private float outlineWidth;
    private int backgroundColor;
    private int availableColor;
    private int unavailableColor;
    private int selectedColor;
    private int areaColor;
    private float areaStrokeWidth;

    private boolean visible = true;//当前概要图是否可见

    private Paint paint;//画笔
    private Bitmap cache;//背景图缓存

    protected SimpleRectangleOutput.Point backgroundCoordinate = new SimpleRectangleOutput.Point();//背景图左上点在显示坐标系中的坐标
    private Rect canvasClipRect = new Rect();//整体画布矩形
    private Rect backgroundRect = new Rect();//背景图矩形
    private Rect seatRect = new Rect();//座位矩形
    private Rect cacheRect = new Rect();//cache缓存的矩形
    private RectF outputSrcRect = new RectF();//output输出的源矩形

    public OutlineMapImpl(float outlineWidth, int backgroundColor, int availableColor, int unavailableColor, int selectedColor, int areaColor, float areaStrokeWidth) {
        this.outlineWidth = outlineWidth;
        this.backgroundColor = backgroundColor;
        this.availableColor = availableColor;
        this.unavailableColor = unavailableColor;
        this.selectedColor = selectedColor;
        this.areaColor = areaColor;
        this.areaStrokeWidth = areaStrokeWidth;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        paint.setStrokeWidth(areaStrokeWidth);
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void outlineChanged() {
        //当座位状态变化时, 会调用该方法通知概要图更新, 需要重新绘制背景图
        Bitmap cache = this.cache;
        this.cache = null;
        if (cache != null && !cache.isRecycled()){
            cache.recycle();
        }
    }

    @Override
    public void draw(Canvas canvas, SimpleRectangleOutput output, SeatTable seatTable) {
        if (!visible){
            return;
        }

        //如果缓存为空的话, 绘制背景到缓存中
        Bitmap cache = this.cache;
        if (cache == null || cache.isRecycled()) {
            try {
                cache = drawBackgroundToCache(canvas, output, seatTable);
            } catch (Exception e) {
                logger.w("error while drawBackgroundToCache", e);
                return;
            }
            this.cache = cache;
        }

        //尝试绘制背景后, 缓存还是为空的话, 放弃绘制概要图
        if (cache == null || cache.isRecycled()){
            logger.w("skip drawing outline, because the cache is null or recycled");
            return;
        }

        //绘制背景图到画布中
        try {
            canvas.drawBitmap(cache, cacheRect, backgroundRect, null);
        } catch (Exception e) {
            logger.w("error while draw background and seat", e);
            return;
        }

        //从output获得实际矩形
        output.getSrcDstRectF(outputSrcRect, null);

        outputSrcRect.left = (float) backgroundCoordinate.getX() + outputSrcRect.left * (cacheRect.width() / seatTable.getMatrixWidth()) + areaStrokeWidth / 2;
        outputSrcRect.right = (float) backgroundCoordinate.getX() + outputSrcRect.right * (cacheRect.width() / seatTable.getMatrixWidth()) - areaStrokeWidth / 2;
        outputSrcRect.top = (float) backgroundCoordinate.getY() + outputSrcRect.top * (cacheRect.height() / seatTable.getMatrixHeight()) + areaStrokeWidth / 2;
        outputSrcRect.bottom = (float) backgroundCoordinate.getY() + outputSrcRect.bottom * (cacheRect.height() / seatTable.getMatrixHeight()) - areaStrokeWidth / 2;

        //绘制显示区域
        paint.setColor(areaColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(outputSrcRect, paint);
    }

    protected Bitmap drawBackgroundToCache(Canvas canvas, SimpleRectangleOutput output, SeatTable seatTable){

        //缓存
        Bitmap cache;
        Canvas cacheCanvas;

        //获得原始画布尺寸
        canvas.getClipBounds(canvasClipRect);

        //显示的行列数
        int displayColumnNum = seatTable.getColumnNum() + seatTable.getPadding() * 2;
        int displayRowNum = seatTable.getRowNum() + seatTable.getPadding() * 2;

        //计算指定的概要图宽度分配到每个座位单元的宽度
        float unitWidth = outlineWidth / (float)displayColumnNum;

        //计算座位宽度
        int seatWidth = (int) Math.floor(unitWidth * 0.6f);//80%分配给座位
        if (seatWidth < 2){
            seatWidth = 2;
        }

        //计算座位间距
        int seatSpacing = (int) Math.floor(unitWidth * 0.4f);//20%分配给间隔
        if (seatSpacing < 1){
            seatSpacing = 1;
        }

        //重新计算矩形宽高
        int backgroundWidth = displayColumnNum * seatWidth + (displayColumnNum + 1) * seatSpacing;
        int backgroundHeight = displayRowNum * seatWidth + (displayRowNum + 1) * seatSpacing;

        //创建缓存bitmap
        cache = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas(cache);

        //绘制背景颜色
        cacheCanvas.drawColor(backgroundColor);

        //画笔设为填充模式
        paint.setStyle(Paint.Style.FILL);

        //绘制座位
        for (int r = 0 ; r < displayRowNum ; r++){
            for (int c = 0 ; c < displayColumnNum ; c++){
                Seat seat = seatTable.getSeat(r - seatTable.getPadding(), c - seatTable.getPadding());
                if (seat == null || seat.getType() == SeatType.MULTI_SEAT_PLACEHOLDER || seat.getState() == SeatState.NULL){
                    continue;
                }
                seatRect.left = (c + 1) * seatSpacing + c * seatWidth;
                seatRect.top = (r + 1) * seatSpacing + r * seatWidth;
                seatRect.right = seatRect.left + seatWidth * seat.getType().getColumn() + seatSpacing * (seat.getType().getColumn() - 1);
                seatRect.bottom = seatRect.top + seatWidth * seat.getType().getRow() + seatSpacing * (seat.getType().getRow() - 1);

                switch (seat.getState()){
                    case AVAILABLE:
                        paint.setColor(availableColor);
                        break;
                    case UNAVAILABLE:
                        paint.setColor(unavailableColor);
                        break;
                    case SELECTED:
                        paint.setColor(selectedColor);
                        break;
                    default:
                        break;
                }
                cacheCanvas.drawRect(seatRect, paint);
            }
        }

        //计算矩形左上角坐标
        calculateBackgroundCoordinate(canvasClipRect, backgroundWidth, backgroundHeight);

        //实际绘制到View上去的矩形
        backgroundRect.left = (int) backgroundCoordinate.getX();
        backgroundRect.top = (int) backgroundCoordinate.getY();
        backgroundRect.right = backgroundRect.left + backgroundWidth;
        backgroundRect.bottom = backgroundRect.top + backgroundHeight;

        //缓存的矩形
        cacheRect.left = 0;
        cacheRect.top = 0;
        cacheRect.right = cache.getWidth();
        cacheRect.bottom = cache.getHeight();

        return cache;
    }

    protected void calculateBackgroundCoordinate(Rect canvasClipRect, float backgroundWidth, float backgroundHeight){
        backgroundCoordinate.setX(canvasClipRect.right - backgroundWidth);
        backgroundCoordinate.setY(canvasClipRect.top);
    }

}
