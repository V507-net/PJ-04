package com.skillfactory.pj04.banking;

import com.skillfactory.pj04.client.Client;
import com.skillfactory.pj04.repository.ClientRepository;
import com.skillfactory.pj04.repository.OperationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class BankingServiceImpl implements BankingService {

    private final ClientRepository clientRepository;
    private final OperationRepository operationRepository;

    public BankingServiceImpl(ClientRepository clientRepository, OperationRepository operationRepository)
    {
        this.clientRepository = clientRepository;
        this.operationRepository = operationRepository;
    }

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

                        logOperation(client.id, null, sum, TransactionStatus.WITHDRAW);

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

                logOperation(null, client.id, sum, TransactionStatus.DEPOSIT);

                return TransactionStatus.SUCCESS;
            } else {
                return TransactionStatus.CLIENT_NOT_FOUND;
            }
        }).orElse(TransactionStatus.UNKNOWN_ERROR);
    }


    @Override
    @Transactional
    public TransactionStatus transferMoney(int idFrom, int idTo, double sum) {
        if (idFrom == idTo) {
            TransactionStatus status = TransactionStatus.NOT_ALLOWED;
            operationRepository.save(new Operation(idFrom, idTo, sum, status));
            return status;
        }

        TransactionStatus result = clientRepository.findByIdForUpdate(idFrom)
                .map(sender -> {
                    if (sender.balance - sum >= 0) {
                        return clientRepository.findByIdForUpdate(idTo)
                                .map(receiver -> {
                                    sender.balance -= sum;
                                    receiver.balance += sum;
                                    clientRepository.save(sender);
                                    clientRepository.save(receiver);

                                    logOperation(sender.id, receiver.id, sum,TransactionStatus.TRANSFER );

                                    return TransactionStatus.SUCCESS;
                                })
                                .orElse(TransactionStatus.RECEIVER_NOT_FOUND);
                    } else {
                        return TransactionStatus.INSUFFICIENT_FUNDS;
                    }
                })
                .orElse(TransactionStatus.SENDER_NOT_FOUND);



        return result;
    }

    public void logOperation(Integer fromId, Integer toId, double sum, TransactionStatus status) {
        Operation op = new Operation();
        op.setFromId(fromId);
        op.setToId(toId);
        op.setSum(sum);
        op.setTimestamp(LocalDateTime.now());
        op.setStatus(status);
        operationRepository.save(op);
    }

    public List<Operation> getOperationList(Integer clientId, LocalDateTime startDate, LocalDateTime endDate) {
        return operationRepository.findByFilters(clientId, startDate, endDate);
    }

}
