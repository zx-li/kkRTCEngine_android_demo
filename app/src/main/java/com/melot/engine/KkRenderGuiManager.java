package com.melot.engine;

import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Printer;
import android.view.WindowManager;
import android.widget.CheckBox;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import org.webrtc.KkVideoRendererGui;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FIT;

/**
 * Created by ssq on 2018/12/17.
 */

public class KkRenderGuiManager {
    private CheckBox muteRemoteAudiocheckbox;
    private CheckBox muteRemoteVideocheckbox;
    public VideoRenderer.Callbacks renderer = null;
    private GLSurfaceView surfaceView;
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

    public KkRenderGuiManager(KkRTCEngine engine){
        mEngine = engine;

    }

    public String getStreamName(){
        return mStreamName;
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

        surfaceView = new GLSurfaceView(context);
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
    }

    public void createRenderer(Context context, RelativeLayout layout,RelativeLayout surface_layout,int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror){
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.scalingType = scalingType;
        this.mirror = mirror;

        Log.d("lzx","lzx Test KkRenderGuiManager createRenderer1");
        mLayout = layout;
        mSurfacelayout = surface_layout;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int W = wm.getDefaultDisplay().getWidth();
        int H = wm.getDefaultDisplay().getHeight();

        muteRemoteAudiocheckbox = new CheckBox(context);
        muteRemoteVideocheckbox = new CheckBox(context);
        muteRemoteAudiocheckbox.setVisibility(VISIBLE);
        muteRemoteVideocheckbox.setVisibility(VISIBLE);
        muteRemoteAudiocheckbox.bringToFront();
        muteRemoteVideocheckbox.bringToFront();
        muteRemoteVideocheckbox.setText("视频");
        muteRemoteAudiocheckbox.setText("音频");

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(300, 60);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(300, 60);
        layoutParams1.leftMargin = 750;
        layoutParams1.topMargin = -0+y*H/100;
        layoutParams1.bottomMargin = -60+y*H/100;
        layoutParams1.rightMargin =1050;
        muteRemoteVideocheckbox.setLayoutParams(layoutParams1);
        mLayout.addView(muteRemoteVideocheckbox);

        layoutParams2.leftMargin = 750;
        layoutParams2.topMargin = -0+y*H/100+60;
        layoutParams2.bottomMargin = -60+y*H/100+60;
        layoutParams2.rightMargin =1050;
        muteRemoteAudiocheckbox.setLayoutParams(layoutParams2);
        mLayout.addView(muteRemoteAudiocheckbox);

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

        surfaceView = new GLSurfaceView(context);
        RelativeLayout.LayoutParams surfaceView_layoutParams = new RelativeLayout.LayoutParams(500, 400);
        surfaceView_layoutParams.leftMargin = 600;
        surfaceView_layoutParams.topMargin = -0+y*H/100;
        surfaceView_layoutParams.bottomMargin = -400+y*H/100;
        surfaceView_layoutParams.rightMargin =1100;
        surfaceView.setLayoutParams(surfaceView_layoutParams);
        mSurfacelayout.addView(surfaceView);
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setKeepScreenOn(true);

        VideoCanvas canvas = new VideoCanvas(surfaceView,0,0,100,100,scalingType,mirror);
        if (mEngine != null){
            renderer = mEngine.setupRemoteVideo(canvas);
        }

        Log.d("lzx","lzx Test KkRenderGuiManager createRenderer2");
        //renderer = KkVideoRendererGui.create(canvas);

    }

    public void deleteRenderer(){
        KkVideoRendererGui.remove(renderer);
        renderer = null;
    }

    public VideoRenderer.Callbacks getRenderer(){
        return renderer;
    }


    public void destroy(){
        Log.d("lzx","lzx Test KkRenderGuiManager destroy1");
        //mLayout.removeAllViews();
        mLayout.removeView(muteRemoteAudiocheckbox);
        mLayout.removeView(muteRemoteVideocheckbox);
        mSurfacelayout.removeView(surfaceView);

        KkVideoRendererGui.remove(renderer);
        renderer = null;
        mEngine = null;
        mStreamName = null;
        surfaceView = null;
        Log.d("lzx","lzx Test KkRenderGuiManager destroy2");
        //System.gc();
    }

    public void destroyLocal(){
        mSurfacelayout.removeView(surfaceView);

        KkVideoRendererGui.remove(renderer);
        renderer = null;
        mEngine = null;
        mStreamName = null;
        surfaceView = null;
    }
}
