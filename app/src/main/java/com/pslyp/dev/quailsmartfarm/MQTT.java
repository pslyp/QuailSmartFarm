package com.pslyp.dev.quailsmartfarm;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTT {

    private Context context;

    private String clientId;
    private MqttAndroidClient client;
    private IMqttToken token;

//    String MQTTHOST = "tcp://35.240.245.133:1883";
//    String USERNAME = "pslyp";
//    String PASSWORD = "1475369";

    String MQTTHOST = "tcp://test.mosquitto.org:1883";

    private boolean connected;

    public MQTT(Context context) {
        this.context = context;
    }

    public boolean isConnected() {
        return connected;
    }

    public void connect() {
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context.getApplicationContext(), MQTTHOST, clientId);
//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setUserName(USERNAME);
//        options.setPassword(PASSWORD.toCharArray());

        try {
            token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //Toast.makeText(MQTT.this, "Connected MQTT", Toast.LENGTH_SHORT).show();
//                    subscribe("gh51f5hr55gdfcue684fs61s6v3d54v8/brightness", 1);
//                    subscribe("gh51f5hr55gdfcue684fs61s6v3d54v8/temperature", 1);
//                    subscribe("gh51f5hr55gdfcue684fs61s6v3d54v8/fanStatus", 1);
//                    subscribe("gh51f5hr55gdfcue684fs61s6v3d54v8/lampStatus", 1);

//                    subscribe("4C31A6DBCD72FF1171332936EFDBF273/brightness", 1);
//                    subscribe("4C31A6DBCD72FF1171332936EFDBF273/temperature", 1);
//                    subscribe("4C31A6DBCD72FF1171332936EFDBF273/fanStatus", 1);
//                    subscribe("4C31A6DBCD72FF1171332936EFDBF273/lampStatus", 1);

                    connected = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    connected = false;
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        callBack();
    }

    private void callBack() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                //if(topic.equals("gh51f5hr55gdfcue684fs61s6v3d54v8/temperature"))
                    //temp.setText(new String(message.getPayload()));
                //if(topic.equals("gh51f5hr55gdfcue684fs61s6v3d54v8/brightness"))
                    //bright.setText(new String(message.getPayload()));
                //if(topic.equals("gh51f5hr55gdfcue684fs61s6v3d54v8/fanStatus"))
                    //fanSta.setText(new String(message.getPayload()));
                //if(topic.equals("gh51f5hr55gdfcue684fs61s6v3d54v8/lampStatus"))
                    //lampSta.setText(new String(message.getPayload()));

                /*
                switch (topic) {
                    case "temperature":
                        temp.setText(new String(message.getPayload()));
                        break;
                    case "brightness":
                        bright.setText(new String(message.getPayload()));
                        break;
                    case "fanStatus":
                        fanSta.setText(new String(message.getPayload()));
                        break;
                    case "lampStatus":
                        lampSta.setText(new String(message.getPayload()));
                        break;
                    default: break;
                }
                */
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void publish(String topic, String text) {
        byte[] encodePayload = new byte[0];
        try {
//            String topic2 = "/cloudMessage";
//            String message2 = "cwui9n92gqM:APA91bE5fYxbMAV_ZFAwmRdg7hoXvGcPobCXF_Pli93n80bEoNuEwIgO2csSqbXTVJvuJuVhpCQ7iiADUWQnLTU3y7mj0pWrzlQwXXqT6Oh_Oi98-6Dni0NcTP40gt_jlXXYbLWSoAih";
//
//            MqttMessage message = new MqttMessage();
//            message.setPayload(text.getBytes());

//            encodePayload = text.getBytes("UTF-8");
//            MqttMessage message = new MqttMessage(encodePayload);

            client.publish(topic, text.getBytes(), 0 ,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic, int qos) {
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //Toast.makeText(MainActivity.this, "Subscribe: Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Toast.makeText(MainActivity.this, "Subscribe: Fail", Toast.LENGTH_SHORT).show();
                }
            });
        } catch(MqttException e) {
            e.printStackTrace();
        }
    }
}
