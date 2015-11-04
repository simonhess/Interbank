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
@SuppressWarnings("serial")
public class StaticLeverageRatioRatio extends AbstractStrategy implements
		MacroPrudentialStrategy {

	private double staticCapitalBufferTarget;
	
	/* (non-Javadoc)
	 * @see interbank.strategies.MacroPrudentialStrategy#computePolicyTarget()
	 */
	@Override
	public double computePolicyTarget() {
		return staticCapitalBufferTarget;
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

	/**
	 * @return the staticCapitalBufferTarget
	 */
	public double getStaticCapitalBufferTarget() {
		return staticCapitalBufferTarget;
	}

	/**
	 * @param staticCapitalBufferTarget the staticCapitalBufferTarget to set
	 */
	public void setStaticCapitalBufferTarget(double staticCapitalBufferTarget) {
		this.staticCapitalBufferTarget = staticCapitalBufferTarget;
	}
	
	

}
