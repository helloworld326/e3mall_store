package cn.e3mall.search.msessage;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage) message;
			System.out.println(textMessage.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
