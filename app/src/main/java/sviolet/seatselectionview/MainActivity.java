package sviolet.seatselectionview;

import android.os.Bundle;
import android.os.Message;
import android.view.View;

import sviolet.turquoise.enhance.app.TAppCompatActivity;
import sviolet.turquoise.enhance.app.annotation.inject.ResourceId;
import sviolet.turquoise.enhance.common.WeakHandler;

@ResourceId(R.layout.activity_main)
public class MainActivity extends TAppCompatActivity {

    @ResourceId(R.id.view)
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        myHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void refresh(){
//        view.postInvalidate();
//        myHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends WeakHandler<MainActivity> {

        public MyHandler(MainActivity host) {
            super(host);
        }

        @Override
        protected void handleMessageWithHost(Message message, MainActivity mainActivity) {
            mainActivity.refresh();
        }
    }

}
