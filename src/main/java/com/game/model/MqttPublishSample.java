package com.game.model;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;



public class MqttPublishSample {
	public static void main(String[] args) {
		MqttPublishSample m=new MqttPublishSample();
		for (int i = 0; i < 3; i++) {
			String downUrl="https://static.pexels.com/photos/479/landscape-nature-sunset-trees.jpg";
			m.mqttPublish(downUrl);	
		}
		System.out.println("end");
	}
	public void mqttPublish(String downUrl)
	{
		boolean status;
		String content;
		String saveDir		="/home/bridgelabz6/Downloads/apk";
        String topic        = "Download";
        int qos             = 1;
        String broker       = "tcp://localhost:1883";
        String clientId     = "pahomqttpublish";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            
            FileStreaming fs=new FileStreaming();
            status=fs.downloadFile(downUrl, saveDir);//file downloading
            if(status==true)
            	content="Download Completed";
            else
            	content="Download can not Completed";
            System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
            //System.exit(0);
        
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }

}