/**
 * 
 */
package domain;

import java.util.Hashtable;
import jade.util.leap.LinkedList;
import view.MainFrame;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

/**
 * @author Sergio Fernandez Garcia
 * @author Eduardo Sanchez Lopez
 *
 */
public class AemetAgent extends Agent {	
	private Hashtable<String,LinkedList> comunidades_table;
	protected MainFrame mf;
	protected Boolean inicio;
	
	@Override
	protected void setup() {
		this.addBehaviour(new ListarComunidades(this));
		mf = new MainFrame(this);
	}
	
	@Override
	protected void takeDown() {
		doDelete();	
	}
	
	public void BuscarInformacionEstaciones(String objetivo) {
		AID id_comunidadAgente = new AID("ComunidadAutonomaAgent", AID.ISLOCALNAME);

		//Pedir información al agente de la Comunidad Autónoma (ComunidadAutonomaAgent)
		ACLMessage msg_peticion = new ACLMessage(ACLMessage.QUERY_IF);
		msg_peticion.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
		msg_peticion.setLanguage("INFO");
		msg_peticion.setContent(objetivo);
		msg_peticion.addReceiver(id_comunidadAgente);
		
		this.addBehaviour(new BuscarInformacion(this, msg_peticion));
		
	}
	
	
	private class ListarComunidades extends OneShotBehaviour{
		
		public ListarComunidades(Agent agente) {
			super(agente);
			comunidades_table = new Hashtable<String, LinkedList>();
		}
		@Override
		public void action() {
			
			AID id_comunidadAgente = new AID("ComunidadAutonomaAgent", AID.ISLOCALNAME);

			//Solicita el listado de las comunidaddes Autónomas  a ComunidadAutonomaAgent.
			ACLMessage msg_comunidades = new ACLMessage(ACLMessage.INFORM);
			msg_comunidades.setLanguage("COMUNIDADES");
			msg_comunidades.addReceiver(id_comunidadAgente);
			msg_comunidades.setSender(getAID());
			
			send(msg_comunidades);
			
			ACLMessage resp_comunidades = blockingReceive();
			if (resp_comunidades != null) {
				try {
					mf.setComunidades((Hashtable<String, String>)resp_comunidades.getContentObject());
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else{
				block();
			}
		}
		
	}
	
	//Comportamiento que solicita información al resto de agentes 
	private class BuscarInformacion extends AchieveREInitiator{
		
		public BuscarInformacion(Agent agente, ACLMessage aclmsg) {
			super(agente, aclmsg);
		}
		
		protected void handleAgree(ACLMessage agree) {
			System.out.println("Se ha aceptado");
		}
		
		protected void handleRefuse(ACLMessage refuse) {
			System.out.println("Se ha rechazado");
		}
		
		protected void handleNotUnderstood(ACLMessage not_understood) {
			System.out.println("No se ha comprendido el mensaje");
		}
		
		protected void handleInform(ACLMessage inform) {
			System.out.println("Información del servicio");
			try {
				comunidades_table = (Hashtable<String, LinkedList>) inform.getContentObject();
				mf.setInfo(comunidades_table);
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		protected void handleFailure(ACLMessage fail) {
			System.out.println("Fallo en la petición");
		}
	}
}
