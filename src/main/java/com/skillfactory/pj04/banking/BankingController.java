package com.skillfactory.pj04.banking;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BankingController {

    private final BankingService bankingService;

    @Autowired
    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    //Получаем операции v3
    @GetMapping("/getOperationList")
    public ResponseEntity<List<Operation>> getOperationList(
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Начальная дата (Формат ISO.DATE_TIME: yyyy-MM-dd'T'HH:mm:ss)") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Конечная дата (Формат ISO.DATE_TIME: yyyy-MM-dd'T'HH:mm:ss)") LocalDateTime endDate
    ) {
        List<Operation> ops = bankingService.getOperationList(clientId, startDate, endDate);

        if (ops.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(ops);
    }

    //Передача между клиентами v1

    @PutMapping("/transfer/{idFrom}/{idTo}")
    public ResponseEntity<Map<String, Object>> transferMoney(
            @PathVariable int idFrom,
            @PathVariable int idTo,
            @RequestParam double sum) {

        TransactionStatus status = bankingService.transferMoney(idFrom, idTo, sum);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        response.put("from", idFrom);
        response.put("to", idTo);
        response.put("sum", sum);
        response.put("status", status.name());

        HttpStatus httpStatus;
        switch (status) {
            case SUCCESS -> httpStatus = HttpStatus.OK;
            case INSUFFICIENT_FUNDS -> httpStatus = HttpStatus.BAD_REQUEST;
            case SENDER_NOT_FOUND, RECEIVER_NOT_FOUND -> httpStatus = HttpStatus.NOT_FOUND;
            case NOT_ALLOWED -> httpStatus = HttpStatus.BAD_REQUEST;
            default -> httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        }

        return ResponseEntity.status(httpStatus).body(response);
    }

    //Пополнение баланса v2
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

    //Снимаем деньги с баланса v3

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



}
