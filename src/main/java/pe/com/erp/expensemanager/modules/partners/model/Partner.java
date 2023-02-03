package pe.com.erp.expensemanager.modules.partners.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private StatusInvitationsPartner statusRequest;

    @Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="DATE_SEND_REQUEST")
    private Date dateSendRequest;

    @Temporal(value=TemporalType.TIMESTAMP)
    @Column(name="DATE_RECEIVED_RESPONSE")
    private Date dateReceivedResponse;

    private Long ownerId;



}
