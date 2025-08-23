package com.skillfactory.pj04.banking;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "operations")
public class Operation {


    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_id")
    private Integer fromId;

    @Column(name = "to_id")
    private Integer toId;

    @Setter
    @Column(name = "money_sum")
    private double sum;

    @Setter
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Setter
    @Column(name = "operation_datetime")
    private LocalDateTime timestamp;

    public Operation() {}

    public Operation(Integer fromId, Integer toId, double sum, TransactionStatus status) {
        this.fromId = fromId;
        this.toId = toId;
        this.sum = sum;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

}
