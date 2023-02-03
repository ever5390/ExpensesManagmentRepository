package pe.com.erp.expensemanager.modules.period.dao;

import pe.com.erp.expensemanager.modules.period.model.Period;

public class PeriodDetailDao {

	private double amountEstimado;
	private double totalSpent;
	private double saving;
	private Period period;
	
	public double getAmountEstimado() {
		return amountEstimado;
	}
	public void setAmountEstimado(double amountEstimado) {
		this.amountEstimado = amountEstimado;
	}
	public double getTotalSpent() {
		return totalSpent;
	}
	public void setTotalSpent(double totalSpent) {
		this.totalSpent = totalSpent;
	}
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	public double getSaving() {
		return this.amountEstimado - this.totalSpent;
	}
	public void setSaving(double saving) {
		this.saving = saving;
	}
	@Override
	public String toString() {
		return "PeriodDetailDao [amountEstimado=" + amountEstimado + ", totalSpent=" + totalSpent + ", saving=" + saving
				+ ", period=" + period + "]";
	}
	

	
}
