package com.melot.engine;

import com.melot.engine.util.SystemUiHider;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.webrtc.KkVideoRendererGui;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class JanusActivity extends Activity  implements View.OnClickListener,
        com.melot.engine.Timer.OnTimeListener, KkRTCEngine.KkStatObserver {
    private static final boolean AUTO_HIDE = true;

    AndroidUtils systemHelper = null;

    private GLSurfaceView vsv;
    private GLSurfaceView remotevsv;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    private VideoRoomTest videoRoomTest;
    private VideoRenderer.Callbacks remoteRenders[] = new VideoRenderer.Callbacks[3];

    private Button sdkInitBtn;
    private Button startorstopPreviewBtn;
    private Button joinorleaveRoomBtn;
    private Button switchCameraBtn;
    private Button startorstopPushBtn;
    private Button subscribeorunsubscribeBtn;

    private CheckBox muteLocalAudiocheckbox;
    private CheckBox muteLocalVideocheckbox;
    private CheckBox muteRemoteAudiocheckbox;
    private CheckBox muteRemoteVideocheckbox;

    private TextView roomId_Edit;
    private TextView appId_Edit;

    private boolean issdkInit = false;
    private boolean isStartPreview = false;
    private boolean isjoinRoom = false;
    private boolean isstartPush = false;
    private boolean issubscribe = false;

    private boolean ismuteLocalAudio = false;
    private boolean ismuteLocalVideo = false;
    private boolean ismuteRemoteAudio = false;
    private boolean ismuteRemoteVideo = false;

    private RelativeLayout testlayout,surface_layout,localsurface_layout;
    private Timer freshTimer = null;
    private TextView cpuUsedView;
    private TextView bitrateView;
    private TextView fpsView;
    private TextView resolutionView;

    //private KkRenderersLayout layout = null;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private class MyInit implements Runnable {

        public void run() {
            //init();
        }

/*        private void init() {
            try {
                String appid = null;
                try{
                    appid = appId_Edit.getText().toString();
                }catch(NumberFormatException e ){
                    e.printStackTrace();
                }

                videoRoomTest = new VideoRoomTest(JanusActivity.this,appid,testlayout);
                videoRoomTest.initializeMediaContext(JanusActivity.this, true, *//*con*//*null);
                //videoRoomTest.Start();


            } catch (Exception ex) {
                Log.e("lzx computician.janusclient", ex.getMessage());
                System.out.println(ex);
            }
        }*/
    }

    private void init() {
        try {
            String appid = null;
            try{
                appid = appId_Edit.getText().toString();
            }catch(NumberFormatException e ){
                e.printStackTrace();
            }

            videoRoomTest = new VideoRoomTest(JanusActivity.this,testlayout,surface_layout,localsurface_layout);
            videoRoomTest.initializeMediaContext(JanusActivity.this,appid, true, /*con*/null);
            //videoRoomTest.Start();


        } catch (Exception ex) {
            Log.e("lzx computician.janusclient", ex.getMessage());
            System.out.println(ex);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_janus);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        testlayout = (RelativeLayout)findViewById(R.id.kkrtc_checkbox);
        localsurface_layout = (RelativeLayout)findViewById(R.id.kkrtc_container);
        surface_layout = (RelativeLayout)findViewById(R.id.kkrtc_container2);

/*
        vsv = (GLSurfaceView) findViewById(R.id.glview);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        //KkVideoRendererGui.setView(vsv, new MyInit()*/
/*,this*//*
);
        KkVideoRendererGui.init();
        localRender = KkVideoRendererGui.create(new VideoCanvas(vsv,0,0,100,100,RendererCommon.ScalingType.SCALE_ASPECT_FIT, true));//videoRoomTest.setupLocalVideo(new VideoCanvas(vsv,0,0,100,100,RendererCommon.ScalingType.SCALE_ASPECT_FIT, true));
*/

        sdkInitBtn = (Button) findViewById(R.id.SDKInit);
        sdkInitBtn.setOnClickListener(this);

        startorstopPreviewBtn = (Button) findViewById(R.id.startPrevieworstopPreview);
        startorstopPreviewBtn.setOnClickListener(this);

        joinorleaveRoomBtn = (Button) findViewById(R.id.joinRoomorleaveRoom);
        joinorleaveRoomBtn.setOnClickListener(this);

        switchCameraBtn = (Button) findViewById(R.id.switchCamera);
        switchCameraBtn.setOnClickListener(this);

        startorstopPushBtn = (Button) findViewById(R.id.startorstoppush);
        startorstopPushBtn.setOnClickListener(this);

        subscribeorunsubscribeBtn = (Button) findViewById(R.id.subscribeorunsubscribe);
        subscribeorunsubscribeBtn.setOnClickListener(this);

        muteLocalAudiocheckbox = (CheckBox) findViewById(R.id.muteLocalAudio);
        muteLocalVideocheckbox = (CheckBox) findViewById(R.id.muteLocalVideo);
        //muteRemoteAudiocheckbox = (CheckBox) findViewById(R.id.muteRemoteAudio);
        //muteRemoteVideocheckbox = (CheckBox) findViewById(R.id.muteRemoteVideo);

        muteLocalAudiocheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (issdkInit){
                    videoRoomTest.muteLocalAudioStream(isChecked);
                }
            }
        });
        muteLocalVideocheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (issdkInit){
                    videoRoomTest.muteLocalVideoStream(isChecked);
                }
            }
        });
/*
        muteRemoteAudiocheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                videoRoomTest.muteRemoteAudioStream(null,isChecked);
            }
        });
        muteRemoteVideocheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                videoRoomTest.muteRemoteVideoStream(null,isChecked);
            }
        });
*/


        roomId_Edit = (TextView) findViewById(R.id.RoomNo);
        roomId_Edit.setText("32");

        appId_Edit = (TextView) findViewById(R.id.appid);
        appId_Edit.setText("1");

        systemHelper = new AndroidUtils();
        //systemHelper.loadImageBuf(this);
        freshTimer = new Timer(1000, true, this);
        //freshTimer.resume();

        cpuUsedView = (TextView) findViewById(R.id.cpuUsed);
        bitrateView = (TextView) findViewById(R.id.bitrate);
        fpsView = (TextView) findViewById(R.id.fps);
        resolutionView = (TextView) findViewById(R.id.resolution);
    }

    public void onTime(Timer timer){
        updateCpu();
        if (videoRoomTest != null) {
            videoRoomTest.getRtcStats(this);
        }
    }

    public void onRtcStat(final Map<String, String> reports) {
        if (videoRoomTest != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (reports.containsKey("Fps") == true) {
                        fpsView.setText(reports.get("Fps") + " fps");
                    }
                    if ((reports.containsKey("ActualBitrate") == true) &&
                        (reports.containsKey("TargetBitrate") == true)){
                        String bitrate = Integer.parseInt(reports.get("ActualBitrate"))/1000 + " / " +
                                Integer.parseInt(reports.get("ActualBitrate"))/1000 + " k/s";
                        bitrateView.setText(bitrate);
                    }

                    if ((reports.containsKey("EncoderWidth") == true) &&
                            (reports.containsKey("EncoderHeight") == true)){
                        resolutionView.setText(reports.get("EncoderWidth") + "x" + reports.get("EncoderHeight"));
                    }
                }
            });

        }
    }

    private void updateCpu() {
        long cpu = systemHelper.getProcessCpuUsed();
        cpuUsedView.setText(Long.toString(cpu) + " %");
    }

    @Override
    public void onPause() {
        System.out.println("MainActivity: onPause");
        super.onPause();
        freshTimer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        freshTimer.resume();
    }

    public void onClick(View arg0) {
        if (arg0.getId() == R.id.SDKInit) {
            if (!issdkInit &&!isStartPreview && !isjoinRoom && !isstartPush && !issubscribe){
                init();
                sdkInitBtn.setText("销毁引擎");
                issdkInit=true;
            }else{
                if(issdkInit && !isStartPreview && !isjoinRoom && !isstartPush && !issubscribe){
                    videoRoomTest.unit();
                    sdkInitBtn.setText("初始化引擎");
                    issdkInit=false;
                }
            }
        }

        if (!issdkInit){
            Log.d("lzx","lzx Test onClick");
            return;
        }

        if (arg0.getId() == R.id.startPrevieworstopPreview) {
            if (!isStartPreview){
                videoRoomTest.startPreView(/*localRender*/);
                startorstopPreviewBtn.setText("停止预览");
                isStartPreview=true;
            }else{
                if(!isstartPush){
                    videoRoomTest.stopPreview();
                    startorstopPreviewBtn.setText("开始预览");
                    isStartPreview=false;
                }
            }
        }
        if (arg0.getId() == R.id.joinRoomorleaveRoom) {
            if (!isjoinRoom){
                try{
                    int roomid = Integer.valueOf(roomId_Edit.getText().toString()).intValue();
                    videoRoomTest.joinRoom(roomid);
                }catch(NumberFormatException e ){
                        e.printStackTrace();
                }

                videoRoomTest.setBeautyPara(100, PreprocessFlag.SkinSoften);
                videoRoomTest.setBeautyPara(100, PreprocessFlag.SkinRuddy);
                videoRoomTest.setBeautyPara(100, PreprocessFlag.SkinBright);
                videoRoomTest.setBeautyPara(100, PreprocessFlag.FaceSlender);
                videoRoomTest.setBeautyPara(100, PreprocessFlag.EyeEnlargement);
                //videoRoomTest.setStickPic("/sdcard/rabbit");

                joinorleaveRoomBtn.setText("离开房间");
                isjoinRoom = true;
            }else{
                muteLocalAudiocheckbox.setChecked(false);
                muteLocalVideocheckbox.setChecked(false);
/*
                muteRemoteAudiocheckbox.setChecked(false);
                muteRemoteVideocheckbox.setChecked(false);
*/

                videoRoomTest.leaveRoom();
                videoRoomTest.setBeautyPara(1, PreprocessFlag.SkinSoften);
                videoRoomTest.setBeautyPara(1, PreprocessFlag.SkinRuddy);
                videoRoomTest.setBeautyPara(1, PreprocessFlag.SkinBright);
                videoRoomTest.setBeautyPara(1, PreprocessFlag.FaceSlender);
                videoRoomTest.setBeautyPara(1, PreprocessFlag.EyeEnlargement);
                videoRoomTest.setStickPic("");

                joinorleaveRoomBtn.setText("加入房间");
                startorstopPushBtn.setText("开始推流");
                subscribeorunsubscribeBtn.setText("开始拉流");

                isjoinRoom = false;
                isstartPush = false;
                issubscribe = false;
            }
        }

        if (arg0.getId() == R.id.startorstoppush) {
            if(!isjoinRoom || !isStartPreview)
                return;
            if (!isstartPush){
                videoRoomTest.startPush("");

                startorstopPushBtn.setText("停止推流");
                isstartPush = true;
            }else{
                videoRoomTest.stopPush();
                startorstopPushBtn.setText("开始推流");
                isstartPush = false;
            }

        }
        if (arg0.getId() == R.id.switchCamera) {
            videoRoomTest.switchCamera();
        }
        if (arg0.getId() == R.id.subscribeorunsubscribe) {
            if(!isjoinRoom)
                return;
            if(!issubscribe){
                videoRoomTest.startPreviewRemoteVideo();

                subscribeorunsubscribeBtn.setText("停止拉流");
                issubscribe = true;
            }else{
                videoRoomTest.stopPreviewRemoteVideo("");

                subscribeorunsubscribeBtn.setText("开始拉流");
                issubscribe = false;
            }
        }
    }
}
