package domain;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class EnclaveAgent extends Agent {
		
	protected void setup() {
		System.out.println("[Enclave] - Servicio Activo");
		this.addBehaviour(new AtenderSolicitudes());
		
	}
	//		---------------- [ COMPORTAMIENTO ] ----------------
	// Comportamiento para atender las solicitudes de información
	private class AtenderSolicitudes extends CyclicBehaviour{
		
		@Override
		public void action() {
			ACLMessage msg_recive = receive();
			
			if (msg_recive!= null) {
				Location loc = new Location();
				String url_enclave = msg_recive.getContent();
				try {
					loc = enclaveInfo(url_enclave);
					
					ACLMessage msg_out = msg_recive.createReply();
					msg_out.setContentObject(loc);
					
					send(msg_out);
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}else {
				block();
			}	
		}
		
		private Location enclaveInfo(String url_enclave) {		
			Double latitude = null;
			Double longitude = null;
			
//			System.out.println("\nAGENTE ENCLAVE\n---------------------");
			try{
				Document doc = Jsoup.connect(url_enclave).get();
				
				Elements latitudes = doc.select(".latitude");
				for(Element elem_latitud: latitudes) {
					latitude = Double.parseDouble(elem_latitud.attr("title"));
				}
				
				
				Elements longitudes = doc.select(".longitude");
				for(Element elem_longitudes: longitudes) {
					longitude = Double.parseDouble(elem_longitudes.attr("title"));
				}
				
			}catch(Exception e){
				System.out.println("Exception "+e);
			}
			
			return new Location(latitude,longitude);
		}
	}
}
