package com.melot.engine;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


public class KkEngineEventHandler {
    private static String TAG = "MyEngineEventHandler";
    private final Context mContext;
    private final ConcurrentHashMap<KkEventHandler, Integer> mEventHandlerList = new ConcurrentHashMap<KkEventHandler, Integer>();
    final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                Log.d(TAG, "onFirstRemoteVideoDecoded " + " " + width + " " + height + " " + elapsed);
                handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
            }
        }

        @Override
        public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
            Log.d(TAG, "onFirstLocalVideoFrame " + " " + width + " " + height + " " + elapsed);
        }

        @Override
        public void onUserJoined(JSONArray array) {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onUserJoined(array);
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            // FIXME this callback may return times
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onUserOffline(uid, reason);
            }
        }

        @Override
        public void onPublisherInRoom(JSONArray array) {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onPublisherInRoom(array);
            }
        }

        @Override
        public void onUserMuteVideo(int uid, boolean muted) {
        }

        @Override
        public void onUserMuteAudio(int uid, boolean muted) {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onUserMuteAudio(uid, muted);
            }
        }

        @Override
        public void onConnectionInterrupted() {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onConnectionInterrupted();
            }
        }

        @Override
        public void onConnectionLost() {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onConnectionLost();
            }
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {

        }

        @Override
        public void onLastmileQuality(int quality) {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onLastmileQuality(quality);
            }
        }

        @Override
        public void onError(int err) {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onError(err);
            }
        }

        @Override
        public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onNetworkQuality(uid, txQuality, rxQuality);
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.d(TAG, "onJoinChannelSuccess " + channel + " " + uid + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);

            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onJoinChannelSuccess(channel, uid, elapsed);
            }
        }

        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.d(TAG, "onRejoinChannelSuccess " + channel + " " + uid + " " + elapsed);
        }

        public void onWarning(int warn) {
            Iterator<KkEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                KkEventHandler handler = it.next();
                handler.onWarning(warn);
            }

            Log.d(TAG, "onWarning " + warn);
        }
    };

    public KkEngineEventHandler(Context ctx) {
        this.mContext = ctx;
    }

    public void addEventHandler(KkEventHandler handler) {
        this.mEventHandlerList.put(handler, 0);
    }

    public void removeEventHandler(KkEventHandler handler) {
        this.mEventHandlerList.remove(handler);
    }

}
