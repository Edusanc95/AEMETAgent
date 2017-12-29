package domain;

import java.io.IOException;
import java.util.Hashtable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.util.leap.LinkedList;
import view.MainFrame;

public class ComunidadAutonomaAgent extends Agent {
	
	private static final String AEMET = "http://www.aemet.es";
	private static final String ID_CC_AA_DIR = "/es/eltiempo/prediccion/espana";
	private static final String ULTIMOS_DATOS = "/es/eltiempo/observacion/ultimosdatos";
	
	protected void setup() {
		System.out.println("[Comunidad Autónoma] - Servicio Activo");
		
		MessageTemplate plantillaAEMET = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_QUERY);
		
		this.addBehaviour(new AtenderSolicitudes(this, plantillaAEMET));
	}
	
	
	//Atendemos las peticiones de AEMETAgent.
	private class AtenderSolicitudes extends AchieveREResponder{
		Hashtable<String,LinkedList> comunidades_table;
		private Boolean inicio = false;
		public AtenderSolicitudes(Agent agent, MessageTemplate plantilla) {
			super(agent, plantilla);
			comunidades_table = new Hashtable<String, LinkedList>();
		}
		
		protected ACLMessage handleRequest(ACLMessage request) {
			String solicitud = request.getContent();
			ACLMessage agree = request.createReply();
			
			if(solicitud.equals("start")) {
				inicio = true;
			}
			
			if(inicio || solicitud.equals("Todo") || comunidades_table.containsKey(solicitud)) { 
				agree.setPerformative(ACLMessage.AGREE);
			} else {
				agree.setPerformative(ACLMessage.REFUSE);
			}
			return agree;
		}
		
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
			ACLMessage inform = request.createReply();
			Hashtable<String, String> comunidades = new Hashtable<String, String>();
			try {
				if (inicio) {
					comunidades = getComunidades();
					inicio=false;
					
					if(comunidades != null){
						inform.setContentObject(comunidades);
						inform.setPerformative(ACLMessage.INFORM);
					}else {
						inform.setPerformative(ACLMessage.FAILURE);
					}
					
				}else{
					comunidades_table = aemetAgent();
					if(comunidades_table != null){
						inform.setContentObject(comunidades_table);
						inform.setPerformative(ACLMessage.INFORM);
					}else {
						inform.setPerformative(ACLMessage.FAILURE);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return inform;
		}
		
		private Hashtable<String, String> getComunidades() {
			Hashtable<String, String>comunidades_table = new Hashtable<String, String>();
			try {	
				Document doc = Jsoup.connect(AEMET + ID_CC_AA_DIR).get();
				Elements form = doc.select("form[name=\"frm1\"]");
				Elements comunidades = form.select("option:not(option[selected])");
				for(Element comunidad: comunidades ) {
					comunidades_table.put(comunidad.attr("value"), comunidad.text());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return comunidades_table;
		}

		//-----------------------------------------
		private Hashtable<String, LinkedList> aemetAgent() {
			Hashtable<String, String>comunidades_table = new Hashtable<String, String>();
			Hashtable<String, LinkedList>comunidades_stations_table = new Hashtable<String, LinkedList>();
			System.out.println("\\nAGENTE AEMET\n---------------------");
			try{
				Document doc = Jsoup.connect(AEMET + ID_CC_AA_DIR).get();
				
			
				Elements form = doc.select("form[name=\"frm1\"]");
				Elements comunidades = form.select("option:not(option[selected])");
				for(Element comunidad: comunidades ) {
					comunidades_table.put(comunidad.attr("value"), comunidad.text());
				}
			
				LinkedList weatherStation_list = new LinkedList();
				
				for (String comunidad_key : comunidades_table.keySet()) {
					weatherStation_list = comunidadAutonoma(comunidad_key, comunidades_table.get(comunidad_key));
					comunidades_stations_table.put(comunidad_key, weatherStation_list);
				}
				
				return comunidades_stations_table;
					
				
			}catch(Exception e){
				System.out.println("Exception "+e);
				return null;
			}
			
		}
		
		private LinkedList comunidadAutonoma(String comunidad_key, String comunidad_name) {
			String url_enclave = "";
			
			Hashtable<String,String> localidades_table = new Hashtable<String,String>();
			Hashtable<String, Hashtable<String,String>> provincias_table = new Hashtable<String, Hashtable<String,String>>();
			LinkedList weatherStation_list = new LinkedList(); 
		
			String url_comunidad = AEMET + ULTIMOS_DATOS + "?k="+ comunidad_key +"&w=0";
			
			System.out.println("\nAGENTE C.C.A.A.\n---------------------");
			try{
				Document doc = Jsoup.connect(url_comunidad).get();
				
				provincias_table.clear();
				Elements provincias = doc.select("form[name=\"frm1\"] optgroup");
				
				for(Element elem_provincia: provincias) {
					String provincia = elem_provincia.attr("label");
					
					localidades_table.clear();
					Elements localidades = doc.select("form[name=\"frm1\"] optgroup[label=\"" + provincia + "\"] option");
					
					for(Element elem_localidad: localidades) {	
						String id_localidad = elem_localidad.attr("value");
						String localidad = elem_localidad.text();
			
						localidades_table.put(id_localidad, localidad);
					}
					
					provincias_table.put(provincia, localidades_table);
					
					for(String localidad_key : provincias_table.get(provincia).keySet()) {
						url_enclave = AEMET + ULTIMOS_DATOS + "?k="+ comunidad_key +
											"&l=" + localidad_key + "&w=0&datos=img&x=h24&f=Todas";
						String localidad = provincias_table.get(provincia).get(localidad_key);
						System.out.println(comunidad_key + ", " + provincia + ", " + localidad);
						System.out.println(url_enclave);
						Location loc =  new Location();
						
						//Solicitud de la informacion referente a la estacion.
						ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
						aclmsg.addReceiver(new AID("EnclaveAgent", AID.ISLOCALNAME));
						aclmsg.setContent(url_enclave);
						send(aclmsg);
						

						ACLMessage msgloc = blockingReceive();
						if(msgloc!= null) {
							loc = (Location)msgloc.getContentObject();
						}
						else {
							block();
						}
						//---------------------------------------------------
						System.out.println("\t Lat: " + loc.getLatitude() + " / Long: "+ loc.getLongitude());
						weatherStation_list.add(new WeatherStation(comunidad_key, comunidad_name, provincia, localidad_key, localidad, loc));
					}
				}
					
			}catch(Exception e){
				System.out.println("Exception "+e);
			}
			
			return weatherStation_list;	
		}
	}

}
