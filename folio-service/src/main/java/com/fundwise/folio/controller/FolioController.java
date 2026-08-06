package com.fundwise.folio.controller;
import com.fundwise.folio.dto.FolioRequest; import com.fundwise.folio.entity.Folio; import com.fundwise.folio.service.FolioService;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/folios") @RequiredArgsConstructor public class FolioController {
 private final FolioService service;
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Folio create(@Valid @RequestBody FolioRequest r){return service.create(r);}
 @GetMapping public List<Folio> all(){return service.all();}
 @GetMapping("/investor/{investorId}") public List<Folio> byInvestor(@PathVariable Long investorId){return service.byInvestor(investorId);}
 @GetMapping("/{id}") public Folio one(@PathVariable Long id){return service.one(id);}
 @PutMapping("/{id}") public Folio update(@PathVariable Long id,@Valid @RequestBody FolioRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
