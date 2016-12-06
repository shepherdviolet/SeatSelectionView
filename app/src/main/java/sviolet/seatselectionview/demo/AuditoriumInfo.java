package sviolet.seatselectionview.demo;

/**
 * Created by S.Violet on 2016/12/6.
 */

public class AuditoriumInfo {

    private String cinemaName;//影院名
    private String session;//场次
    private String auditoriumName;//影厅名
    private int maxSeatNum;

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getAuditoriumName() {
        return auditoriumName;
    }

    public void setAuditoriumName(String auditoriumName) {
        this.auditoriumName = auditoriumName;
    }

    public int getMaxSeatNum() {
        return maxSeatNum;
    }

    public void setMaxSeatNum(int maxSeatNum) {
        this.maxSeatNum = maxSeatNum;
    }
}
