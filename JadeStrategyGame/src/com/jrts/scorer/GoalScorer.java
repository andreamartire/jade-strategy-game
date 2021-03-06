package com.jrts.scorer;

import java.util.LinkedList;
import java.util.List;

import com.jrts.agents.MasterAI.Nature;
import com.jrts.common.GoalPriority;
import com.jrts.environment.MasterPerception;

public class GoalScorer {

	public interface Rule {
		double value();
	}

	public static final double MIN_SCORE = 0;
	public static final double MAX_SCORE = 100;

	List<Rule> rules = new LinkedList<GoalScorer.Rule>();

	Nature nature;
	MasterPerception perception;


	public GoalScorer(Nature nature, MasterPerception perception) {
		super();
		this.nature = nature;
		this.perception = perception;
	}

	public void addRule(Rule r) {
		rules.add(r);
	}

	public GoalPriority calculatePriority() {
		double score = 0;
		for (Rule r : rules) {
			double localScore = r.value();
			if(localScore < MIN_SCORE) localScore = 0;
			//if(localScore > MAX_SCORE || Double.isNaN(localScore)) localScore = MAX_SCORE;
			/*
			score += (localScore < MIN_SCORE ? 0 : localScore > MAX_SCORE
					|| Double.isNaN(localScore) ? MAX_SCORE : localScore);
					*/
			score += localScore;
		}
		
		score /= rules.size();

		/*
		if(this.getClass().getName().contains("Att")){
			System.out.println(perception.getTeamDF().getLocalName() + " - ATT : " + score);
			if(perception.getTeamDF().getLocalName().contains("2"))
				System.out.println();
		}
		*/
		if (score < MAX_SCORE / 3)
			return GoalPriority.LOW;
		else if (score < 2 * MAX_SCORE / 3)
			return GoalPriority.MEDIUM;
		
		return GoalPriority.HIGH;
	}

}
