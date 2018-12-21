package com.melot.engine;

import android.content.Context;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.KkVideoRendererGui;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Arrays;
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

    private boolean isstartPreviewRemoteVideo = false;
    private boolean isjoinRoom = false;
    private Context mContext = null;
    private RelativeLayout mLayout = null;
    private KkRenderGuiManager mRenderManager[] = new KkRenderGuiManager[4];

    public VideoRoomTest(/*VideoRenderer.Callbacks localRender, VideoRenderer.Callbacks[] remoteRenders,*/Context context, String appId, RelativeLayout layout) {
        mContext = context;
        mLayout = layout;
        this.mEngineEventHandler = new KkEngineEventHandler(/*mContext*/context);
        try{
            mEngine = KkRTCEngine.createEngine(context,appId,mEngineEventHandler.mRtcEventHandler);

            mEngineEventHandler.addEventHandler(this);
        }catch (Exception ex){

        }

    }

    public boolean initializeMediaContext(Context context, boolean videoHwAcceleration, EGLContext eglContext){
        return mEngine.initialize(context, eglContext,null);
    }

    public void startPreView(VideoRenderer.Callbacks localrender){
        if (mEngine != null){
            mEngine.startPreview(1,localrender);
        }

/*
        for(int m = 0;m<mRenderManager.length;m++){
            mRenderManager[m] = new KkRenderGuiManager(mEngine);
            mRenderManager[m].createRenderer(50, m*20+5, 40, 20, RendererCommon.ScalingType.SCALE_ASPECT_FILL, true);
            //mRenderManager[m].addCheckbox(mContext,mLayout,50, m*20+5);
        }
*/
    }

    public void stopPreview(){
        if (mEngine != null){
            mEngine.stopPreview();
        }
    }

    public void switchCamera(){
        if(mEngine != null){
            mEngine.switchCamera();
        }
/*
        mEngine.muteLocalVideoStream(audio_test);
        audio_test = !audio_test;
*/

/*
        try {
            JSONObject pub = publishers_array.getJSONObject(0);
            if (!localuserId.equals(pub.getString("userId"))){
                JSONArray streamnames = pub.getJSONArray("streamNames");
                String streamname = streamnames.getString(0);
                mEngine.muteLocalVideoStream(streamname,audio_test);
            }

        }catch (Exception ex){
            String err = ex.toString();
            Log.e(TAG,"lzx subScribe Exception = " + err);
        }
*/
    }

    public void joinRoom(int roomid){

/*
        for(int m = 0;m<mRenderManager.length;m++){
            mRenderManager[m] = new KkRenderGuiManager(mEngine);
            mRenderManager[m].createRenderer(50, m*20+5, 40, 20, RendererCommon.ScalingType.SCALE_ASPECT_FILL, true);
            //mRenderManager[m].addCheckbox(mContext,mLayout,50, m*20+5);
        }

*/

        if (mEngine != null && !isjoinRoom){
            mEngine.joinRoom(roomid,uid);
            isjoinRoom = true;
        }
    }

    public void leaveRoom(){
        if (mEngine != null && isjoinRoom){
            mEngine.leaveRoom();

            for(int m = 0;m<mRenderManager.length;m++){
                if (mRenderManager[m] != null){
                    mRenderManager[m].unSubscribe();
                    mRenderManager[m].destroy();
                    mRenderManager[m] = null;
                }
            }

            isjoinRoom = false;
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
                                        mRenderManager[i].createRenderer(mContext,mLayout,50, m*20+5, 40, 20, RendererCommon.ScalingType.SCALE_ASPECT_FILL, true);
                                        mRenderManager[i].subscribe(streamname);
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
                    mRenderManager[i].unSubscribe();
                    mRenderManager[i].destroy();
                    mRenderManager[i] = null;
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
                                if(mRenderManager[i] != null){
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
                                            if (mRenderManager[m] == null){
                                                mRenderManager[m] = new KkRenderGuiManager(mEngine);
                                                mRenderManager[m].createRenderer(mContext,mLayout,50, m*20+5, 40, 20, RendererCommon.ScalingType.SCALE_ASPECT_FILL, true);
                                            }
                                            if (mRenderManager[m].getStreamName() == null){
                                                mRenderManager[m].subscribe(Names[i]);
                                                break;
                                            }

                                        }
                                    }
                                }
                            }

                        } catch (Exception ex) {
                            Log.d(TAG,"lzx Test onPublisherInRoom8 ");
                            Log.e(TAG, "lzx Test subScribe Exception = " + ex.toString());
                        }
                    }
                }
            });
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

    Handler mHandler = new Handler(Looper.getMainLooper());

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
