package com.skillfactory.pj04.banking;

import com.skillfactory.pj04.client.Client;

import java.time.LocalDateTime;
import java.util.List;

public interface BankingService {


    Client getClient(int id);

    List<Client> readAll();

    double getBalance(int id);
    TransactionStatus  takeMoney (int id, double sum);
    TransactionStatus  putMoney (int id, double sum);

    List<Operation> getOperationList(Integer clientId, LocalDateTime startDate, LocalDateTime endDate);

    TransactionStatus transferMoney (int idFrom,int idTo, double sum);

    void logOperation(Integer fromId, Integer toId, double sum, TransactionStatus status);

}
