/**
 * 
 */
package domain;

import java.util.Hashtable;
import jade.util.leap.LinkedList;
import view.MainFrame;

import jade.core.AID;
import jade.core.Agent;
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
	private Hashtable<String,LinkedList> comunidades_table = new Hashtable<String, LinkedList>();
	protected MainFrame mf;
	protected Boolean inicio;
	
	@Override
	protected void setup() {
		//Pedir información al agente de la Comunidad Autónoma (ComunidadAutonomaAgent)
		ACLMessage aclmsg = new ACLMessage(ACLMessage.QUERY_IF);
		aclmsg.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
		//aclmsg.setContent("start");
		aclmsg.setContent("Todo");
		//inicio = true;
		
		aclmsg.addReceiver(new AID("ComunidadAutonomaAgent", AID.ISLOCALNAME));

		this.addBehaviour(new BuscarInformacion(this, aclmsg));
		mf = new MainFrame(this);
	}
	
	@Override
	protected void takeDown() {
		doDelete();
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
