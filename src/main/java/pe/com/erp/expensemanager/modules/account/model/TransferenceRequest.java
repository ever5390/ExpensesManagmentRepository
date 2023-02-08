package pe.com.erp.expensemanager.modules.account.model;

import pe.com.erp.expensemanager.modules.period.model.Period;

import javax.persistence.*;
import java.util.Date;

public class TransferenceRequest {

    private Long id;

    private Account accountOrigin;

    private Account accountDestiny;

    private Double amount;

    private String reason;

    private boolean enabled;

    private TypeTransference typeTransference;

    private Period period;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Account getAccountOrigin() {
        return accountOrigin;
    }

    public void setAccountOrigin(Account accountOrigin) {
        this.accountOrigin = accountOrigin;
    }

    public Account getAccountDestiny() {
        return accountDestiny;
    }

    public void setAccountDestiny(Account accountDestiny) {
        this.accountDestiny = accountDestiny;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TypeTransference getTypeTransference() {
        return typeTransference;
    }

    public void setTypeTransference(TypeTransference typeTransference) {
        this.typeTransference = typeTransference;
    }
}
