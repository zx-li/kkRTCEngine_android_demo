package com.melot.engine;

import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FIT;

/**
 * Created by ssq on 2018/12/17.
 */

public class KkRenderGuiManager {
    private final String TAG = "KkRenderGuiManager";
    private TextView mStreamNameTextView;
    private CheckBox muteRemoteAudiocheckbox;
    private CheckBox muteRemoteVideocheckbox;
    public VideoRenderer.Callbacks renderer = null;
    private KkGLSurfaceView surfaceView;
    private KkRTCEngine mEngine = null;
    private String mStreamName = null;
    private RelativeLayout mLayout = null;
    private RelativeLayout mSurfacelayout = null;
    private int x = 0;
    private int y = 0;
    private int w = 0;
    private int h = 0;
    private RendererCommon.ScalingType scalingType = SCALE_ASPECT_FIT;
    private boolean mirror = false;
    private int kRemoteShowWidth=320;
    private int kRemoteShowHeight=180;
    private  boolean bSubscribering = false;

    public KkRenderGuiManager(KkRTCEngine engine){
        mEngine = engine;

    }

    public String getStreamName(){
        return mStreamName;
    }

    public void setStreamName(String streamName) {
        mStreamName = streamName;
    }

    public void subscribe(String name){
        mStreamName = name;
        if(mEngine != null && renderer != null){
            mEngine.subscribe(mStreamName,renderer);
        }
    }

    public void unSubscribe(){
        if(mEngine != null){
            mEngine.unSubscribe(mStreamName);
        }
        mStreamName = null;
    }

    public void Test(RelativeLayout surface_layout) {
        surfaceView.setReplaceLayout(true);

        mSurfacelayout.removeView(surfaceView);
        mSurfacelayout = surface_layout;
        RelativeLayout.LayoutParams surfaceView_layoutParams = new RelativeLayout.LayoutParams(400, 400);
        mSurfacelayout.addView(surfaceView, surfaceView_layoutParams);

        surfaceView.setReplaceLayout(false);

    }

    public void createLocalRenderer(Context context,RelativeLayout surface_layout,int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror){

        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.scalingType = scalingType;
        this.mirror = mirror;

        mSurfacelayout = surface_layout;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int W = wm.getDefaultDisplay().getWidth();
        int H = wm.getDefaultDisplay().getHeight();

        surfaceView = new KkGLSurfaceView(context);
        RelativeLayout.LayoutParams surfaceView_layoutParams = new RelativeLayout.LayoutParams(400, 400);
        surfaceView_layoutParams.leftMargin = 0;
        surfaceView_layoutParams.topMargin = -0+y*H/100;
        surfaceView_layoutParams.bottomMargin = -400+y*H/100;
        surfaceView_layoutParams.rightMargin = 400;
        surfaceView.setLayoutParams(surfaceView_layoutParams);
        mSurfacelayout.addView(surfaceView);
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setKeepScreenOn(true);

        VideoCanvas canvas = new VideoCanvas(surfaceView,0,0,100,100,scalingType,mirror);
        if (mEngine != null){
            renderer = mEngine.setupLocalVideo(canvas);
        }

/*
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.scalingType = scalingType;
        this.mirror = mirror;
        mSurfacelayout = surface_layout;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int w = wm.getDefaultDisplay().getWidth();
        int h = wm.getDefaultDisplay().getHeight();

        surfaceView = new GLSurfaceView(context);
        RelativeLayout.LayoutParams surfaceView_layoutParams = new RelativeLayout.LayoutParams(w, h);
        surfaceView.setLayoutParams(surfaceView_layoutParams);
        mSurfacelayout.addView(surfaceView);
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setKeepScreenOn(true);

        VideoCanvas canvas = new VideoCanvas(surfaceView,0,0,100,100,scalingType,mirror);
        if (mEngine != null){
            renderer = mEngine.setupLocalVideo(canvas);
        }
*/

    }

    public void createRenderer(Context context, final String streanName, RelativeLayout layout, RelativeLayout surface_layout, int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror){
/*        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.scalingType = scalingType;
        this.mirror = mirror;*/

        Log.d("lzx","lzx Test createRenderer");
        mLayout = layout;
        mSurfacelayout = surface_layout;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int w = wm.getDefaultDisplay().getWidth();
        int h = wm.getDefaultDisplay().getHeight();

        muteRemoteAudiocheckbox = new CheckBox(context);
        muteRemoteVideocheckbox = new CheckBox(context);
        mStreamNameTextView = new TextView(context);
        muteRemoteAudiocheckbox.setVisibility(VISIBLE);
        muteRemoteVideocheckbox.setVisibility(VISIBLE);
        mStreamNameTextView.setVisibility(VISIBLE);
        muteRemoteAudiocheckbox.bringToFront();
        muteRemoteVideocheckbox.bringToFront();
        mStreamNameTextView.bringToFront();
        muteRemoteVideocheckbox.setText("视频");
        muteRemoteAudiocheckbox.setText("音频");
        mStreamNameTextView.setText(streanName);

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(kRemoteShowWidth, kRemoteShowHeight/3);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(kRemoteShowWidth, kRemoteShowHeight/3);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(kRemoteShowWidth, kRemoteShowHeight/3);
        layoutParams1.leftMargin = w-kRemoteShowWidth-16;
        layoutParams1.topMargin = -0+y*h/100;
        layoutParams1.bottomMargin = layoutParams1.topMargin + kRemoteShowHeight/3;
        layoutParams1.rightMargin =layoutParams1.leftMargin+kRemoteShowWidth/2;
        muteRemoteVideocheckbox.setLayoutParams(layoutParams1);
        mLayout.addView(muteRemoteVideocheckbox);

        layoutParams2.leftMargin = w-kRemoteShowWidth/2-16;
        layoutParams2.topMargin = -0+y*h/100;
        layoutParams2.bottomMargin = layoutParams1.topMargin + kRemoteShowHeight/3;
        layoutParams2.rightMargin =layoutParams1.leftMargin+kRemoteShowWidth;
        muteRemoteAudiocheckbox.setLayoutParams(layoutParams2);
        mLayout.addView(muteRemoteAudiocheckbox);

        layoutParams3.leftMargin = w-kRemoteShowWidth;
        layoutParams3.topMargin = -0+y*h/100 + kRemoteShowHeight - 60;
        layoutParams3.bottomMargin = -0+y*h/100 + kRemoteShowHeight;
        layoutParams3.rightMargin =layoutParams3.leftMargin+kRemoteShowWidth;
        mStreamNameTextView.setLayoutParams(layoutParams3);
        mLayout.addView(mStreamNameTextView);

        muteRemoteAudiocheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mEngine != null && mStreamName != null){
                mEngine.muteRemoteAudioStream(mStreamName,isChecked);
            }
            }
        });
        muteRemoteVideocheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mEngine != null && mStreamName != null){
                mEngine.muteRemoteVideoStream(mStreamName,isChecked);
            }
            }
        });


        surfaceView = new KkGLSurfaceView(context);
        RelativeLayout.LayoutParams surfaceView_layoutParams = new RelativeLayout.LayoutParams(kRemoteShowWidth, kRemoteShowHeight);
        surfaceView_layoutParams.leftMargin = w-kRemoteShowWidth-16;
        surfaceView_layoutParams.topMargin = -0+y*h/100;
        surfaceView_layoutParams.bottomMargin = surfaceView_layoutParams.topMargin + kRemoteShowHeight;
        surfaceView_layoutParams.rightMargin =surfaceView_layoutParams.leftMargin+kRemoteShowWidth;
        surfaceView.setLayoutParams(surfaceView_layoutParams);
        mSurfacelayout.addView(surfaceView);

        surfaceView.setClickable(true);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (bSubscribering == false) {
                subscribe(mStreamName);
                bSubscribering =true;
            }
            }
        });


        surfaceView.setPreserveEGLContextOnPause(true);
        //surfaceView.setZOrderMediaOverlay(true);
        surfaceView.setKeepScreenOn(true);

        VideoCanvas canvas = new VideoCanvas(surfaceView,0,0,100,100,scalingType,mirror);
        if (mEngine != null){
            renderer = mEngine.setupRemoteVideo(canvas);
        }
        //renderer = KkVideoRendererGui.create(canvas);

    }

    public VideoRenderer.Callbacks getRenderer(){
        return renderer;
    }

    public void destroy(){
        Log.d(TAG,"kzx Test KkRenderGuiManager");
        //mLayout.removeAllViews();
        mLayout.removeView(muteRemoteAudiocheckbox);
        mLayout.removeView(muteRemoteVideocheckbox);
        mLayout.removeView(mStreamNameTextView);
        mSurfacelayout.removeView(surfaceView);

        //KkVideoRendererGui.remove(renderer);
        renderer = null;
        mEngine = null;
        mStreamName = null;
        surfaceView = null;

        //System.gc();
    }

    public void destroyLocal(){
        mSurfacelayout.removeView(surfaceView);

        renderer = null;
        mEngine = null;
        mStreamName = null;
        surfaceView = null;
    }
}
