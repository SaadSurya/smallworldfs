package com.smallworld.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class Transaction {
    // Represent your transaction data here.
    private long mtn;
    private BigDecimal amount;
    private String senderFullName;
    private Integer senderAge;
    private String beneficiaryFullName;
    private Integer beneficiaryAge;
    private List<Issue> issues;

    @Getter
    @Setter
    @Builder
    public static class Issue {
        private int id;
        private boolean solved;
        private String message;
    }
}
