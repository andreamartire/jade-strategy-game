package com.jrts.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import com.jrts.common.GameConfig;

@SuppressWarnings("serial")
public abstract class JrtsAgent extends Agent {

	private String team;

	@Override
	protected void setup() {
		super.setup();
		addBehaviour(new TickerBehaviour(this, GameConfig.PERCEPTION_REFRESH) {

			@Override
			protected void onTick() {
				updatePerception();
			}
		});
	}

	/**
	 * updates the agent perception
	 * 
	 */
	protected abstract void updatePerception();


	public String getTeam() {
		return team;
	}

	/**
	 * gets the AID of the team directory facilitator
	 */
	protected AID getTeamDF() {
		return new AID(getTeam() + "-df", AID.ISLOCALNAME);
	}

	/**
	 * register this agent description to the team directory facilitator
	 * 
	 * @param desc
	 *            the agent description
	 * @param deletePrevious
	 *            deregister any previous description associated to the agent
	 */
	protected void register(DFAgentDescription desc, boolean deletePrevious) {
		try {
			if (deletePrevious)
				DFService.deregister(this, getTeamDF());
			DFService.register(this, getTeamDF(), desc);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected DFAgentDescription[] search(ServiceDescription sd) {
		DFAgentDescription desc = new DFAgentDescription();
		desc.addServices(sd);
		try {
			return DFService.search(this, getTeamDF(), desc);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DFAgentDescription[1];
	}

	protected void setTeam(String team) {
		this.team = team;
	}

}
