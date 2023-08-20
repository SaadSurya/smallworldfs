package com.smallworld;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smallworld.data.Transaction;
import com.smallworld.data.TransactionDTO;
import com.smallworld.exceptions.DomainException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionDataSource {

    private Map<Long, Transaction> transactions;

    public Map<Long, Transaction> getTransactions() {
        if (transactions == null) {
            transactions = loadTransactions();
        }
        return transactions;
    }

    private Map<Long, Transaction> loadTransactions() {
        var trans = new HashMap<Long, Transaction>();
        ObjectMapper mapper = new ObjectMapper();
        var jsonResource = getClass().getClassLoader().getResource("transactions.json");
        List<TransactionDTO> transactionDTOs;
        try {
            transactionDTOs = mapper.readValue(jsonResource, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new DomainException("Failed to load transaction data from JSON file.");
        }
        for (var transactionDTO : transactionDTOs) {
            var transaction = trans.get(transactionDTO.getMtn());
            if (transaction == null) {
                transaction = Transaction.builder()
                        .mtn(transactionDTO.getMtn())
                        .amount(transactionDTO.getAmount())
                        .senderFullName(transactionDTO.getSenderFullName())
                        .senderAge(transactionDTO.getSenderAge())
                        .beneficiaryFullName(transactionDTO.getBeneficiaryFullName())
                        .beneficiaryAge(transactionDTO.getBeneficiaryAge())
                        .issues(new ArrayList<>())
                        .build();
            }
            if (transactionDTO.getIssueId() != null) {
                transaction.getIssues().add(
                        Transaction.Issue.builder()
                                .id(transactionDTO.getIssueId())
                                .solved(transactionDTO.isIssueSolved())
                                .message(transactionDTO.getIssueMessage())
                                .build()
                );
            }
            trans.put(transactionDTO.getMtn(), transaction);
        }
        return trans;
    }
}
