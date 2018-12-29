package com.melot.engine;

import android.content.Context;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;


//TODO create message classes unique to this plugin
/**
 * Created by ben.trent on 7/24/2015.
 */
public class VideoRoomTest implements KkEventHandler {
    public static final String TAG = "VideoRoomTest";
    public static final String REQUEST = "request";
    public static final String MESSAGE = "message";
    public static final String PUBLISHERS = "publishers";
    private KkPluginHandle handle = null;
    private VideoRenderer.Callbacks mLocalRender;
    private Deque<VideoRenderer.Callbacks> availableRemoteRenderers = new ArrayDeque<>();
    private HashMap<BigInteger, VideoRenderer.Callbacks> remoteRenderers = new HashMap<>();
    private KkRTCEngine mEngine = null;
    private Boolean isChannelExists = true;
    private String channelName = "66666";

    private ManagerHttpConnect connect = new ManagerHttpConnect();
    private String json_httpconnect = null;
    private String json_channel = null;

    private KkEngineEventHandler mEngineEventHandler = null;

    //private int roomid = 31;
    private JSONArray publishers_array;
    private int uid = (int)System.currentTimeMillis() / 1000;
    private String localuserId = String.valueOf(uid);
    private int test = 0;
    //private String streamNames[] = new String[4];

    private boolean audio_test = true;

    private VideoRenderer.Callbacks localRender;
    private boolean isstartPreviewRemoteVideo = false;
    private boolean isjoinRoom = false;
    private Context mContext = null;
    private RelativeLayout mLayout = null;
    private RelativeLayout mSurfacelayout = null;
    private RelativeLayout mLocalSurfacelayout = null;
    private KkRenderGuiManager mLocalRenderManager;
    //private KkLocalRenderGuiManager mLocalRenderManager;
    private KkRenderGuiManager mRenderManager[] = new KkRenderGuiManager[4];

    public VideoRoomTest(/*VideoRenderer.Callbacks localRender, VideoRenderer.Callbacks[] remoteRenders,*/Context context,  RelativeLayout layout,RelativeLayout surfacelayout,RelativeLayout localsurfacelayout) {
        mContext = context;
        mLayout = layout;
        mSurfacelayout = surfacelayout;
        mLocalSurfacelayout = localsurfacelayout;
    }

    public boolean initializeMediaContext(Context context, String appId,boolean videoHwAcceleration, EGLContext eglContext){
        this.mEngineEventHandler = new KkEngineEventHandler(/*mContext*/context);
        try{
            mEngine = KkRTCEngine.createEngine(context,appId,mEngineEventHandler.mRtcEventHandler);

            mEngineEventHandler.addEventHandler(this);
        }catch (Exception ex){

        }
        return mEngine.initialize(context, eglContext,null);
    }

    public void unit(){
        mEngine.uninit();
        mEngine.destroyEngine();
        this.mEngineEventHandler = null;
    }

/*
    public VideoRenderer.Callbacks setupLocalVideo(VideoCanvas canvas){
        return mEngine.setupLocalVideo(canvas);
    }
*/

    public void startPreView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLocalRenderManager = new KkRenderGuiManager(mEngine);
                //mLocalRenderManager = new KkLocalRenderGuiManager(mEngine);
                mLocalRenderManager.createLocalRenderer(mContext,mLocalSurfacelayout,0,0, 100, 100, RendererCommon.ScalingType.SCALE_ASPECT_FILL, true);
                if (mEngine != null){
                    mEngine.startPreview(1,mLocalRenderManager.getRenderer());
                }
            }
        });

    }

    public void stopPreview(){
        if (mEngine != null){
            mEngine.stopPreview();
        }
        if (mLocalRenderManager != null){
            mLocalRenderManager.destroyLocal();
            mLocalRenderManager = null;
        }
    }

    public void switchCamera(){
        if(mEngine != null){
            mEngine.switchCamera();
        }
    }

    public void joinRoom(int roomid){
        if (mEngine != null && !isjoinRoom){
            isjoinRoom = true;
            mEngine.joinRoom(roomid,uid);
        }
    }

    public void leaveRoom(){
        if (mEngine != null && isjoinRoom){
            isjoinRoom = false;

            mEngine.leaveRoom();

            for(int m = 0;m<mRenderManager.length;m++){
                if (mRenderManager[m] != null){
                    mRenderManager[m].unSubscribe();
                    mRenderManager[m].destroy();
                    mRenderManager[m] = null;
                }
            }
        }
    }

    public void startPush(String url){
        if (mEngine != null){
            mEngine.pushlish(url);
        }
    }

    public void stopPush(){
        if (mEngine != null){
            mEngine.unPushlish();
        }
    }

    public void startPreviewRemoteVideo(){
        if (!isstartPreviewRemoteVideo && isjoinRoom){
            if (mEngine != null){
                try {
                    test = 0;
                    for (int i = 0; i < publishers_array.length(); i++) {
                        JSONObject pub = publishers_array.getJSONObject(i);
                        if (!localuserId.equals(pub.getString("userId"))){
                            Log.d(TAG,"lzx Test startPreviewRemoteVideo1");
                            JSONArray streamnames = pub.getJSONArray("streamNames");
                            for(int j = 0;j<streamnames.length();j++){
                                String streamname = streamnames.getString(j);
                                for(int m = 0;m<mRenderManager.length;m++){
                                    Log.d(TAG,"lzx Test startPreviewRemoteVideo2 m = " + m + " test = " + test);
                                    if (test == m && mRenderManager[m] == null){
                                        //streamNames[m] = streamname;
                                        test = test + 1;
                                        mRenderManager[m] = new KkRenderGuiManager(mEngine);
                                        mRenderManager[m].createRenderer(mContext,streamname,mLayout,mSurfacelayout,50, m*20+5, 40, 20, RendererCommon.ScalingType.SCALE_ASPECT_FILL, true);
                                        mRenderManager[m].subscribe(streamname);
                                        Log.d(TAG,"lzx Test startPreviewRemoteVideo streamname = " + streamname);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }catch (Exception ex){
                    Log.e(TAG,"lzx Test subScribe Exception = " + ex.toString());
                }

            }
            isstartPreviewRemoteVideo = true;
        }

    }

    public void stopPreviewRemoteVideo(String streamName){
        if (isstartPreviewRemoteVideo && isjoinRoom) {
            if (mEngine != null) {
                for (int i = 0; i < mRenderManager.length; i++) {
                    if (mRenderManager[i] != null){
                        mRenderManager[i].unSubscribe();
                        mRenderManager[i].destroy();
                        mRenderManager[i] = null;
                    }
                }
            }
            isstartPreviewRemoteVideo = false;
        }
    }

    public void muteLocalVideoStream(boolean muted){
        if (mEngine != null){
            mEngine.muteLocalVideoStream(muted);
        }
    }

    public void muteLocalAudioStream(boolean muted){
        if (mEngine != null){
            mEngine.muteLocalAudioStream(muted);
        }
    }

    public void getRtcStats(KkRTCEngine.KkStatObserver observer) {
        if (mEngine != null){
            mEngine.getRtcStats(observer);
        }
    }

    public void muteRemoteVideoStream(String streamName, boolean muted) {
        try {
            for (int i = 0; i < publishers_array.length(); i++) {
                JSONObject pub = publishers_array.getJSONObject(i);
                if (!localuserId.equals(pub.getString("userId"))) {
                    JSONArray streamnames = pub.getJSONArray("streamNames");
                    for (int j = 0; j < streamnames.length(); j++) {
                        String streamname = streamnames.getString(j);
                        mEngine.muteRemoteVideoStream(streamname, muted);
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    public void muteRemoteAudioStream(String streamName, boolean muted){
        try {
            for (int i = 0; i < publishers_array.length(); i++) {
                JSONObject pub = publishers_array.getJSONObject(i);
                if (!localuserId.equals(pub.getString("userId"))) {
                    JSONArray streamnames = pub.getJSONArray("streamNames");
                    for (int j = 0; j < streamnames.length(); j++) {
                        String streamname = streamnames.getString(j);
                        mEngine.muteRemoteAudioStream(streamname, muted);
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    public void setBeautyPara(int beautyPara, int flag){

        if(mEngine != null){
            //mEngine.setBeautyPara(beautyPara,flag);
        }
    }

    public void setStickPic(String itemName){
        if (mEngine != null){
            //mEngine.setStickPic(itemName);
        }
    }

    public int getVideoWidth(){
        int w = 0;
        if (mEngine != null){
            w = mEngine.getLocalMediaManager().getWidth();
        }
        return w;
    }

    public int getVideoHeight(){
        int h = 0;
        if (mEngine != null){
            h = mEngine.getLocalMediaManager().getHeight();
        }
        return h;
    }

    public int getBitrate(){
        int bitrate = 0;
        if (mEngine != null){

        }
        return bitrate;
    }

    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed){

    }

    public void onJoinChannelSuccess(String channel, int uid, int elapsed){
    }

    public void onUserJoined(JSONArray array){

    }

    public void onUserOffline(int uid, int reason){

    }

    public void onPublisherInRoom(JSONArray array){
        publishers_array = array;
        if (isjoinRoom) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String Names[] = new String[4];
                    if (mEngine != null) {
                        try {
                            for (int i = 0; i < publishers_array.length(); i++) {
                                JSONObject pub = publishers_array.getJSONObject(i);
                                if (!localuserId.equals(pub.getString("userId"))) {
                                    JSONArray streamnames = pub.getJSONArray("streamNames");
                                    for (int j = 0; j < streamnames.length(); j++) {
                                        String streamname = streamnames.getString(j);
                                        for (int m = 0; m < Names.length; m++) {
                                            Log.d(TAG, "lzx Test startPreviewRemoteVideo2 m = " + m + " test = " + test);
                                            if (Names[m] == null) {
                                                Names[m] = streamname;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < mRenderManager.length; i++) {
                                if (mRenderManager[i] != null) {
                                    boolean tag = false;
                                    for (int j = 0; j < Names.length; j++) {
                                        if (mRenderManager[i].getStreamName() != null) {
                                            if (mRenderManager[i].getStreamName().equals(Names[j])) {
                                                tag = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!tag) {
                                        if (mRenderManager[i].getStreamName() != null) {
                                            Log.d(TAG, "lzx Test onPublisherInRoom unSubscribe");
                                            mRenderManager[i].unSubscribe();
                                            mRenderManager[i].destroy();
                                            mRenderManager[i] = null;
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < Names.length; i++) {
                                boolean tag = false;
                                for (int j = 0; j < mRenderManager.length; j++) {
                                    if (Names[i] != null && mRenderManager[j] != null) {
                                        if (Names[i].equals(mRenderManager[j].getStreamName())) {
                                            tag = true;
                                            break;
                                        }
                                    }
                                }
                                if (!tag) {
                                    for (int m = 0; m < mRenderManager.length; m++) {
                                        if (Names[i] != null) {
                                            if (mRenderManager[m] == null) {
                                                mRenderManager[m] = new KkRenderGuiManager(mEngine);
                                                Log.d(TAG, "lzx Test onPublisherInRoom subscribe");
                                                mRenderManager[m].createRenderer(mContext, Names[i], mLayout, mSurfacelayout, 50, m * 20 + 5, 40, 20, RendererCommon.ScalingType.SCALE_ASPECT_FILL, true);
                                            }
                                            if (mRenderManager[m].getStreamName() == null) {
                                                mRenderManager[m].setStreamName(Names[i]);
                                                //mRenderManager[m].subscribe(Names[i]);
                                                Log.d(TAG, "lzx Test subscribe streamName = " + Names[i] + " localuserId = " + localuserId);
                                                break;
                                            }

                                        }
                                    }
                                }
                            }

                        } catch (Exception ex) {
                            Log.d(TAG, "lzx Test onPublisherInRoom8 ");
                            Log.e(TAG, "lzx Test subScribe Exception = " + ex.toString());
                        }
                    }
                }
            });
        }
    }

    public void onError(int err){

    }

    public void onWarning(int warn){

    }
    // 频道内网络质量报告回调
    public void onNetworkQuality(int uid, int txQuality, int rxQuality){

    }

    public void onUserMuteAudio( int uid, boolean muted ){

    }

    public void onConnectionInterrupted(){

    }

    public void onConnectionLost(){

    }

    public void onLastmileQuality(int quality){

    }

    public void onLeaveChannel(){

    }

    Handler mHandler = new Handler(Looper.getMainLooper());

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
