package lipwapoa;

import java.util.GregorianCalendar;

import org.smslib.AGateway.Protocols;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

public class Main implements Runnable{
	private static SerialModemGateway gateway;//
	private static Service service;
	static DBHandler handler;

	public static void main(String []args){
		handler = new DBHandler();
		/*emulator
		AITISMSServer server = new AITISMSServer(true);
		
		try {
            //Set your processor to create a new object of your handler
            SMSHandlerThread.setAITIInboundMessageNotification(new SmsProcessor());

            //wait for incoming messages
            server.doIt();
        } catch (Exception e) {
        	
        }*/
		
		// Real implementation
		gateway = new SerialModemGateway("modem", "COM13", 460800, "HUAWEI", "E160");
		gateway.setProtocol(Protocols.PDU);
		gateway.setInbound(true);
		gateway.setOutbound(true);
		
		service = new Service();
		service.setInboundNotification(new SmsProcessor());
		
		try {
			service.addGateway(gateway);
			service.startService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Service getService(){
		return service;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(1000);
			handler.detProductToDelete(new GregorianCalendar().getTime().toString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
