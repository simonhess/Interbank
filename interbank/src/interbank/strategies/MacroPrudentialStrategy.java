/**
 * 
 */
package interbank.strategies;

import jmab.strategies.SingleStrategy;

/**
 * @author Joeri Schasfoort
 *
 */
public interface MacroPrudentialStrategy extends SingleStrategy {
	
	public double computePolicyTarget();

}
