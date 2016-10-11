package sviolet.seatselectionview.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import sviolet.turquoise.uix.viewgesturectrl.output.SimpleRectangleOutput;

/**
 * <p>多人座位以左上角的位子为作为实体, 绘图时, 仅绘制实体座位, 并根据实际作为占的长宽绘制, 占位类型的座位(MULTI_SEAT_PLACEHOLDER)
 * 不进行绘制. 点击时, 若点击到占位类型的座位, 则视为点击到实体座位, 占位类型的座位会持有实体座位(host).</p>
 *
 * Created by S.Violet on 2016/10/8.
 */

public class SeatTable {

    private Seat[][] seats;
    private int rowNum;
    private int columnNum;
    private float seatWidth;//(单座位)宽度
    private float seatHeight;//(单座位)高度
    private int padding;

    public SeatTable(int row, int column, float seatWidth, float seatHeight, int padding){
        if (row < 0){
            throw new RuntimeException("row must >= 0");
        }
        if (column < 0){
            throw new RuntimeException("column must >= 0");
        }
        if (seatWidth <= 0){
            throw new RuntimeException("seat width must > 0");
        }
        if (seatHeight <= 0){
            throw new RuntimeException("seat height must > 0");
        }
        if (padding < 0){
            throw new RuntimeException("padding must >= 0");
        }
        this.seats = new Seat[row][column];
        this.rowNum = row;
        this.columnNum = column;
        this.seatWidth = seatWidth;
        this.seatHeight = seatHeight;
        this.padding = padding;
    }

    public void setSeat(int row, int column, Seat seat){
        if (row < 0 || column < 0){
            return;
        }
        if (row >= rowNum){
            return;
        }
        if (column >= columnNum){
            return;
        }
        seats[row][column] = seat;
    }

    public Seat getSeat(int row, int column){
        if (row < 0 || column < 0){
            return null;
        }
        if (row >= rowNum){
            return null;
        }
        if (column >= columnNum){
            return null;
        }
        return seats[row][column];
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getColumnNum() {
        return columnNum;
    }

    public float getMatrixWidth(){
        return (getColumnNum() + 2 * padding) * seatWidth;
    }

    public float getMatrixHeight(){
        return (getRowNum() + 2 * padding) * seatHeight;
    }

    /****************************************************************
     * draw
     */

    private Rect srcRect = new Rect();
    private Rect dstRect = new Rect();
    private Rect drawDstRect = new Rect();

    private SimpleRectangleOutput.Point leftTopPoint = new SimpleRectangleOutput.Point();
    private SimpleRectangleOutput.Point rightBottomPoint = new SimpleRectangleOutput.Point();

    public void draw(Canvas canvas, SimpleRectangleOutput output, SeatImagePool imagePool){
        //从手势控制器的矩形输出中, 获得当前的源矩阵和目标矩阵
        //源矩阵表示座位表中, 该显示到界面上的部分, 坐标系为座位表的坐标系
        //目标矩阵表示界面上绘制图形的部分, 坐标系为显示坐标系
        output.getSrcDstRect(srcRect, dstRect);

        canvas.save();
        //画布根据目标矩形裁剪,保证不会绘制出界
        canvas.clipRect(dstRect);

        int minRow = (int) Math.floor(srcRect.top / seatHeight);
        minRow = minRow > padding ? minRow : padding;

        int maxRow = (int) Math.ceil(srcRect.bottom / seatHeight);
        maxRow = maxRow < rowNum + padding ? maxRow : rowNum + padding;

        int minColumn = (int) Math.floor(srcRect.left / seatWidth);
        minColumn = minColumn > padding ? minColumn : padding;

        int maxColumn = (int) Math.ceil(srcRect.right / seatWidth);
        maxColumn = maxColumn < columnNum + padding ? maxColumn : columnNum + padding;

        for (int r = minRow ; r < maxRow ; r++){
            for (int c = minColumn ; c < maxColumn ; c++){
                Seat seat = getSeat(r - padding, c - padding);
                if (seat == null){
                    continue;
                }
                if (seat.getType() == SeatType.MULTI_SEAT_PLACEHOLDER){
                    continue;
                }
                Bitmap bitmap = imagePool.getImage(seat.getType(), seat.getState());
                if (bitmap == null || bitmap.isRecycled()){
                    continue;
                }

                float x = c * seatWidth;
                float y = r * seatHeight;
                output.mappingActualPointToDisplay(x, y, leftTopPoint);
                output.mappingActualPointToDisplay(x + seat.getType().getColumn() * seatWidth, y + seat.getType().getRow() * seatHeight, rightBottomPoint);

                drawDstRect.left = (int) leftTopPoint.getX();
                drawDstRect.top = (int) leftTopPoint.getY();
                drawDstRect.right = (int) rightBottomPoint.getX();
                drawDstRect.bottom = (int) rightBottomPoint.getY();
                canvas.drawBitmap(bitmap, imagePool.getImageRect(seat.getType(), seat.getState()), drawDstRect, null);
            }
        }

        canvas.restore();

    }

}
