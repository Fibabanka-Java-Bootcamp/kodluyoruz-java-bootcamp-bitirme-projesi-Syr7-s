package org.kodluyoruz.mybank.extractofaccount.service;

import org.kodluyoruz.mybank.extractofaccount.entity.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.repository.ExtractOfAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExtractOfAccountService {
    private final ExtractOfAccountRepository extractOfAccountRepository;

    public ExtractOfAccountService(ExtractOfAccountRepository extractOfAccountRepository) {
        this.extractOfAccountRepository = extractOfAccountRepository;
    }

    public ExtractOfAccount create(ExtractOfAccount extractOfAccount) {
        return extractOfAccountRepository.save(extractOfAccount);
    }

    public Optional<ExtractOfAccount> get(int extractNo) {
        return extractOfAccountRepository.findById(extractNo);
    }

    public ExtractOfAccount update(ExtractOfAccount extractOfAccount) {
        return extractOfAccountRepository.save(extractOfAccount);
    }
}
