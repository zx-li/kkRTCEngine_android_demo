package com.melot.engine;

import android.view.WindowManager;

import android.content.Context;
import android.widget.RelativeLayout;

import org.webrtc.KkVideoRendererGui;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;
import static org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FIT;

/**
 * Created by ssq on 2018/12/17.
 */

public class KkLocalRenderGuiManager {
    //public VideoRenderer.Callbacks renderer = null;
    public SurfaceViewRenderer surfaceViewRenderer = null;
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

    public KkLocalRenderGuiManager(KkRTCEngine engine){
        mEngine = engine;

    }

    public String getStreamName(){
        return mStreamName;
    }

    public void subscribe(String name){
        mStreamName = name;
        if(mEngine != null && surfaceViewRenderer != null){
            mEngine.subscribe(mStreamName,surfaceViewRenderer);
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

        //surfaceView = new GLSurfaceView(context);
        surfaceViewRenderer = new SurfaceViewRenderer(context);
        RelativeLayout.LayoutParams surfaceView_layoutParams = new RelativeLayout.LayoutParams(400, 400);
        surfaceView_layoutParams.leftMargin = 0;
        surfaceView_layoutParams.topMargin = -0+y*H/100;
        surfaceView_layoutParams.bottomMargin = -400+y*H/100;
        surfaceView_layoutParams.rightMargin = 400;
        surfaceViewRenderer.setLayoutParams(surfaceView_layoutParams);
        mSurfacelayout.addView(surfaceViewRenderer);
        surfaceViewRenderer.setKeepScreenOn(true);

    }

    public void deleteRenderer(){
        KkVideoRendererGui.remove(surfaceViewRenderer);
        surfaceViewRenderer = null;
    }

    public VideoRenderer.Callbacks getSurfaceViewRenderer(){
        return surfaceViewRenderer;
    }

    public void destroyLocal(){
        mSurfacelayout.removeView(surfaceViewRenderer);
        surfaceViewRenderer = null;
        mEngine = null;
        mStreamName = null;
    }
}
