/**
 * 
 */
package interbank.strategies;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author joeri
 *
 */
public class StaticDebtToIncomeRatio extends AbstractStrategy implements
		MacroPrudentialStrategy {

	private double DebtToIncomeRatioTarget;
	
	/* (non-Javadoc)
	 * @see interbank.strategies.MacroPrudentialStrategy#computePolicyTarget()
	 */
	@Override
	public double computePolicyTarget() {
		return DebtToIncomeRatioTarget;
	}
	
	/**
	 * 
	 */
	public StaticDebtToIncomeRatio() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param agent
	 */
	public StaticDebtToIncomeRatio(Agent agent) {
		super(agent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param scheduler
	 * @param agent
	 */
	public StaticDebtToIncomeRatio(EventScheduler scheduler, Agent agent) {
		super(scheduler, agent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#populateFromBytes(byte[], jmab.population.MacroPopulation)
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		// TODO Auto-generated method stub

	}

	public double getDebtToIncomeRatioTarget() {
		return DebtToIncomeRatioTarget;
	}

	public void setDebtToIncomeRatioTarget(double debtToIncomeRatioTarget) {
		DebtToIncomeRatioTarget = debtToIncomeRatioTarget;
	}


}
