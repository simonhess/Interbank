/**
 * 
 */
package interbank.strategies;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author joeri
 *
 */
@SuppressWarnings("serial")
public class StaticNetFundingRatio extends AbstractStrategy implements
		MacroPrudentialStrategy {

	private double staticNetFundingRatio;
	
	/* (non-Javadoc)
	 * @see interbank.strategies.MacroPrudentialStrategy#computePolicyTarget()
	 */
	@Override
	public double computePolicyTarget() {
		return staticNetFundingRatio;
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
	 * @return the staticNetFundingRatio
	 */
	public double getStaticNetFundingRatio() {
		return staticNetFundingRatio;
	}

	/**
	 * @param staticNetFundingRatio the staticNetFundingRatio to set
	 */
	public void setStaticNetFundingRatio(double staticNetFundingRatio) {
		this.staticNetFundingRatio = staticNetFundingRatio;
	}



}
