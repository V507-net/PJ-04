package com.skillfactory.pj04.banking;

import com.skillfactory.pj04.client.Client;
import com.skillfactory.pj04.database.OperationLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface BankingService {


    Client getClient(int id);

    List<Client> readAll();

    double getBalance(int id);
    TransactionStatus  takeMoney (int id, double sum);
    TransactionStatus  putMoney (int id, double sum);

    OperationLog getOperationList(int id, String dateStart, String dateEnd);

    TransactionStatus transferMoney (int idFrom,int idTo, double sum);


}
