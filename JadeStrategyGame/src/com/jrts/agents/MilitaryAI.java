package com.jrts.agents;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import com.jrts.agents.MasterAI.Nature;
import com.jrts.behaviours.PatrolBehaviour;
import com.jrts.behaviours.UpdateUnitTable;
import com.jrts.common.AgentStatus;
import com.jrts.common.GameConfig;
import com.jrts.common.GoalPriority;
import com.jrts.common.Utils;
import com.jrts.environment.Direction;
import com.jrts.environment.Perception;
import com.jrts.environment.Position;
import com.jrts.environment.World;
import com.jrts.messages.EnemySighting;
import com.jrts.messages.Notification;
import com.jrts.messages.Order;


public class MilitaryAI extends GoalBasedAI {
	private static final long serialVersionUID = 9114684864072759345L;
	
	private Level logLevel = Level.FINE;

	int soldierCounter = 0;
	int lastCityCenterLife = GameConfig.BUILDING_ENERGY;
	
	long lastSoldiersRecall = 0;
	
	@Override
	protected void setup() {
		super.setup();

		addBehaviour(new UpdateUnitTable(this, Soldier.class));
		
		addBehaviour(new TickerBehaviour(this, 2000) {
			private static final long serialVersionUID = 1746608629262055814L;
			@Override
			protected void onTick() {
				requestSoldierCreation();
				managePatroling();
				manageFighting();	
			}
		});

		addBehaviour(new TickerBehaviour(this, 5000) {
			private static final long serialVersionUID = 1746608629262055814L;
			@Override
			protected void onTick() {		
				manageExploring();
			}
		});
	}

	private void manageFighting() {
		if(goalLevels.getAttack() == GoalPriority.HIGH || (goalLevels.getAttack() == GoalPriority.MEDIUM && Utils.random.nextInt(4) == 0)){
			Collection<Position> cityCenterPositions = requestMap().getKnownCityCenters();
			cityCenterPositions.remove(myCityCenter);
			if(cityCenterPositions.size() > 0)
			{
				Position posToAttack = myCityCenter.nearest(cityCenterPositions);
				
				// Creo l'insieme dei soldati da mandare all'attacco
				ArrayList<AID> soldiers = unitTable.getUnitsWithStatus(AgentStatus.FREE);
				if(nature != Nature.DEFENSIVE) 
					soldiers.addAll(unitTable.getUnitsWithStatus(AgentStatus.EXPLORING));
				if(nature == Nature.AGGRESSIVE) 
					soldiers.addAll(unitTable.getUnitsWithStatus(AgentStatus.PATROLING));
				
				// Stabilisco il numero minimo di unit� che deve avere il battaglione
				int minSize = 5;
				if(nature == Nature.DEFENSIVE) minSize = 10;
				else if(nature == Nature.AVERAGE) minSize = 7;
				
				// Se ci sono abbastanza soldati disponibili li mando all'attacco
				if(soldiers.size() >= minSize){// || Utils.random.nextInt(4) == 0){
					for (AID aid : soldiers) {
						Order order = new Order(AgentStatus.GO_FIGHTING);
						order.setPosition(posToAttack);
						giveOrder(aid, order);
					}
				}
			} else // Se non ho ancora trovato un CityCenter provo ad aggiungere un esploratore
				this.addExplorer();
		}
	}
	
	private void managePatroling() {

		if(goalLevels.getDefence().equals(GoalPriority.LOW))
			try { Thread.sleep(Utils.random.nextInt(5000)); } 
			catch (InterruptedException e) {  }
		
		int numPatrolingSoldiers = unitTable.getUnitsWithStatus(AgentStatus.PATROLING).size();
		int neededPatrolingSoldiers = goalLevels.extimatePatrolingUnits();
		
		if(neededPatrolingSoldiers > numPatrolingSoldiers)
			addPatroler();
		else if(neededPatrolingSoldiers < numPatrolingSoldiers)
			giveOrder(unitTable.getAUnitWithStatus(AgentStatus.PATROLING), new Order(AgentStatus.FREE));
	}
	
	private void manageExploring() {

		if(goalLevels.getExploration().equals(GoalPriority.LOW))
			try { Thread.sleep(Utils.random.nextInt(5000)); } 
			catch (InterruptedException e) {  }
		
		int numExploringSoldiers = unitTable.getUnitsWithStatus(AgentStatus.EXPLORING).size();
		int neededExploringSoldiers = goalLevels.extimateExplorationUnits();
		
		if(neededExploringSoldiers > numExploringSoldiers)
			addExplorer();
		else if(neededExploringSoldiers < numExploringSoldiers && Utils.random.nextBoolean())
			giveOrder(unitTable.getAUnitWithStatus(AgentStatus.EXPLORING), new Order(AgentStatus.FREE));
	}
	
	public void requestSoldierCreation()
	{
		if (resourcesContainer.isThereEnoughFood(GameConfig.SOLDIER_FOOD_COST) && 
				resourcesContainer.isThereEnoughWood(GameConfig.SOLDIER_WOOD_COST)) {
						
			DFAgentDescription[] workers = this.getTeamDF().searchByUnitStatus(Worker.class, AgentStatus.FREE);
			if(workers.length > 0)
			{
				logger.log(logLevel, "ABBASTANZA LAVORATORI");
				
				DFAgentDescription worker = workers[0];	
				giveOrder(worker.getName(), new Order(AgentStatus.GO_UPGRADING));
				
				resourcesContainer.removeFood(GameConfig.SOLDIER_FOOD_COST);
				resourcesContainer.removeWood(GameConfig.SOLDIER_WOOD_COST);
			}
		}
	}

	public void trainSoldier()
	{		
		unitFactory.trainUnit(Soldier.class);
		soldierCounter++;
	}
	
	public void addExplorer()
	{
		AID soldier = this.getUnitTable().getAFreeUnit();
		if(soldier != null)
			giveOrder(soldier, new Order(AgentStatus.EXPLORING));
	}
	
	public void addPatroler()
	{
		AID soldier = this.getUnitTable().getAFreeUnit();
		if(soldier != null){
			
			Direction angle = Utils.getMapAnglePosition(myCityCenter);
			
			Order order = new Order(AgentStatus.PATROLING);
			if(angle.equals(Direction.LEFT_UP))
			{
				if(Utils.random.nextBoolean())
					order.setDirection(Direction.RIGHT);
				else
					order.setDirection(Direction.DOWN);
			} else if(angle.equals(Direction.LEFT_DOWN))
			{
				if(Utils.random.nextBoolean())
					order.setDirection(Direction.RIGHT);
				else
					order.setDirection(Direction.UP);
			} else if(angle.equals(Direction.RIGHT_UP)){
				if(Utils.random.nextBoolean())
					order.setDirection(Direction.LEFT);
				else
					order.setDirection(Direction.DOWN);
			} else if(angle.equals(Direction.RIGHT_DOWN)){
				if(Utils.random.nextBoolean())
					order.setDirection(Direction.LEFT);
				else
					order.setDirection(Direction.UP);
			}
			
			switch (Utils.random.nextInt(3)) {
			case 0:
				order.setDistance(PatrolBehaviour.DISTANCE_LITTLE);
				break;
			case 1:
				order.setDistance(PatrolBehaviour.DISTANCE_MEDIUM);
				break;
			case 2:
				order.setDistance(PatrolBehaviour.DISTANCE_BIG);
				break;
			}
			
			giveOrder(soldier, order);
		}
	}
	
	@Override
	protected void updatePerception() {
		Perception cityCenterPerception = World.getInstance().getPerception(myCityCenter, GameConfig.CITY_CENTER_SIGHT);
		sendNotification(Notification.PERCEPTION, cityCenterPerception, getMasterAID());
		
		EnemySighting enemies = lookForEnemies(myCityCenter, GameConfig.CITY_CENTER_SIGHT, cityCenterPerception);
		sendNotification(Notification.ENEMY_SIGHTED, enemies, getMasterAID());
		if (!enemies.isEmpty()) {
			onEnemySighting(enemies);
		}
		
		int cityCenterLife = cityCenterPerception.get(myCityCenter).getEnergy();
		if (cityCenterLife < lastCityCenterLife) {
			lastCityCenterLife = cityCenterLife;
			underAttack();
		}
	}

	private void underAttack() {
		sendNotification(Notification.CITYCENTER_UNDER_ATTACK, myCityCenter, getMasterAID());
		logger.log(logLevel, getTeamName() + " city center under attack!");
		
		long times = GregorianCalendar.getInstance().getTimeInMillis();
		if((times - lastSoldiersRecall) > 5000){
			Collection<AID> soldiers = unitTable.getSoldiers();
			for (AID aid : soldiers) {
				//Order order = new Order(AgentStatus.FREE);
				Order order = new Order(AgentStatus.GO_FIGHTING);
				order.setPosition(myCityCenter.step(Direction.UP));
				giveOrder(aid, order);
			}
			
			this.lastSoldiersRecall = times;
		}
	}
	
	public void onEnemySighting(EnemySighting e) {
		int numSightedSoldiers = e.getSoldierNumber();
		int distance = (int)e.getSightingPosition().distance(myCityCenter);
		ArrayList<AID> freeSoldiers = unitTable.getFreeUnits();
		int numMyFreeSoldier = freeSoldiers.size();
		
		logger.log(logLevel, 
				"\n----------------- Start OnEnemySighting ----------------\n" +
				"EnemySighting received: \n" + 
					e +
				"numSightedSoldiers = "+numSightedSoldiers+"\n"+
				"distance = " + distance +"\n"+
				"numMyFreeSoldier = "+numMyFreeSoldier+"\n");
		
		int x=1, y=1, z=1;
		switch (nature) {
		case AGGRESSIVE:
			x = 3;
			y = 3;
			z = 3;
			break;
		case AVERAGE:
			x = 2;
			y = 2;
			z = 1;
			break;
		case DEFENSIVE:
			x = 1;
			y = 1;
			z = 1;
			break;
		default:
			break;
		}

		//TODO: Un numero che che rappresneti il livelo dovrebbe essere messo altrove, visto che sarà utilizzato spesso
		int defence = 1;
		if(goalLevels.getDefence().equals(GoalPriority.LOW))
			defence = 1;
		else if(goalLevels.getDefence().equals(GoalPriority.MEDIUM))
			defence = 2;
		else if(goalLevels.getDefence().equals(GoalPriority.HIGH))
			defence = 3;
		
		int heuristic = numSightedSoldiers*x - distance*defence/y + numMyFreeSoldier*z;
		logger.log(logLevel, "CALCOLO EURISTICA, VALORE: " + heuristic + "\n" +
				"----------------- End OnEnemySighting ----------------\n");
	}
	
	@Override
	protected void handleNotification(Notification n) {
		super.handleNotification(n);
		if (n.getSubject().equals(Notification.ENEMY_SIGHTED)) {
			EnemySighting e = (EnemySighting) n.getContentObject();
			// forward notification to masterAi
			sendNotification(n.getSubject(), n.getContentObject(), getMasterAID());
			// decide what to do
			onEnemySighting(e);
			
		} else if (n.getSubject().equals(Notification.UNIT_DEATH)) {
			soldierCounter--;
			sendNotification(Notification.UNIT_DEATH, n.getContentObject(), getMasterAID());
			
		} else if (n.getSubject().equals(Notification.READY_TO_BE_UPGRADED)) {
			this.trainSoldier();
			
		} else if (n.getSubject().equals(Notification.UNIT_UNDER_ATTACK)) {
			sendNotification(Notification.UNIT_UNDER_ATTACK, n.getContentObject(), getMasterAID());
		}
	}
	
	@Override
	protected Object handleRequest(String requestSubject) {
		// TODO Auto-generated method stub
		return null;
	}
}
