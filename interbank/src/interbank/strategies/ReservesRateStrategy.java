package interbank.strategies;

import jmab.strategies.SingleStrategy;

public interface ReservesRateStrategy extends SingleStrategy {

	public double computeReservesRate();
}
