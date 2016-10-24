package sviolet.seatselectionview.parser;

import sviolet.seatselectionview.view.SeatTable;

/**
 * Created by S.Violet on 2016/10/24.
 */

public interface SeatDataParser {

    SeatTable parse() throws SeatDataParseException;

}
