package interbank.strategies;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort
 * This class lets the central bank set a fixed interest rate
 */
public class StaticMonetary extends AbstractStrategy implements
		MonetaryPolicyStrategy {
	
	private double staticInterestRate;
	
	@Override
	public double computeAdvancesRate() {
		return staticInterestRate;
	}

	public double getstaticInterestRate() {
		return staticInterestRate;
	}

	public void setstaticInterestRate(double staticInterestRate) {
		this.staticInterestRate = staticInterestRate;
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

}
