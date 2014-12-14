package no.marius.wold.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

/**
 * Created by Marius on 13/12/2014.
 */
public class ControllerService extends Service
{
    private static final String TAG = "ControllerService";

    private Toast mToast;

    private DeviceListener mListener = new AbstractDeviceListener()
    {
        private Arm mArm = Arm.UNKNOWN;
        private XDirection mXDirection = XDirection.UNKNOWN;

        @Override
        public void onConnect(Myo myo, long timestamp)
        {
            showToast("Connected");
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp)
        {
            showToast("Disconnected");
        }

        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection)
        {
            mArm = arm;
            mXDirection = xDirection;
        }

        @Override
        public void onArmUnsync(Myo myo, long timestamp)
        {
            mArm = Arm.UNKNOWN;
            mXDirection = XDirection.UNKNOWN;
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose)
        {
            showToast("Pose:"+pose.name().toString());
            switch (pose)
            {
                case UNKNOWN:
                    break;
                case REST:
                    break;
                case DOUBLE_TAP:
                    break;
                case FIST:
                    break;
                case WAVE_IN:
                    break;
                case WAVE_OUT:
                    break;
                case FINGERS_SPREAD:
                    break;
            }

            if(pose != Pose.UNKNOWN && pose != Pose.REST)
            {
                myo.unlock(Myo.UnlockType.HOLD);

                myo.notifyUserAction();
            }
            else
            {
                myo.unlock(Myo.UnlockType.TIMED);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent){return null;}

    @Override
    public void onCreate()
    {
        super.onCreate();

        Hub hub = Hub.getInstance();
        if(!hub.init(this, getPackageName()))
        {
            showToast("Couldn't initalise Hub");
            stopSelf();
            return;
        }

        hub.addListener(mListener);
        findDevice();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Hub.getInstance().removeListener(mListener);

        Hub.getInstance().shutdown();
    }

    private void findDevice()
    {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showToast(String text)
    {
        Log.v(TAG,text);
        if (mToast == null)
        {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else
        {
            mToast.setText(text);
        }
        mToast.show();
    }
}
