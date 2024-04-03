package com.capcorp.utils;

import static com.capcorp.utils.ConstantsKt.BASE_URL;
import static com.capcorp.utils.GeneralFunctionKt.getAccessToken;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.capcorp.utils.location.LiveLocation;
import com.capcorp.webservice.models.chats.ChatDelivery;
import com.capcorp.webservice.models.chats.ChatMessageListing;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Rohit Sharma on 9/8/17.
 */

public class AppSocket {

    private static final AppSocket ourInstance = new AppSocket();
    private final int MANUAL_RECONNECT_INTERVAL = 10000;
    private final String TAG = "AppSocket";
    private Socket mSocket;
    private Timer manualReconnectTimer = new Timer();
    private final List<OnMessageReceiver> onMessageReceiverList = new ArrayList<>();

    private final List<OnLiveLocationListener> onLiveLocationListenerList = new ArrayList<>();

    private final List<ConnectionListener> onConnectionListeners = new ArrayList<>();
    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            manualReconnectTimer.cancel();
            Log.e(TAG, "onConnect called");
            notifyConnectionListeners(Socket.EVENT_CONNECT);
        }
    };
    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onDisconnect called");
            restartManualReconnection();
            notifyConnectionListeners(Socket.EVENT_DISCONNECT);
        }
    };
    private final Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onError called");
            restartManualReconnection();
            notifyConnectionListeners(Socket.EVENT_ERROR);
        }
    };
    private final Emitter.Listener onTimeOut = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onTimeOut called");
            restartManualReconnection();
            notifyConnectionListeners(Socket.EVENT_CONNECT_TIMEOUT);
        }
    };
    private final Emitter.Listener onReconnecting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onReconnecting called");
            restartManualReconnection();
            notifyConnectionListeners(Socket.EVENT_RECONNECTING);
        }
    };
    private final Emitter.Listener onReconnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "onReconnectError called");
            restartManualReconnection();
            notifyConnectionListeners(Socket.EVENT_RECONNECT_ERROR);
        }
    };

    private AppSocket() {
    }

    public static AppSocket get() {
        return ourInstance;
    }

    public boolean init(Context context) {
        onMessageReceiverList.clear();
        onConnectionListeners.clear();
        try {
            if (!getAccessToken(context).isEmpty()) {
                IO.Options options = new IO.Options();
                options.forceNew = false;
                options.reconnection = true;
                options.query = "accessToken=" + getAccessToken(context);
                mSocket = IO.socket(BASE_URL, options);
                connect();
                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onError);
                mSocket.on(Socket.EVENT_ERROR, onError);
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeOut);
                mSocket.on(Socket.EVENT_RECONNECTING, onReconnecting);
                mSocket.on(Socket.EVENT_RECONNECT_ERROR, onReconnectError);
                mSocket.on(Socket.EVENT_RECONNECT_FAILED, onReconnectError);
                return true;
            } else {
                return false;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isConnected() {
        return mSocket.connected();
    }

    private void restartManualReconnection() {
        manualReconnectTimer.cancel();
        manualReconnectTimer = new Timer();
        manualReconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mSocket.io().reconnection(true);
                connect();
                Log.e(TAG, "ManualReconnection Timer Task Called");
            }
        }, MANUAL_RECONNECT_INTERVAL);
    }

    public Socket getSocket() {
        if (!mSocket.connected())
            connect();
        return mSocket;
    }

    public void connect() {
        if (!mSocket.connected())
            mSocket.connect();
    }

    public void disconnect() {
        mSocket.off();
        mSocket.disconnect();
    }

    public void emit(final String event, final Object... args) {
        mSocket.emit(event, args);
    }

    public void on(String event, Emitter.Listener fn) {
        mSocket.on(event, fn);
    }

    public void off() {
        mSocket.off();
    }

    public void off(String event) {
        mSocket.off(event);
    }

    public void off(String event, Emitter.Listener fn) {
        mSocket.off(event, fn);
    }

    public void sendMessage(ChatMessageListing message, final OnSentMessageReceiver msgAck) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(Events.SEND_MESSAGE, jsonObject, new Ack() {
            @Override
            public void call(final Object... args) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("-----------ack vivek------ " + args[0].toString());
                        msgAck.onSentMessageReceive(new Gson().fromJson(args[0].toString(), ChatMessageListing.class));
                    }
                });
            }
        });
    }

    public void deliveryMessage(ChatDelivery message, final OnDeliverReceiver msgAck) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(Events.DELIVER_MESSAGE, jsonObject, new Ack() {
            @Override
            public void call(final Object... args) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("-------- deliver--------- " + args[0].toString());
                        msgAck.onDeliverReceive(new Gson().fromJson(args[0].toString(), ChatMessageListing.class));
                    }
                });
            }
        });
    }

    public void readMessage(ChatDelivery message) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(Events.READ_MESSAGE, jsonObject, new Ack() {
            @Override
            public void call(final Object... args) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("---------read-------- " + args[0].toString());
                    }
                });
            }
        });
    }

    public void sendMessageDelivery(ChatMessageListing message) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(Events.SEND_MESSAGE, jsonObject);
    }

    public void addConnectionListener(ConnectionListener listener) {
        onConnectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        onConnectionListeners.remove(listener);
    }

    public void removeAllConnectionListeners() {
        onConnectionListeners.clear();
    }

    private void notifyConnectionListeners(final String status) {
        for (final ConnectionListener listener : onConnectionListeners) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onConnectionStatusChanged(status);
                }
            });
        }
    }

    public void addOnMessageReceiver(OnMessageReceiver receiver) {
        if (onMessageReceiverList.isEmpty()) {
            onReceiveMessageEvent();
        }
        onMessageReceiverList.add(receiver);
    }

    public void removeOnMessageReceiver(OnMessageReceiver receiver) {
        onMessageReceiverList.remove(receiver);
        if (onMessageReceiverList.isEmpty()) {
            mSocket.off(Events.RECEIVE_MESSAGE);
        }
    }

    public void removeAllMessageReceivers() {
        onMessageReceiverList.clear();
        mSocket.off(Events.RECEIVE_MESSAGE);
    }

    private void onReceiveMessageEvent() {
        mSocket.on(Events.RECEIVE_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args != null) {
                    ChatMessageListing chat = new Gson().fromJson(args[0].toString(), ChatMessageListing.class);
                    notifyMessageReceivers(chat);
                }
            }
        });
    }

    public void onReadMessageEvent(OnReadReceiver msgAck) {
        mSocket.on(Events.MARK_RECEIVE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("---------read_receive_ack-------- " + args[0].toString());
                        msgAck.onReadReceive(new Gson().fromJson(args[0].toString(), ChatMessageListing.class));
                    }
                });

            }
        });
    }

    private void notifyMessageReceivers(final ChatMessageListing message) {
        for (final OnMessageReceiver receiver : onMessageReceiverList) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    receiver.onMessageReceive(message);
                }
            });

        }
    }

    public void addOnLiveLocationListener(OnLiveLocationListener liveLocationListener) {
        if (onLiveLocationListenerList.isEmpty()) {
            onLiveLocationEvent();
        }
        onLiveLocationListenerList.add(liveLocationListener);
    }

    public void removeLiveLocationListener(OnLiveLocationListener liveLocationListener) {
        onLiveLocationListenerList.remove(liveLocationListener);
        if (onLiveLocationListenerList.isEmpty()) {
            mSocket.off(Events.LOCATION);
        }
    }

    public void removeAllLiveLocationListener() {
        onLiveLocationListenerList.clear();
        mSocket.off(Events.LOCATION);
    }

    public void sendLiveLocation(LiveLocation liveLocation) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(liveLocation));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(Events.LIVE_TRACKING, jsonObject, new Ack() {
            @Override
            public void call(Object... args) {
                // Acknowledgement
            }
        });
    }

    private void onLiveLocationEvent() {
        mSocket.on(Events.LOCATION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                LiveLocation liveLocation = new Gson().fromJson(args[0].toString(), LiveLocation.class);
                notifyLiveLocationListeners(liveLocation);
            }
        });
    }

    private void notifyLiveLocationListeners(final LiveLocation liveLocation) {
        for (final OnLiveLocationListener listener : onLiveLocationListenerList) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onLiveLocationUpdate(liveLocation);
                }
            });
        }
    }

    public interface Events {
        String SEND_MESSAGE = "sendMessage";
        String RECEIVE_MESSAGE = "receiveMessage";
        String DELIVER_MESSAGE = "deliverMessage";
        String MARK_RECEIVE = "oppositeMarkAsRead";
        String READ_MESSAGE = "readMessage";
        String LIVE_TRACKING = "liveTracking";
        String LOCATION = "location";
    }

    public interface OnLiveLocationListener {
        void onLiveLocationUpdate(LiveLocation liveLocation);
    }

    public interface OnMessageReceiver {
        void onMessageReceive(ChatMessageListing message);
    }

    public interface OnSentMessageReceiver {
        void onSentMessageReceive(ChatMessageListing message);
    }

    public interface OnDeliverReceiver {
        void onDeliverReceive(ChatMessageListing message);
    }

    public interface OnReadReceiver {
        void onReadReceive(ChatMessageListing message);
    }

    public interface ConnectionListener {
        void onConnectionStatusChanged(String status);
    }

}
