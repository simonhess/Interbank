/*
 * JMAB - Java Macroeconomic Agent Based Modeling Toolkit
 * Copyright (C) 2013 Alessandro Caiani and Antoine Godin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package interbank.agents;

import interbank.StaticValues;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jmab.agents.BondDemander;
import jmab.agents.BondSupplier;
import jmab.agents.LaborDemander;
import jmab.agents.LaborSupplier;
import jmab.agents.LiabilitySupplier;
import jmab.events.MacroTicEvent;
import jmab.goods.Bond;
import jmab.goods.Deposit;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * Note that the government uses a reserve account in the central bank rather than a deposit account due to
 * the bond market.
 */
/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class GovernmentAntiCyclical extends Government implements LaborDemander, BondSupplier{
	
	double unemploymentBenefit;
	protected double doleExpenditure;
	protected double profitsFromCB;
	


	

	/**
	 * @return the unemploymentBenefit
	 */
	public double getUnemploymentBenefit() {
		return unemploymentBenefit;
	}

	/**
	 * @param unemploymentBenefit the unemploymentBenefit to set
	 */
	public void setUnemploymentBenefit(double unemploymentBenefit) {
		this.unemploymentBenefit = unemploymentBenefit;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.SimpleAbstractAgent#onTicArrived(AgentTicEvent)
	 */
	@Override
	protected void onTicArrived(MacroTicEvent event) {
		switch(event.getTic()){
		case StaticValues.TIC_GOVERNMENTLABOR:
			computeLaborDemand();
			break;
		case StaticValues.TIC_TAXES:
			collectTaxes(event.getSimulationController());
			break;
		case StaticValues.TIC_BONDINTERESTS:
			payInterests();
			break;
		case StaticValues.TIC_BONDSUPPLY:
			receiveCBProfits();
			determineBondsInterestRate();
			emitBonds();
			break;
		case StaticValues.TIC_WAGEPAYMENT:
			payWages();
			payUnemploymentBenefits(event.getSimulationController());
			break;
		case StaticValues.TIC_UPDATEEXPECTATIONS:
			this.updateAggregateVariables();
			break;
		}
	}

	/**
	 * 
	 */
	private void receiveCBProfits() {
		Item deposit=this.getItemStockMatrix(true, StaticValues.SM_RESERVES);
		CentralBank cb=(CentralBank) deposit.getLiabilityHolder();
		deposit.setValue(deposit.getValue()+cb.getCBProfits());
		profitsFromCB=cb.getCBProfits();
	}

	/**
	 * Pay interest and coupon on bonds using deposits until depleted, after that reserves
	 */
	protected void payInterests() {
		List<Item> bonds=this.getItemsStockMatrix(false, StaticValues.SM_BONDS);
		Item reserves=this.getItemStockMatrix(true, StaticValues.SM_RESERVES);
		double interestsBonds=0;
		for(Item b:bonds){
			Bond bond=(Bond)b;
			if(bond.getAssetHolder() instanceof Government){
				//This is due to bad rounding
				bond.setQuantity(0);
			}else{
				// determine amount of interest to be payed
				BondDemander holder = (BondDemander)bond.getAssetHolder();
				holder.setBondInterestsReceived(bond.getValue()*bond.getInterestRate());
				interestsBonds+=bond.getValue()*bond.getInterestRate();
				// pay the central bank in reserves
				if(holder instanceof CentralBank){
					reserves.setValue(reserves.getValue()-bond.getValue()*bond.getInterestRate());
					if(bond.getAge()==bond.getMaturity()){
						reserves.setValue(reserves.getValue()-bond.getValue());
						bond.setQuantity(0);
					}
				}
				// pay banks in its own deposits first in then in reserves
				else if(holder instanceof Bank){
					Item deposit = getItemStockMatrix(true, StaticValues.SM_DEP, holder);
					double repaymentBonds = 0; 
					if(bond.getAge()==bond.getMaturity()) {
						repaymentBonds = bond.getValue();
						// set bond quantity at 0 knowing that it will be repaid. 
						bond.setQuantity(0);
					}
					interestsBonds+=bond.getValue()*bond.getInterestRate();
					double paymentDue = repaymentBonds + interestsBonds; 
					double value = deposit.getValue();
					// try to pay using deposits
					if (paymentDue > 0) {
						if (value <= paymentDue) {
							// pay as much as possible using the deposit account at the bondholding bank
							deposit.setValue(deposit.getValue()- value);
							paymentDue -= value;
						}else {
							// if the deposit is sufficiently large pay the duePayment in total
							deposit.setValue(deposit.getValue()- paymentDue); 
							paymentDue = 0;
						}
					// if payments on bonds are not fully made using deposits, use reserves
					}if (paymentDue > 0) {
						Item hDep = holder.getItemStockMatrix(true, StaticValues.SM_RESERVES);
						((LiabilitySupplier) hDep.getLiabilityHolder()).transfer(reserves, hDep, paymentDue);
						paymentDue = 0;
					}
				}
			}
		}
		totInterestsBonds=interestsBonds;
	}
	
	/**
	 * Pay unemployment benefits to unemployed households. First use deposit accounts 
	 * if there are not enough deposits use reserves. 
	 */
	private void payUnemploymentBenefits(SimulationController simulationController) {
		MacroPopulation macroPop = (MacroPopulation) simulationController.getPopulation();
		Population households= (Population) macroPop.getPopulation(StaticValues.HOUSEHOLDS_ID);
		double averageWage=0;
		double employed=0;
		for(Agent agent:households.getAgents()){
			Households worker= (Households) agent;
			if (worker.getEmployer()!=null){
				averageWage+=worker.getWage();
				employed+=1;
			}
		}
		averageWage=averageWage/employed;
		double unemploymentBenefit=averageWage*this.unemploymentBenefit;
		double doleAmount=0;
		List<Item> deposits = this.getItemsStockMatrix(true, StaticValues.SM_DEP);
		for(Agent agent:households.getAgents()){
			Households worker= (Households) agent;
			if (worker.getEmployer()==null){
				LaborSupplier unemployed = (LaborSupplier) worker;
				Item payableStock = unemployed.getPayableStock(StaticValues.MKT_LABOR);
				List<Item> remainingDeposits = new ArrayList<Item>();
				for (Item dep:deposits) {
					double value = dep.getValue();
					if (unemploymentBenefit > 0) {
						// if there is insufficient amount in the deposit account to pay the bill
						if (value <= unemploymentBenefit) {
							// pay as much as possible
							LiabilitySupplier payingSupplier = (LiabilitySupplier) dep.getLiabilityHolder();
							payingSupplier.transfer(dep, payableStock, value);
							unemploymentBenefit -= value;
						}
						else {
							// if the deposit is sufficiently large pay the unemploymentBenefitbill in total
							LiabilitySupplier payingSupplier = (LiabilitySupplier) dep.getLiabilityHolder();
							payingSupplier.transfer(dep, payableStock, unemploymentBenefit);
							unemploymentBenefit = 0;
							if(dep.getValue()>0){
								remainingDeposits.add(dep);
							}
						}
					}	
				}
				deposits = remainingDeposits;
				//If there are insufficient deposits pay using reserves
				if (unemploymentBenefit > 0) {
					Deposit depositGov = (Deposit) this.getItemStockMatrix(true, StaticValues.SM_RESERVES);
					LiabilitySupplier payingSupplier = (LiabilitySupplier) depositGov.getLiabilityHolder();
					payingSupplier.transfer(depositGov, payableStock, unemploymentBenefit);
					doleAmount+=unemploymentBenefit;
				}	
			}
		}
		this.doleExpenditure=doleAmount;
	}

	/**
	 * Sets the labor demand equal to the fixed labor demand
	 */
	@Override
	protected void computeLaborDemand() {
		int currentWorkers = this.employees.size();
		Collections.shuffle(employees);
		for(int i=0;i<this.turnoverLabor*currentWorkers;i++){
			fireAgent(employees.get(i));
		}
		cleanEmployeeList();
		currentWorkers = this.employees.size();
		int nbWorkers = this.fixedLaborDemand;
		if(nbWorkers>currentWorkers){
			this.setActive(true, StaticValues.MKT_LABOR);
			this.laborDemand=nbWorkers-currentWorkers;
		}else{
			this.setActive(false, StaticValues.MKT_LABOR);
			this.laborDemand=0;
			Collections.shuffle(this.employees);
			for(int i=0;i<currentWorkers-nbWorkers;i++){
				fireAgent(employees.get(i));
			}
		}
		cleanEmployeeList();	
	}
	
	/** 
	 * This class uses this paywages function instead of the one from government mother class
	 */
	protected void payWages(){
		// if there are any wages to pay
		if(employees.size()>0){
			// Get wagebill 
			// double wageBillStep1 = this.getWageBill();						UNUSED!!
			// get accounts to pay from, list of deposits + reserves
			List<Item> deposits = this.getItemsStockMatrix(true, StaticValues.SM_DEP);
			// pay the employees 
			Collections.shuffle(this.employees);
			for(int i=0;i<employees.size();i++){
				LaborSupplier employee = (LaborSupplier) employees.get(i);
				Item payableStock = employee.getPayableStock(StaticValues.MKT_LABOR);
				double wage = employee.getWage();
				// loop through the deposits to reduce government deposit accounts in wages
				List<Item> remainingDeposits = new ArrayList<Item>();
				for (Item dep:deposits) {
					double value = dep.getValue();
					if (wage > 0) {
						// if there is insufficient amount in the deposit account to pay the bill
						if (value <= wage) {
							// pay as much as possible
							LiabilitySupplier payingSupplier = (LiabilitySupplier) dep.getLiabilityHolder();
							payingSupplier.transfer(dep, payableStock, value);
							wage -= value;
						}
						else {
							// if the deposit is sufficiently large pay the wagebill in total
							LiabilitySupplier payingSupplier = (LiabilitySupplier) dep.getLiabilityHolder();
							payingSupplier.transfer(dep, payableStock, wage);
							wage = 0;
							if(dep.getValue()>0){
								remainingDeposits.add(dep);
							}
						}
					}	
				}
				deposits=remainingDeposits;
				
				// if not all wages are payed out of deposits pay the remained out of reserves
				if (wage > 0) {
					Deposit reserves = (Deposit) this.getItemStockMatrix(true, StaticValues.SM_RESERVES);
					LiabilitySupplier payingSupplier = (LiabilitySupplier) reserves.getLiabilityHolder();
					payingSupplier.transfer(reserves, payableStock, wage);
					wage = 0;
				}
			}	
		}
	}

	/**
	 * @return the doleExpenditure
	 */
	public double getDoleExpenditure() {
		return doleExpenditure;
	}

	/**
	 * @param doleExpenditure the doleExpenditure to set
	 */
	public void setDoleExpenditure(double doleExpenditure) {
		this.doleExpenditure = doleExpenditure;
	}


	/**
	 * @return the profitsFromCB
	 */
	public double getProfitsFromCB() {
		return profitsFromCB;
	}

	/**
	 * @param profitsFromCB the profitsFromCB to set
	 */
	public void setProfitsFromCB(double profitsFromCB) {
		this.profitsFromCB = profitsFromCB;
	}	

	
	
	/**
	 * Populates the agent characteristics using the byte array content. The structure is as follows:
	 * [sizeMacroAgentStructure][MacroAgentStructure][bondPrice][bondInterestRate][turnoverLabor][unemploymentBenefit][laborDemand]
	 * [fixedLaborDemand][bondMaturity][sizeTaxedPop][taxedPopulations][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public void populateAgent(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		byte[] macroBytes = new byte[buf.getInt()];
		buf.get(macroBytes);
		super.populateCharacteristics(macroBytes, pop);
		bondPrice = buf.getDouble();
		bondInterestRate = buf.getDouble();
		turnoverLabor = buf.getDouble();
		unemploymentBenefit = buf.getDouble();
		laborDemand = buf.getInt();
		fixedLaborDemand = buf.getInt();
		bondMaturity = buf.getInt();
		int lengthTaxedPopulatiobns = buf.getInt();
		taxedPopulations = new int[lengthTaxedPopulatiobns];
		for(int i = 0 ; i < lengthTaxedPopulatiobns ; i++){
			taxedPopulations[i] = buf.getInt();
		}
		int matSize = buf.getInt();
		if(matSize>0){
			byte[] smBytes = new byte[matSize];
			buf.get(smBytes);
			this.populateStockMatrixBytes(smBytes, pop);
		}
		int expSize = buf.getInt();
		if(expSize>0){
			byte[] expBytes = new byte[expSize];
			buf.get(expBytes);
			this.populateExpectationsBytes(expBytes);
		}
		int lagSize = buf.getInt();
		if(lagSize>0){
			byte[] lagBytes = new byte[lagSize];
			buf.get(lagBytes);
			this.populatePassedValuesBytes(lagBytes);
		}
		int stratSize = buf.getInt();
		if(stratSize>0){
			byte[] stratBytes = new byte[stratSize];
			buf.get(stratBytes);
			this.populateStrategies(stratBytes, pop);
		}
	}
	
	/**
	 * protected ArrayList<MacroAgent> employees;
	protected UnemploymentRateComputer uComputer; 
	 * Generates the byte array containing all relevant informations regarding the household agent. The structure is as follows:
	 * [sizeMacroAgentStructure][MacroAgentStructure][bondPrice][bondInterestRate][turnoverLabor][unemploymentBenefit][laborDemand]
	 * [fixedLaborDemand][bondMaturity][sizeTaxedPop][taxedPopulations][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public byte[] getBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] charBytes = super.getAgentCharacteristicsBytes();
			out.write(ByteBuffer.allocate(4).putInt(charBytes.length).array());
			out.write(charBytes);
			ByteBuffer buf = ByteBuffer.allocate(48+4*taxedPopulations.length);
			buf.putDouble(bondPrice);
			buf.putDouble(bondInterestRate);
			buf.putDouble(turnoverLabor);
			buf.putDouble(unemploymentBenefit);
			buf.putInt(laborDemand);
			buf.putInt(fixedLaborDemand);
			buf.putInt(bondMaturity);
			buf.putInt(taxedPopulations.length);
			for(int i = 0 ; i < taxedPopulations.length ; i++){
				buf.putInt(taxedPopulations[i]);
			}
			out.write(buf.array());
			byte[] smBytes = super.getStockMatrixBytes();
			out.write(ByteBuffer.allocate(4).putInt(smBytes.length).array());
			out.write(smBytes);
			byte[] expBytes = super.getExpectationsBytes();
			out.write(ByteBuffer.allocate(4).putInt(expBytes.length).array());
			out.write(expBytes);
			byte[] passedValBytes = super.getPassedValuesBytes();
			out.write(ByteBuffer.allocate(4).putInt(passedValBytes.length).array());
			out.write(passedValBytes);
			byte[] stratsBytes = super.getStrategiesBytes();
			out.write(ByteBuffer.allocate(4).putInt(stratsBytes.length).array());
			out.write(stratsBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
}
