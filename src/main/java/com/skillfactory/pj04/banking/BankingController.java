package com.skillfactory.pj04.banking;


import com.skillfactory.pj04.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class BankingController {

    private final BankingService bankingService;

    @Autowired
    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    //Передача между клиентами v1


    //Пополнение баланса v1
    @PutMapping("/putMoney/{id}")
    public ResponseEntity<Map<String, Object>> putMoney(
            @PathVariable int id,
            @RequestParam double sum) {

        TransactionStatus status = bankingService.putMoney(id, sum);

        Map<String, Object> response = new HashMap<>();
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        response.put("timestamp", timestamp);

        switch (status) {
            case SUCCESS:
                response.put("success", true);
                response.put("message", "Пополнение успешно выполнено");
                response.put("balance", bankingService.getBalance(id));
                return ResponseEntity.ok(response);

            case CLIENT_NOT_FOUND:
                response.put("success", false);
                response.put("message", "Клиент не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

            default:
                throw new IllegalStateException("Неизвестная ошибка");
        }
    }

    //Снимаем деньги с баланса v2

    @PutMapping("/takeMoney/{id}")
    public ResponseEntity<Map<String, Object>> takeMoney(
            @PathVariable int id,
            @RequestParam double sum) {

        TransactionStatus status = bankingService.takeMoney(id, sum);

        Map<String, Object> response = new HashMap<>();
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        response.put("timestamp", timestamp);

        switch (status) {
            case SUCCESS:
                response.put("success", true);
                response.put("message", "Списание успешно выполнено");
                response.put("balance", bankingService.getBalance(id));
                return ResponseEntity.ok(response);

            case INSUFFICIENT_FUNDS:
                response.put("success", false);
                response.put("message", "Недостаточно средств");
                response.put("balance", bankingService.getBalance(id));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

            case CLIENT_NOT_FOUND:
                response.put("success", false);
                response.put("message", "Клиент не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

            default:
                throw new IllegalStateException("Неизвестная ошибка");
        }
    }

    //Получение баланса v4
    @GetMapping("/getBalance/{id}")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable int id) {
        double clientBalance = bankingService.getBalance(id);

        Map<String, Object> response = new HashMap<>();
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        response.put("timestamp", timestamp);

        if (Double.isNaN(clientBalance)) {
            response.put("status", "error");
            response.put("message", "Клиент с ID " + id + " не найден");
            response.put("balance", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("status", "success");
        response.put("message", "Баланс клиента №" + id);
        response.put("balance", clientBalance);
        return ResponseEntity.ok(response);
    }

    //Получение баланса v2
//    @GetMapping("/getBalance/{id}")
//    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable int id) {
//        double clientBalance = bankingService.getBalance(id);
//
//        Map<String, Object> response = new HashMap<>();
//
//        if (Double.isNaN(clientBalance)) {
//            response.put("status", "error");
//            response.put("message", "Клиент с ID " + id + " не найден");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//
//        response.put("status", "success");
//        response.put("balance", clientBalance);
//        return ResponseEntity.ok(response);
//    }



}
