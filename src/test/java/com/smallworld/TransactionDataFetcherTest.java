package com.smallworld;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDataFetcherTest {

    private TransactionDataFetcher transactionDataFetcher;
    @BeforeEach
    public void init() {
        transactionDataFetcher = new TransactionDataFetcher();
    }

    @Test
    void testGetTotalTransactionAmount() {
        var total = transactionDataFetcher.getTotalTransactionAmount();
        assertEquals(0, new BigDecimal("2889.17").compareTo(total));

    }

    @Test
    void testGetTotalTransactionAmountSentBy() {
        var sender = "Tom Shelby";
        var total = transactionDataFetcher.getTotalTransactionAmountSentBy(sender);
        assertEquals(0, new BigDecimal("678.06").compareTo(total));
    }

    @Test
    void testGetMaxTransactionAmount() {
        var max = transactionDataFetcher.getMaxTransactionAmount();
        assertEquals(0, new BigDecimal("985").compareTo(max));
    }

    @Test
    void testCountUniqueClients() {
        var count = transactionDataFetcher.countUniqueClients();
        assertEquals(14, count);
    }

    @Test
    void testHasOpenComplianceIssues() {
        var client = "Tom Shelby";
        var hasOpenIssues = transactionDataFetcher.hasOpenComplianceIssues(client);
        assertTrue(hasOpenIssues);
    }

    @Test
    void testGetTransactionsByBeneficiaryName() {
        var transactions = transactionDataFetcher.getTransactionsByBeneficiaryName();
        assertEquals(10, transactions.size());
        for (var forEachBene : transactions.values()) {
            assertEquals(1, forEachBene.size());
        }
    }

    @Test
    void testGetUnsolvedIssueIds() {
        var issueIds = transactionDataFetcher.getUnsolvedIssueIds();
        assertEquals(5, issueIds.size());
        assertArrayEquals(new Integer[]{1, 3, 99, 54, 15}, issueIds.toArray(Integer[]::new));
    }

    @Test
    void testGetAllSolvedIssueMessages() {
        var messages = transactionDataFetcher.getAllSolvedIssueMessages();
        assertEquals(3, messages.size());
    }

    @Test
    void testGetTop3TransactionsByAmount() {
        var transactions = transactionDataFetcher.getTop3TransactionsByAmount();
        assertEquals(new BigDecimal("985.0"), transactions.get(0).getAmount());
        assertEquals(new BigDecimal("666.0"), transactions.get(1).getAmount());
        assertEquals(new BigDecimal("430.2"), transactions.get(2).getAmount());
    }

    @Test
    void testGetTopSender() {
        var sender = transactionDataFetcher.getTopSender();
        assertTrue(sender.isPresent());
        assertEquals("Arthur Shelby", sender.get());
    }
}
