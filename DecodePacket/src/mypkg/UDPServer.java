package mypkg;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class UDPServer extends Thread{
	final int PORT = 5033;		// UDP server port.
	final int TYPEIDINDEX = 9;	// Device type id index.
	private byte[] hexData = new byte[146];	// received raw data packet.
	private DatagramSocket udpServer;
	private DeviceRule deviceRule;
	
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private String timestamp;
	
	public UDPServer() {}
	
	// Start the UDP server.
	public boolean ServerOn() {
		// Read the packet rule from file.
		deviceRule = new DeviceRule();
		
		if(deviceRule.init()) {
			try {
				udpServer = new DatagramSocket(PORT);
				System.out.println("[UDP Server ON]");
				return true;
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				return false;
			}
		}else
			return false;
	}
	
	public void WaitConnection() {
		int connectionCount = 0;
		
		while(true) {
			DatagramPacket receivePacket = new DatagramPacket(hexData, hexData.length);
			try {
				udpServer.receive(receivePacket);
				timestamp = dateFormat.format(new Date());	// get currect time and convert to String.
				
				System.out.print("[" + timestamp + "] " + "[ " + (++connectionCount) +" Packet Receive] - ");
				
				PacketData packetData = new PacketData(timestamp, new String(receivePacket.getData()));
				
				// first check CRC > Correct: decode ; Wrong: waitting next
				if(packetData.checkCRC()) {
					packetData.setRule(deviceRule.getRule(packetData.getTypeId())); 
					packetData.decode();
				}else
					System.out.print("[Packet CRC Incorrect. Waitting next packet.]\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				System.out.println(e.getMessage());
				break;
			}
		}
	}
	
	// instead of RESTful api ... temporary ...
	public void run() {
		Scanner scan = new Scanner(System.in);
		
		while(true) {
			String cmd = scan.nextLine();
			switch(cmd) {
			case "show":
				System.out.println("[Result]\n" + PacketData.getAllData());
				break;
			case "quit":
				ServerDown();
				return;
			default:
					break;
			}
		}
	}
	
	public void ServerDown() {
		udpServer.close();
		System.out.println("[UDP Server Closed]");
	}
}