package com.jrts.agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

import java.util.ArrayList;

import com.jrts.O2Ainterfaces.IResourceAI;
import com.jrts.O2Ainterfaces.IUnit;
import com.jrts.common.AgentStatus;
import com.jrts.environment.Position;
import com.jrts.environment.World;

@SuppressWarnings("serial")
public class ResourceAI extends JrtsAgent implements IResourceAI{
	
	int collectedWood;
	int collectedFood;
	
	ArrayList<AID> workersList = new ArrayList<AID>();
	
	public ResourceAI() {
		super();
		registerO2AInterface(IResourceAI.class, this);
	}

	protected void setup(){
		super.setup();
		
		String[] args = (String[]) getArguments();
		if (args != null) {
			setTeam(args[0]);
		}
		else{
			System.out.println("Needs team's name");
			System.exit(1);
		}
		
		createWorker();
		addBehaviour(new WakerBehaviour(this, 5000) {
			@Override
			protected void handleElapsedTimeout() {
				assignWoodcutter();
			}
		});
		
		addBehaviour(new CyclicBehaviour(this){
			
			@Override
			public void action() {
				doWait(1000);
				// send a food/wood update message to the masterAi
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//				System.out.println("Notify resources: " + "food: " + collectedFood + " wood: " + collectedWood);
				msg.setContent("food: " + collectedFood + " wood: " + collectedWood);

				msg.addReceiver(new AID(getTeam(), AID.ISLOCALNAME));

				send(msg);
			}
		});
	}
	
	public AID createWorker(){
		World world = World.getInstance();
		String workerName = getTeam() + "-worker"+workersList.size();
		Position workerPosition = world.neighPosition(world.getBuilding(getTeam()));
		if(workerPosition != null){
			//Instantiate the worker
			// get a container controller for creating new agents
			PlatformController container = getContainerController();
			AgentController agentController;
			try {
				Object[] args = {workerPosition, getTeam(), this};
				agentController = container.createNewAgent(workerName, "com.jrts.agents.Worker", args);
				agentController.start();
				IUnit o2a = agentController.getO2AInterface(IUnit.class);
				World.getInstance().addUnit(workerPosition, workerName, o2a);
				
				// keep the worker's ID on a local list
				
				AID workerAID = new AID(workerName, AID.ISLOCALNAME);
				workersList.add( workerAID );
				return workerAID;
			} catch (ControllerException e) {
				e.printStackTrace();
			}
		}
		else{
			System.out.println(getTeam() + ":Cannot instantiate the worker");
		}
		return null;
	}
	
	public void assignWoodcutter() {
		ServiceDescription sd = new ServiceDescription();
		sd.setType(AgentStatus.FREE);
		DFAgentDescription[] free = search(sd);
		AID worker = (free.length > 0 ? free[0].getName() : createWorker());
		if (worker != null) {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId("order");
			msg.addReceiver(worker);
			msg.setContent(AgentStatus.WOOD_CUTTING);
			send(msg);
		}
	}

	@Override
	protected void updatePerception() {
	}

	@Override
	public void addFood(int newFood) {
		collectedFood += newFood;
	}

	@Override
	public void addWood(int newWood) {
		System.out.println("Ricevo " + newWood + " legna");
		collectedWood += newWood;
		System.out.println("Wood: " + collectedWood);
	}
}
