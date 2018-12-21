package com.melot.engine;

import android.util.DisplayMetrics;
import android.util.Log;
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
    private KkRTCEngine mEngine = null;
    private String mStreamName = null;
    private RelativeLayout mLayout = null;
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
        KkVideoRendererGui.update(renderer,x,y,w,h,scalingType,mirror);
    }

    public void unSubscribe(){
        if(mEngine != null){
            mEngine.unSubscribe(mStreamName);
        }
        mStreamName = null;
    }

    public void createRenderer(Context context, RelativeLayout layout,int x, int y, int width, int height, RendererCommon.ScalingType scalingType, boolean mirror){
        renderer = KkVideoRendererGui.create(x,y,width,height,scalingType,mirror);
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.scalingType = scalingType;
        this.mirror = mirror;

        Log.d("lzx","lzx Test createRenderer");
        mLayout = layout;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int w = wm.getDefaultDisplay().getWidth();
        int h = wm.getDefaultDisplay().getHeight();

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
        layoutParams1.topMargin = -0+y*h/100;
        layoutParams1.bottomMargin = -60+y*h/100;
        layoutParams1.rightMargin =1050;
        muteRemoteVideocheckbox.setLayoutParams(layoutParams1);
        mLayout.addView(muteRemoteVideocheckbox);

        layoutParams2.leftMargin = 750;
        layoutParams2.topMargin = -0+y*h/100+60;
        layoutParams2.bottomMargin = -60+y*h/100+60;
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

    }

    public void deleteRenderer(){
        KkVideoRendererGui.remove(renderer);
        renderer = null;
    }

    public VideoRenderer.Callbacks getRenderer(){
        return renderer;
    }


    public void destroy(){
        //mLayout.removeAllViews();
        mLayout.removeView(muteRemoteAudiocheckbox);
        mLayout.removeView(muteRemoteVideocheckbox);
        KkVideoRendererGui.remove(renderer);
        renderer = null;
        mEngine = null;
        mStreamName = null;

        //System.gc();
    }
}
