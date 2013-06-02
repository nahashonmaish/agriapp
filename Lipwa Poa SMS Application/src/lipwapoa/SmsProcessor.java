package lipwapoa;

import java.io.IOException;
import java.util.StringTokenizer;

import org.smslib.GatewayException;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.TimeoutException;

public class SmsProcessor implements IInboundMessageNotification{
	
	private DBHandler handler;
	private Main main;

	@Override
	public void process(String str, MessageTypes messageType, InboundMessage inboundMessage) {
		// TODO Auto-generated method stub
		
		handler = new DBHandler();
		main = new Main();
		
		String theMessage = inboundMessage.getText();
		String theNumber = inboundMessage.getOriginator();
		String theHelp= inboundMessage.getText();
		
		System.out.println(theMessage);
		
		StringTokenizer tokenizer = new StringTokenizer(theMessage, "#");
		String []hashItem = new String[tokenizer.countTokens()];

		
		int i = 0;
		while(tokenizer.hasMoreTokens()){
			hashItem[i] = tokenizer.nextToken();
			i++;
		}
		//System.out.println(hashItem[1]);
		
		if(hashItem[0].equalsIgnoreCase("register")){
			System.out.println("Registration");
			if(handler.validateRegistration(theNumber) == true){
				handler.registerMember(theNumber, hashItem[1]);
				System.out.println(hashItem[1] + " registered..");
				OutboundMessage out = new OutboundMessage(theNumber, "Thank you for registering with Lipwa POA..");
				try {
					//sv.sendMessage(out);
					main.getService().sendMessage(out);
				} catch (TimeoutException | GatewayException | IOException
						| InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				System.out.println(hashItem[1] + " not registered..");
				OutboundMessage out = new OutboundMessage(theNumber, "Sorry, you have already registered with Lipwa POA..");
				try {
					//sv.sendMessage(out);
					main.getService().sendMessage(out);
				} catch (TimeoutException | GatewayException | IOException
						| InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if(hashItem[0].equalsIgnoreCase("sell")){
			if(handler.getIfUserExist(theNumber) == true){
				handler.addCrop(hashItem[1], hashItem[2], hashItem[3], hashItem[4]);
				System.out.println("Crop added successfully..");
				
				try {
					OutboundMessage out = new OutboundMessage(theNumber, "You have successfully added "+ hashItem[1]+" to the Lipwa POA products list..");
					//sv.sendMessage(out);
					main.getService().sendMessage(out);
				} catch (TimeoutException | GatewayException | IOException
						| InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
				System.out.println(hashItem[1] + " not added..");
				OutboundMessage out = new OutboundMessage(theNumber, "Sorry, you must have registered with lipa POA to upload products..");
				try {
					//sv.sendMessage(out);
					main.getService().sendMessage(out);
				} catch (TimeoutException | GatewayException | IOException
						| InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if(hashItem[0].equalsIgnoreCase("buy")){
			System.out.println("Crop fetched..");
			if(handler.replyToUser(hashItem[1], hashItem[2]).length >= 0){
				OutboundMessage out = new OutboundMessage(theNumber, "Average price for "+hashItem[1]+" in "+hashItem[2]+" is Ksh."+ handler.replyToUser(hashItem[1], hashItem[2])[0] + " per unit");
				try {
					//sv.sendMessage(out);
					main.getService().sendMessage(out);
				} catch (TimeoutException | GatewayException | IOException
						| InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				OutboundMessage out = new OutboundMessage(theNumber, "Sorry, we could not get the average price for " + hashItem[1] + " in " + hashItem[2]);
				try {
					//sv.sendMessage(out);
					main.getService().sendMessage(out);
				} catch (TimeoutException | GatewayException | IOException
						| InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if(hashItem[0].equalsIgnoreCase("help")){
			System.out.println("seeking help");
			OutboundMessage out = new OutboundMessage(theNumber, "To register, send 'register#your name', \nTo sell a product " +
					"send, 'sell#product name#price#units#location'\n" +
					"To buy, send 'buy#product name#location'.\n\n" +
					"Thank you for using Lipwa POA!");
			try {
				//sv.sendMessage(out);
				main.getService().sendMessage(out);
			} catch (TimeoutException | GatewayException | IOException
					| InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
