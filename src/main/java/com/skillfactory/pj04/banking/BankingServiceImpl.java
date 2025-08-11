package com.skillfactory.pj04.banking;

import com.skillfactory.pj04.client.Client;
import com.skillfactory.pj04.database.OperationLog;
import com.skillfactory.pj04.repository.ClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankingServiceImpl implements BankingService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<Client>  readAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client getClient(int id) {
        return clientRepository.findById(id).get();
    }

    @Override
    public double getBalance(int id) {
        return clientRepository.findById(id)
                .map(client -> client.balance)
                .orElse(Double.NaN);
    }

    @Override
    @Transactional
    public TransactionStatus  takeMoney(int id, double sum) {
        return clientRepository.findByIdForUpdate(id)
                .map(client -> {
                    if (client.balance - sum >= 0) {
                        client.balance -= sum;
                        clientRepository.save(client);
                        return TransactionStatus.SUCCESS;
                    } else {
                        return TransactionStatus.INSUFFICIENT_FUNDS;
                    }
                })
                .orElse(TransactionStatus.CLIENT_NOT_FOUND);
    }

    @Override
    @Transactional
    public TransactionStatus putMoney(int id, double sum) {
        return clientRepository.findByIdForUpdate(id)
                .map(client -> {
            if (client.balance + sum >= 0) {
                client.balance += sum;
                clientRepository.save(client); // сохраняем
                return TransactionStatus.SUCCESS;
            } else {
                return TransactionStatus.CLIENT_NOT_FOUND;
            }
        }).orElse(TransactionStatus.UNKNOWN_ERROR);
    }
    @Override
    public OperationLog getOperationList(int id, String dateStart, String dateEnd) {
        return null;
    }

    @Override
    @Transactional
    public TransactionStatus transferMoney(int idFrom, int idTo, double sum) {
        if (idFrom == idTo) {
            return TransactionStatus.BAD_ASS;
        }

        return clientRepository.findByIdForUpdate(idFrom)
                .map(sender -> {
                    if (sender.balance - sum >= 0) {
                        return clientRepository.findByIdForUpdate(idTo)
                                .map(receiver -> {
                                    sender.balance -= sum;
                                    receiver.balance += sum;
                                    clientRepository.save(sender);
                                    clientRepository.save(receiver);
                                    return TransactionStatus.SUCCESS;
                                })
                                .orElse(TransactionStatus.RECEIVER_NOT_FOUND);
                    } else {
                        return TransactionStatus.INSUFFICIENT_FUNDS;
                    }
                })
                .orElse(TransactionStatus.SENDER_NOT_FOUND);
    }
}
