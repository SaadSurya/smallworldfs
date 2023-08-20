package com.smallworld;

import com.smallworld.data.Transaction;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TransactionDataFetcher {

    private final TransactionDataSource transactionDataSource;

    public TransactionDataFetcher() {
        transactionDataSource = new TransactionDataSource();
    }

    /**
     * Returns the sum of the amounts of all transactions
     */
    public BigDecimal getTotalTransactionAmount() {
        var transactions = transactionDataSource.getTransactions();
        return transactions.values()
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    public BigDecimal getTotalTransactionAmountSentBy(String senderFullName) {
        if (senderFullName == null) {
            return BigDecimal.ZERO;
        }
        var transactions = transactionDataSource.getTransactions();
        return transactions.values()
                .stream()
                .filter(x -> senderFullName.equals(x.getSenderFullName()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns the highest transaction amount
     */
    public BigDecimal getMaxTransactionAmount() {
        var transactions = transactionDataSource.getTransactions();
        var optionalMax = transactions.values()
                .stream()
                .map(Transaction::getAmount)
                .max(BigDecimal::compareTo);
        return optionalMax.isEmpty() ? BigDecimal.ZERO : optionalMax.get();
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    public long countUniqueClients() {
        var transactions = transactionDataSource.getTransactions();
        var distinctClients = transactions.values()
                .stream()
                .map(Transaction::getSenderFullName)
                .collect(Collectors.toSet());
        distinctClients.addAll(transactions.values()
                        .stream()
                        .map(Transaction::getBeneficiaryFullName)
                        .collect(Collectors.toSet()));
        return distinctClients.size();
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    public boolean hasOpenComplianceIssues(String clientFullName) {
        if (clientFullName == null) {
            return false;
        }
        var transactions = transactionDataSource.getTransactions();
        return transactions.values()
                .stream()
                .filter(x -> clientFullName.equals(x.getSenderFullName()) || clientFullName.equals(x.getBeneficiaryFullName()))
                .anyMatch(x -> x.getIssues().stream().anyMatch(i -> !i.isSolved()));
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    public Map<String, List<Transaction>> getTransactionsByBeneficiaryName() {
        var transactions = transactionDataSource.getTransactions();
        var indexed = new HashMap<String, List<Transaction>>();
        transactions.values()
                .forEach(x -> indexed.merge(
                        x.getBeneficiaryFullName()
                        , List.of(x)
                        , (r, v) -> {
                            r.addAll(v);
                            return r;
                        }
                ));
        return indexed;
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    public Set<Integer> getUnsolvedIssueIds() {
        var transactions = transactionDataSource.getTransactions();
        return transactions.values()
                .stream()
                .flatMap(x -> x.getIssues().stream().filter(i -> !i.isSolved()).map(Transaction.Issue::getId))
                .collect(Collectors.toSet());
    }

    /**
     * Returns a list of all solved issue messages
     */
    public List<String> getAllSolvedIssueMessages() {
        var transactions = transactionDataSource.getTransactions();
        return transactions.values()
                .stream()
                .flatMap(x -> x.getIssues()
                        .stream()
                        .filter(Transaction.Issue::isSolved)
                        .map(Transaction.Issue::getMessage))
                .toList();
    }

    /**
     * Returns the 3 transactions with the highest amount sorted by amount descending
     */
    public List<Transaction> getTop3TransactionsByAmount() {
        var transactions = transactionDataSource.getTransactions();
        return transactions.values()
                .stream()
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .limit(3)
                .toList();
    }

    /**
     * Returns the senderFullName of the sender with the most total sent amount
     */
    public Optional<String> getTopSender() {
        String topSender = null;
        var maxSent = BigDecimal.ZERO;
        var senderMap = new HashMap<String, BigDecimal>();
        var transactions = transactionDataSource.getTransactions();
        for (var transaction : transactions.values()) {
            var totalSent = senderMap.merge(transaction.getSenderFullName(), transaction.getAmount(), BigDecimal::add);
            if (totalSent.compareTo(maxSent) > 0) {
                maxSent = totalSent;
                topSender = transaction.getSenderFullName();
            }
        }
        return Optional.ofNullable(topSender);
    }

}
