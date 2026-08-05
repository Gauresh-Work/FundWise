package com.fundwise.folio.service;

import com.fundwise.folio.dto.FolioRequest;
import com.fundwise.folio.entity.Folio;
import com.fundwise.folio.exception.NotFoundException;
import com.fundwise.folio.repository.FolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FolioService {
    private final FolioRepository repository;
    private final RestClient.Builder restClientBuilder;

    public Folio create(FolioRequest r) {
        return repository.save(copy(r, new Folio()));
    }

    public List<Folio> all() {
        return repository.findAll();
    }

    public Folio one(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public Folio update(Long id, FolioRequest r) {
        return repository.save(copy(r, one(id)));
    }

    public void delete(Long id) {
        repository.delete(one(id));
    }

    private Folio copy(FolioRequest r, Folio e) {
        validateInvestor(r.investorId());
        BigDecimal currentNav = currentNav(r.schemeId());
        e.setInvestorId(r.investorId());
        e.setSchemeId(r.schemeId());
        e.setFolioNumber(r.folioNumber());
        e.setStatus(r.status());
        e.setCurrentUnits(r.currentUnits());
        // NAV is owned by Scheme Service. Never accept a client-supplied price or value.
        e.setAverageNav(currentNav);
        e.setCurrentValue(r.currentUnits().multiply(currentNav).setScale(2, RoundingMode.HALF_UP));
        return e;
    }

    private void validateInvestor(Long investorId) {
        try {
            restClientBuilder.build().get()
                    .uri("http://investor-service/investors/{id}", investorId)
                    .retrieve().toBodilessEntity();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected investor does not exist");
            }
            throw unavailable("Investor Service", ex);
        } catch (Exception ex) {
            throw unavailable("Investor Service", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private BigDecimal currentNav(Long schemeId) {
        try {
            Map<String, Object> scheme = restClientBuilder.build().get()
                    .uri("http://scheme-service/schemes/{id}", schemeId)
                    .retrieve().body(Map.class);
            Object nav = scheme == null ? null : scheme.get("currentNav");
            if (nav == null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "The selected scheme does not have a current NAV");
            }
            return new BigDecimal(nav.toString());
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected scheme does not exist");
            }
            throw unavailable("Scheme Service", ex);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw unavailable("Scheme Service", ex);
        }
    }

    private ResponseStatusException unavailable(String service, Exception cause) {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                service + " is unavailable; folios cannot be priced right now", cause);
    }
}
