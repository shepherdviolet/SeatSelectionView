package sviolet.seatselectionview.parser;

import java.util.HashMap;
import java.util.Map;

import sviolet.seatselectionview.view.Seat;
import sviolet.seatselectionview.view.SeatState;
import sviolet.seatselectionview.view.SeatTable;
import sviolet.seatselectionview.view.SeatType;

/**
 * 简易座位数据解析器
 *
 * Created by S.Violet on 2016/10/24.
 */

public class SimpleSeatDataParser implements SeatDataParser {

    private static final String NULL = "N";
    private static final Map<String, SeatType> seatTypeMap = new HashMap<>();
    private static final Map<String, SeatState> seatStateMap = new HashMap<>();

    static{
        //座位类型
        seatTypeMap.put("N", null);
        seatTypeMap.put("S", SeatType.SINGLE);
        seatTypeMap.put("C", SeatType.COUPLE);
        seatTypeMap.put("H", SeatType.MULTI_SEAT_PLACEHOLDER);

        //座位状态
        seatStateMap.put("N", SeatState.NULL);
        seatStateMap.put("A", SeatState.AVAILABLE);
        seatStateMap.put("U", SeatState.UNAVAILABLE);
        seatStateMap.put("S", SeatState.SELECTED);
    }

    private SeatTable seatTable;

    public SimpleSeatDataParser(int rowNum, int columnNum, float seatWidth, float seatHeight, int padding) throws SeatDataParseException {
        seatTable = new SeatTable(rowNum, columnNum, seatWidth, seatHeight, padding);
    }

    /**
     *
     *
     *
     * @param row 显示行号
     * @param rowId 影院实际行号
     * @param columnIds 座位号(影院实际)
     * @param columnTypes 座位类型
     * @param columnStates 不可选的座位
     */
    public void addRow(int row, String rowId, String columnIds, String columnTypes, String columnStates) throws SeatDataParseException {
        if (row >= seatTable.getRowNum()){
            throw new SeatDataParseException("row out of bound, the row no is " + row + ", but the max row num is " + seatTable.getRowNum());
        }
        if (columnIds == null){
            throw new SeatDataParseException("input columnIds is null");
        }
        if (columnTypes == null){
            throw new SeatDataParseException("input columnTypes is null");
        }
        if (columnStates == null){
            throw new SeatDataParseException("input columnStates is null");
        }

        String[] columnIdArray = columnIds.split("\\|");
        String[] columnTypeArray = columnTypes.split("\\|");
        String[] columnStateArray = columnStates.split("\\|");

        if (!(columnIdArray.length == columnTypeArray.length && columnIdArray.length == columnStateArray.length)){
            throw new SeatDataParseException("length of columnIds/columnTypes/columnStates is not match, columnIds length:" + columnIdArray.length +
                    " columnTypes length:" + columnTypeArray.length +
                    " columnStates length:" + columnStateArray.length);
        }

        Seat hostSeat = null;
        for (int column = 0 ; column < columnIdArray.length ; column++){
            if (column >= seatTable.getColumnNum()){
                throw new SeatDataParseException("column out of bound, the column no is " + column + ", but the max column num is " + seatTable.getColumnNum());
            }
            //影厅座位号
            String columnId = columnIdArray[column];
            //座位号为N的视为无座位
            if (NULL.equals(columnId)){
                continue;
            }
            //座位类型
            SeatType type = seatTypeMap.get(columnTypeArray[column]);
            //座位类型为空, 视为无座位
            if (type == null){
                continue;
            }
            //座位状态
            SeatState state = seatStateMap.get(columnStateArray[column]);
            if (state == null){
                state = SeatState.NULL;
            }

            switch (type){
                case SINGLE:
                    //单座
                    seatTable.setSeat(row, column, new Seat(SeatType.SINGLE, state, rowId, columnId));
                    break;
                case COUPLE:
                    //双座
                    hostSeat = new Seat(SeatType.COUPLE, state, rowId, columnId);
                    seatTable.setSeat(row, column, hostSeat);
                    break;
                case MULTI_SEAT_PLACEHOLDER:
                    //双座(占位)
                    if (hostSeat == null){
                        throw new SeatDataParseException("we found a placeHolder seat without host seat");
                    }
                    seatTable.setSeat(row, column, hostSeat.createMultiSeatPlaceholder(rowId, columnId));
                    break;
                default:
                    break;
            }
        }

        //设置影院实际行号
        seatTable.setRowId(row, rowId);
    }

    @Override
    public SeatTable parse() throws SeatDataParseException {
        return seatTable;
    }

}
