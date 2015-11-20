package interbank.strategies;

import java.util.ArrayList;

import jmab.agents.CreditDemander;
import jmab.agents.CreditSupplier;
import jmab.population.MacroPopulation;
import jmab.strategies.SelectLenderStrategy;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort
 * This class contains the methods required to select a cheapest lender on the interbank market
 */
public class CheapestBankLender extends AbstractStrategy implements
		SelectLenderStrategy {

	private int loansId;
	
	/**
	 * This is the main method used to select the cheapest interbank lender
	 */
	@Override
	public Agent selectLender(ArrayList<Agent> lenders, double amount,
			int length) {
		// TODO this is now just the same as for cheapest lender. I'm not sure what needs to change for interbank. 
		double minRate=Double.POSITIVE_INFINITY;
		CreditSupplier minLender=(CreditSupplier) lenders.get(0);
		for(Agent lender : lenders){
			double tempRate=((CreditSupplier)lender).getInterestRate(loansId, (CreditDemander)agent,amount, length);
			if(tempRate<minRate){
				minRate=tempRate;
				minLender=(CreditSupplier)lender;
			}
		}
		return minLender;
	}
	
	public CheapestBankLender() {
	}

	public CheapestBankLender(Agent agent) {
		super(agent);
	}

	public CheapestBankLender(EventScheduler scheduler, Agent agent) {
		super(scheduler, agent);
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		// TODO Auto-generated method stub

	}

	public int getLoansId() {
		return loansId;
	}

	public void setLoansId(int loansId) {
		this.loansId = loansId;
	}
	
}
