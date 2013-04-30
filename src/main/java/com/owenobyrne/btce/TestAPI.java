package com.owenobyrne.btce;

import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.owenobyrne.btce.api.model.Depth;

public class TestAPI {
	static final Logger logger = Logger.getLogger(TestAPI.class.getName());
	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		BTCE btce = (BTCE)context.getBean("BTCE");
		
		try {
			//logger.info("Spent: " + btce.getTradeResult("ask", "34d2b6a5-5683-44e5-94fd-e67bddb69279").getData().getTotal_spent().getValue());
			
			//Info i = btce.getInfo();
			Depth md = btce.getMarketDepth("btc_eur");
			//Quote bidq = mg.getQuote("bid");
			//Quote askq = mg.getQuote("ask");
			
			//logger.info("MtGox portfolio value = " + 
			//		i.getData().getWallets().get("BTC").getBalance().getValue_int().multiply(askq.getData().getAmount())
			//		);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
