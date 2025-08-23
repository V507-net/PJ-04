package com.skillfactory.pj04;

import com.skillfactory.pj04.banking.BankingService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Startup {

    private final BankingService bankingService;

    public Startup(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("Выполняем putMoney, кладем на счет клиента с id=1 сумму 500, чтобы добавить данные в таблицу operation");

        bankingService.putMoney(1, 500.0);

    }
}
