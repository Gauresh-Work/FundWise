package com.fundwise.transaction.controller;
import com.fundwise.transaction.dto.TransactionRequest; import com.fundwise.transaction.entity.Transaction; import com.fundwise.transaction.service.TransactionService;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/transactions") @RequiredArgsConstructor public class TransactionController {
 private final TransactionService service;
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Transaction create(@Valid @RequestBody TransactionRequest r){return service.create(r);}
 @GetMapping public List<Transaction> all(@RequestParam(required=false) Long folioId){return service.all(folioId);}
 @GetMapping("/{id}") public Transaction one(@PathVariable Long id){return service.one(id);}
 @PutMapping("/{id}") public Transaction update(@PathVariable Long id,@Valid @RequestBody TransactionRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
