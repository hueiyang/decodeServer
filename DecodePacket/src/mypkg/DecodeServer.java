package mypkg;

public class DecodeServer {
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		UDPServer udpServer = new UDPServer();
		
		if(udpServer.ServerOn()) {
			udpServer.start();
			udpServer.WaitConnection();
		}
	}
}
