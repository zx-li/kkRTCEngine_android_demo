package com.melot.engine;

import org.json.JSONArray;

public interface KkEventHandler {
    void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);

    void onJoinChannelSuccess(String channel, int uid, int elapsed);

    void onUserJoined(JSONArray array);
    
    void onUserOffline(int uid, int reason);

    void onPublisherInRoom(JSONArray array);

    void onError(int err);
    
    public void onWarning(int warn);
    // 频道内网络质量报告回调 
    public void onNetworkQuality(int uid, int txQuality, int rxQuality);
    
    public void onUserMuteAudio( int uid, boolean muted );
    
    public void onConnectionInterrupted();
    
    public void onConnectionLost();

    public void onLastmileQuality(int quality);
}
