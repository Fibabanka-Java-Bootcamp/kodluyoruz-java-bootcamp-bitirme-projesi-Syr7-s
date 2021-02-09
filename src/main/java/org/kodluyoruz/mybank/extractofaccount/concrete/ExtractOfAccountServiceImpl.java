package org.kodluyoruz.mybank.extractofaccount.concrete;


import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountRepository;
import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExtractOfAccountServiceImpl implements ExtractOfAccountService<ExtractOfAccount> {
    private final ExtractOfAccountRepository extractOfAccountRepository;

    public ExtractOfAccountServiceImpl(ExtractOfAccountRepository extractOfAccountRepository) {
        this.extractOfAccountRepository = extractOfAccountRepository;
    }

    @Override
    public ExtractOfAccount create(ExtractOfAccount extractOfAccount) {
        return extractOfAccountRepository.save(extractOfAccount);
    }

    @Override
    public Optional<ExtractOfAccount> get(int extractNO) {
        return extractOfAccountRepository.findById(extractNO);
    }

    @Override
    public ExtractOfAccount update(ExtractOfAccount extractOfAccount) {
        return extractOfAccountRepository.save(extractOfAccount);
    }
}
