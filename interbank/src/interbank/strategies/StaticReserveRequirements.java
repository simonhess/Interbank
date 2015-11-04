/**
 * 
 */
package interbank.strategies;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Joeri Schasfoort
 *
 */
@SuppressWarnings("serial")
public class StaticReserveRequirements extends AbstractStrategy implements
		MacroPrudentialStrategy {

	private double staticReserveRequirements;
	/* (non-Javadoc)
	 * @see interbank.strategies.MacroPrudentialStrategy#computePolicyTarget()
	 */
	@Override
	public double computePolicyTarget() {
		return staticReserveRequirements;
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
	 * @return the staticReserveRequirements
	 */
	public double getStaticReserveRequirements() {
		return staticReserveRequirements;
	}

	/**
	 * @param staticReserveRequirements the staticReserveRequirements to set
	 */
	public void setStaticReserveRequirements(double staticReserveRequirements) {
		this.staticReserveRequirements = staticReserveRequirements;
	}



}
