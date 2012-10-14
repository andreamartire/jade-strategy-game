package com.jrts.agents;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.Random;

import com.jrts.O2Ainterfaces.IUnit;
import com.jrts.behaviours.CheckReceivedAttacks;
import com.jrts.behaviours.FollowPathBehaviour;
import com.jrts.behaviours.LookForEnemy;
import com.jrts.behaviours.ReceiveOrders;
import com.jrts.common.GameConfig;
import com.jrts.environment.CellType;
import com.jrts.environment.Direction;
import com.jrts.environment.Perception;
import com.jrts.environment.Position;
import com.jrts.environment.World;
import com.jrts.environment.WorldMap;

@SuppressWarnings("serial")
public abstract class Unit extends JrtsAgent implements IUnit {
	

	private Position position = null;
	private String status;
	private Perception perception;
	private DFAgentDescription agentDescription;
	private ServiceDescription basicService;
	
	int life;
	int speed;
	int forceOfAttack;
	int sight;
	

	public Unit() {
		super();
		registerO2AInterface(IUnit.class, this);
	}

	public Unit(Position position) {
		this();
		this.position = position;
	}

	@Override
	protected void setup() {
		super.setup();
		Object[] args = getArguments();
		if (args != null) {
			setPosition((Position) args[0]);
			setTeam((String) args[1]);
		}
		agentDescription = new DFAgentDescription();
		agentDescription.setName(getAID());
		basicService = new ServiceDescription();
		basicService.setName(getAID().getName());
		basicService.setType(getClass().getName());
		agentDescription.addServices(basicService);
		register(agentDescription, false);
		addBehaviour(new CheckReceivedAttacks(this));
		addBehaviour(new LookForEnemy(this, 2000));
		addBehaviour(new ReceiveOrders(this));
	}
	
	public void goThere(Position p) {
		goThere(p.getRow(), p.getCol());
	}

	public void goThere(int x, int y) {
		System.out.println("go there");
		addBehaviour(new FollowPathBehaviour(this, x, y, GameConfig.UNIT_MOVING_ATTEMPTS));
	}
	
	public AID getMasterAID() {
		return new AID(getTeam(), AID.ISLOCALNAME);
	}

	@Override
	protected void updatePerception() {
		perception = World.getInstance().getPerception(getPosition(), sight);
		//send perception to Master
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setConversationId(Perception.class.getSimpleName());
		msg.addReceiver(getMasterAID());
		try {
			msg.setContentObject(perception);
			send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean move(Direction dir){
		System.out.println("moving");
		return World.getInstance().move(this.position, dir);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	protected void setPosition(Position position) {
		if (this.position == null)
			this.position = position;
	}

	@Override
	public int getLife() {
		return life;
	}

	protected void setLife(int life) {
		if(life>0)
			this.life = life;
		else{
			this.life = 0;
			die();
		}
	}
	
	private void die() {
		World.getInstance().agentDies(getPosition());
		doDelete();
	}

	public void decreaseLife(int damage){
		setLife(getLife() - damage);
	}

	public int getSpeed() {
		return speed;
	}

	protected void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getForceOfAttack() {
		return forceOfAttack;
	}

	protected void setForceOfAttack(int forceOfAttack) {
		this.forceOfAttack = forceOfAttack;
	}

	public int getSight() {
		return sight;
	}

	protected void setSight(int sight) {
		this.sight = sight;
	}

	public void spendTime() {
		try {
			Thread.sleep(GameConfig.DEFAULT_REFRESH_TIME/getSpeed());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void spendRandomTime() {
		Random r = new Random();
		try {
			Thread.sleep(GameConfig.DEFAULT_REFRESH_TIME*r.nextInt(5));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getStatus() {
		return status;
	}
	
	@Override
	public String getId() {
		return getAID().getLocalName();
	}
	
	public void switchStatus(String newStatus) {
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getAID().getName());
		sd.setType(newStatus);
		agentDescription.clearAllServices();
		agentDescription.addServices(basicService);
		agentDescription.addServices(sd);
		// register the description with the DF
		register(agentDescription, true);
		status = newStatus;
	}
	
	public boolean isFriend(String aid) {
		DFAgentDescription desc = new DFAgentDescription();
		desc.setName(new AID(aid, AID.ISLOCALNAME));
		try {
			return DFService.search(this, getTeamDF(), desc).length == 0;
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public Position findNearest(CellType type) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		String id = WorldMap.class.getSimpleName();
		msg.setConversationId(id);
		msg.addReceiver(getMasterAID());
		send(msg);
		ACLMessage response = blockingReceive(MessageTemplate.MatchConversationId(id));
		WorldMap map;
		try {
			map = (WorldMap) response.getContentObject();
			return map.findNearest(getPosition(), type);
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Perception getPerception() {
		return perception;
	}
	
}
