package com.fundwise.folio.service;
import com.fundwise.folio.dto.FolioRequest;
import com.fundwise.folio.entity.Folio;
import com.fundwise.folio.exception.NotFoundException;
import com.fundwise.folio.repository.FolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor public class FolioService {
 private final FolioRepository repository;
 public Folio create(FolioRequest r){ return repository.save(copy(r,new Folio())); }
 public List<Folio> all(){ return repository.findAll(); }
 public List<Folio> byInvestor(Long investorId){ return repository.findByInvestorId(investorId); }
 public Folio one(Long id){ return repository.findById(id).orElseThrow(()->new NotFoundException(id)); }
 public Folio update(Long id,FolioRequest r){ return repository.save(copy(r,one(id))); }
 public void delete(Long id){ repository.delete(one(id)); }
 private Folio copy(FolioRequest r,Folio e){ e.setInvestorId(r.investorId());e.setSchemeId(r.schemeId());e.setFolioNumber(r.folioNumber());e.setStatus(r.status());e.setCurrentUnits(r.currentUnits());e.setAverageNav(r.averageNav());e.setCurrentValue(r.currentValue());return e; }
}
