package interbank.strategies;

import jmab.strategies.SingleStrategy;

/**
 * @author Joeri Schasfoort, Alessandro Caiani and Antoine Godin
 *
 */
public interface MonetaryPolicyStrategy extends SingleStrategy {
	
	public double computeAdvancesRate();
	
}
