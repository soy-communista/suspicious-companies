package uz.sicnt.app.domain.legalentities;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Data
@Entity
@Table(name = "RFMAILS")
public class RfMails {

    @Column(name = "NS10_CODE")
    private short ns10Code;

    @Column(name = "NS11_CODE")
    private short ns11Code;

    @Column(name = "TIN")
    private String tin;

    @Column(name = "NAME")
    private String name;

    @Column(name = "NA1_CODE")
    private short na1Code;

    @Column(name = "NS13_CODE")
    private short ns13Code;

    @Column(name = "MAIL_DATE")
    @Temporal(TemporalType.TIMESTAMP)

    private Date mailDate;
    @Column(name = "MAIL_NA3")

    private String mailNa3;
    @Column(name = "SENDER")

    private String sender;
    @Column(name = "SEND_NS10")

    private Short sendNs10;
    @Column(name = "SEND_NS11")

    private Short sendNs11;
    @Column(name = "SEND_DATE")

    @Temporal(TemporalType.TIMESTAMP)
    private Date sendDate;

    @Column(name = "SEND_NA3")
    private String sendNa3;

    @Column(name = "RECIPIENT")
    private String recipient;

    @Column(name = "ANSWER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date answerDate;

    @Column(name = "ANSWER_NA3")
    private String answerNa3;

    @Column(name = "STATE")
    private short state;

    @Id
    @Column(name = "PKEY")
    private String pkey;

    @Column(name = "STATUS")
    private Short status;

    @Column(name = "OWN_NS10_CODE")
    private Short ownNs10Code;

    @Column(name = "OWN_NS11_CODE")
    private Short ownNs11Code;

    @Column(name = "OWN_SEND_NS10")
    private Short ownSendNs10;

    @Column(name = "OWN_SEND_NS11")
    private Short ownSendNs11;

    @Column(name = "NP1_STATE")
    private Short np1State;

}
