package uk.ac.cam.cl.group_project.delta.algorithm.communications;

import java.util.Map;

import uk.ac.cam.cl.group_project.delta.NetworkInterface;
import uk.ac.cam.cl.group_project.delta.algorithm.MessageData;

public class MessageReceiver {
	
	private int position = 0;
	private PlatoonLookup messageLookup;

	public MessageReceiver(NetworkInterface network, PlatoonLookup map) {
		messageLookup = map;
	}

	public void sendMessage(MessageData message) {
		// TODO Auto-generated method stub
		
	}

	public int getCurrentPosition() {
		return position;
	}

	public void notifyEmergency() {
		// TODO Auto-generated method stub
		
	}
	
	
}
