package pe.com.erp.expensemanager.modules.transaction.model;

import pe.com.erp.expensemanager.modules.partners.model.Partner;

import javax.persistence.*;

@Entity
@Table(name = "reposition")
public class Reposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Double amountToRepo;

    private Double amountToRepoPayed;

    private boolean pendingPay;

    @ManyToOne
    @JoinColumn(name = "partner_to_pay_id")
    private Partner partnerToPay;

/* @ManyToOne
    @JoinColumn(name="transaction_id", nullable=false)
    private Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
*/
    public Double getAmountToRepoPayed() {
        return amountToRepoPayed;
    }

    public void setAmountToRepoPayed(Double amountToRepoPayed) {
        this.amountToRepoPayed = amountToRepoPayed;
    }

    public boolean isPendingPay() {
        return pendingPay;
    }

    public void setPendingPay(boolean pendingPay) {
        this.pendingPay = pendingPay;
    }

    public Partner getPartnerToPay() {
        return partnerToPay;
    }

    public Double getAmountToRepo() {
        return amountToRepo;
    }

    public void setAmountToRepo(Double amountToRepo) {
        this.amountToRepo = amountToRepo;
    }

    public void setPartnerToPay(Partner partnerToPay) {
        this.partnerToPay = partnerToPay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Reposition{" +
                "id=" + id +
                ", amountToRepo='" + amountToRepo + '\'' +
                ", partnerToPay=" + partnerToPay +
                '}';
    }
}