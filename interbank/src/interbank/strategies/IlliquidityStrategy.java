/**
 * 
 */
package interbank.strategies;

import jmab.strategies.SingleStrategy;

/**
 * @author Joeri Schasfoort
 *
 */
public interface IlliquidityStrategy extends SingleStrategy {
	
	public void illiquid();
}
