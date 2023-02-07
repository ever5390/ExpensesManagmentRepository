package pe.com.erp.expensemanager.modules.expense.model;

import pe.com.erp.expensemanager.modules.partners.model.Partner;

import javax.persistence.*;

@Entity
@Table(name = "reposition")
public class Reposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String amountToRepo;

    @ManyToOne
    @JoinColumn(name = "partner_to_pay_id")
    private Partner partnerToPay;

    public Partner getPartnerToPay() {
        return partnerToPay;
    }

    public String getAmountToRepo() {
        return amountToRepo;
    }

    public void setAmountToRepo(String amountToRepo) {
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