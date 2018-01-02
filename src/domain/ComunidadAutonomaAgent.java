package domain;

import java.io.IOException;
import java.util.Hashtable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
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
	private Hashtable<String,String> comunidades_table;
	
	protected void setup() {
		System.out.println("[Comunidad Autónoma] - Servicio Activo");
		comunidades_table = new Hashtable<String, String>();
		
		MessageTemplate plantillaAEMET = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_QUERY);
		
		this.addBehaviour(new ListarComunidades(this));
		this.addBehaviour(new AtenderSolicitudes(this, plantillaAEMET));
	}
	
	
	//Comportamiento para listar Comunidades Autónomas
	private class ListarComunidades extends CyclicBehaviour{
		
		MessageTemplate templateListar = null;
		
		public ListarComunidades(Agent agent){
			super(agent);
			MessageTemplate filtroSender = MessageTemplate.MatchSender(new AID("AemetAgent", AID.ISLOCALNAME));
			MessageTemplate filtroPerfomative = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate filtroLanguage = MessageTemplate.MatchLanguage("COMUNIDADES");
			
			templateListar = MessageTemplate.and(filtroSender, filtroLanguage);
			templateListar = MessageTemplate.and(templateListar, filtroPerfomative);
		}
		@Override
		public void action() {
			ACLMessage msg = receive(templateListar);
			if(msg != null) {
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
				ACLMessage res = msg.createReply();
				try {
					res.setContentObject(comunidades_table);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				send(res);
				
			}
			else{
				block();
			}
			
		}
	}
	
	
	//Atendemos las peticiones de AEMETAgent.
	private class AtenderSolicitudes extends AchieveREResponder{
		Hashtable<String,LinkedList> comunidades_stations_table;
		
		public AtenderSolicitudes(Agent agent, MessageTemplate plantilla) {
			super(agent, plantilla);
		}
		
		protected ACLMessage handleRequest(ACLMessage request) {
			String solicitud = request.getContent();
			ACLMessage agree = request.createReply();
						
			if(comunidades_table.isEmpty() || solicitud.equals("all") || comunidades_table.containsKey(solicitud)) { 
				agree.setPerformative(ACLMessage.AGREE);
			} else {
				agree.setPerformative(ACLMessage.REFUSE);
			}
			return agree;
		}
		
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
			String filtro = request.getContent();
			ACLMessage inform = request.createReply();
			try {
				comunidades_stations_table = aemetAgent(filtro);
				if(comunidades_stations_table != null){
					inform.setContentObject(comunidades_stations_table);
					inform.setPerformative(ACLMessage.INFORM);
				}else {
					inform.setPerformative(ACLMessage.FAILURE);
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
		private Hashtable<String, LinkedList> aemetAgent(String filtro) {
			
			Hashtable<String, String>comunidades_table = new Hashtable<String, String>();
			Hashtable<String, LinkedList>comunidades_stations_table = new Hashtable<String, LinkedList>();
			System.out.println("\\nAGENTE AEMET\n---------------------");
			try{
				Document doc = Jsoup.connect(AEMET + ID_CC_AA_DIR).get();
				
			
				Elements form = doc.select("form[name=\"frm1\"]");
				Elements comunidades = form.select("option:not(option[selected])");
				for(Element comunidad: comunidades ) {
					if(filtro.equals("all") || filtro.equals(comunidad.attr("value")))
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
											"&l=" + localidad_key + "&w=0&datos=det&x=h24&f=temperatura";
						String localidad = provincias_table.get(provincia).get(localidad_key);
						System.out.println(comunidad_key + ", " + provincia + ", " + localidad);
						System.out.println(url_enclave);
						Location loc =  new Location();
						WeatherInformation w_info = new WeatherInformation(); 
						
						//Solicitud de la informacion referente a la estacion.
						ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
						aclmsg.addReceiver(new AID("EnclaveAgent", AID.ISLOCALNAME));
						aclmsg.setContent(url_enclave);
						send(aclmsg);
						

						ACLMessage msgloc = blockingReceive();
						if(msgloc!= null) {
							LinkedList empaquetado = new LinkedList();
							
							empaquetado = (LinkedList)msgloc.getContentObject();
							
							loc = (Location)empaquetado.get(0);
							w_info = (WeatherInformation) empaquetado.get(1);
							
						}
						else {
							block();
						}
						//---------------------------------------------------
						System.out.println("\t Lat: " + loc.getLatitude() + " / Long: "+ loc.getLongitude());
						weatherStation_list.add(new WeatherStation(comunidad_key, comunidad_name, provincia, localidad_key, localidad, loc, w_info));
					}
				}
					
			}catch(Exception e){
				System.out.println("Exception "+e);
			}
			
			return weatherStation_list;	
		}
	}

}
