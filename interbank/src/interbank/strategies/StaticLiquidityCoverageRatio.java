/**
 * 
 */
package interbank.strategies;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author joeri this class is used by central banks to 
 * impose a fixed liquidity coverage ratio
 */
public class StaticLiquidityCoverageRatio extends AbstractStrategy implements
		MacroPrudentialStrategy {

	private double staticLiquidityCoverageTarget;
	/* 
	 * Compute the liquidity coverage ratio as a fixed target
	 */
	@Override
	public double computePolicyTarget() {
		return staticLiquidityCoverageTarget;
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



}
