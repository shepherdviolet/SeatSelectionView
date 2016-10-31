package sviolet.seatselectionview.parser;

/**
 * 座位数据解析异常
 *
 * Created by S.Violet on 2016/10/24.
 */

public class SeatDataParseException extends Exception {

    public SeatDataParseException(String message) {
        super(message);
    }

    public SeatDataParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
