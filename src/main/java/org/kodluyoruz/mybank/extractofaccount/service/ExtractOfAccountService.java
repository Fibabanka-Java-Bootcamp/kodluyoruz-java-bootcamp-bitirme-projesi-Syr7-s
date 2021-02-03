package org.kodluyoruz.mybank.extractofaccount.service;

import org.kodluyoruz.mybank.extractofaccount.entity.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.repository.ExtractOfAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class ExtractOfAccountService {
    private final ExtractOfAccountRepository extractOfAccountRepository;

    public ExtractOfAccountService(ExtractOfAccountRepository extractOfAccountRepository) {
        this.extractOfAccountRepository = extractOfAccountRepository;
    }

    public ExtractOfAccount create(ExtractOfAccount extractOfAccount){
        return extractOfAccountRepository.save(extractOfAccount);
    }
}
