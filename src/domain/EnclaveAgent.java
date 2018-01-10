package domain;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.util.leap.LinkedList;

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
				WeatherInformation w_info = new WeatherInformation();
				
				String url_enclave = msg_recive.getContent();
				try {
					loc = enclaveInfo(url_enclave);
					w_info = enclaveWeatherInfo(url_enclave);
					
					LinkedList empaquetado = new LinkedList();
					empaquetado.add(loc);
					empaquetado.add(w_info);
					
					ACLMessage msg_out = msg_recive.createReply();
					msg_out.setContentObject(empaquetado);
					
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
		
		private WeatherInformation enclaveWeatherInfo(String url_enclave) {		
			String fecha = null;
			float temperatura_c = 0;
			float velocidad_viento = 0;
			String dir_viento = null;
			String dir_viento_img = null;
			float racha = 0;
			String dir_racha = null;
			String dir_racha_img = null;
			float precipitacion = 0;
			float presion = 0;
			float tendencia = 0;
			float humedad = 0;
			
		
			try{
				Document doc = Jsoup.connect(url_enclave).get();
				
				int row = -1;
				Elements datos = new Elements();
				do {
					row++;
					datos = doc.select("tbody tr:eq("+ row + ") td");
				}while(datos.get(1).text().isEmpty() && row < 23);
				
				if (row == 23) {
					return null;
				}
				fecha = datos.get(0).text();
				temperatura_c = (datos.get(1).text().isEmpty()) ? 0: Float.valueOf(datos.get(1).text());
				velocidad_viento = (datos.get(2).text().isEmpty()) ? 0: Float.valueOf(datos.get(2).text());
				// Dirección e imagen de la dirección del viento se toma después.
				racha = (datos.get(4).text().isEmpty()) ? 0: Float.valueOf(datos.get(4).text());
				//Dirección de la racha e imagen de la dirección de la racha se toma después.
				precipitacion = (datos.get(6).text().isEmpty()) ? 0: Float.valueOf(datos.get(6).text());
				presion = (datos.get(7).text().isEmpty()) ? 0: Float.valueOf(datos.get(7).text());
				tendencia = (datos.get(8).text().isEmpty()) ? 0: Float.valueOf(datos.get(8).text());
				humedad = (datos.get(9).text().isEmpty()) ? 0: Float.valueOf(datos.get(9).text());
				
				Elements imagenes = doc.select("tbody tr:eq("+ row + ") td img");
				int inicio  = 0;
				if (!imagenes.isEmpty()){
					if(!datos.get(3).text().isEmpty()) {
						dir_viento = (imagenes.get(inicio).text().isEmpty())? null : imagenes.get(inicio).attr("title");
						dir_viento_img = (imagenes.get(inicio).text().isEmpty())? null :  "http://www.aemet.es" + imagenes.get(inicio).attr("src");
						inicio++;
					}
					
					if(!datos.get(5).text().isEmpty()) {
						dir_racha = (imagenes.get(inicio).text().isEmpty())? null : imagenes.get(inicio).attr("title");
						dir_racha_img = (imagenes.get(inicio).text().isEmpty())? null :  "http://www.aemet.es" + imagenes.get(inicio).attr("src");
					}
				}
				
			}catch(Exception e){
				System.out.println("EnclaveInfo");
				System.out.println("Exception "+e);
			}
			
			return new WeatherInformation(fecha, temperatura_c, velocidad_viento, dir_viento, dir_viento_img,
						racha, dir_racha, dir_racha_img, precipitacion, presion, tendencia, humedad);
		}
	}
}
