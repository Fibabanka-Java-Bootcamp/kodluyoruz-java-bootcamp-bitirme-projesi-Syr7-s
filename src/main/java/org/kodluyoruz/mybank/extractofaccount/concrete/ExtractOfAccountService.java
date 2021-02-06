package org.kodluyoruz.mybank.extractofaccount.concrete;


import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountRepository;
import org.kodluyoruz.mybank.extractofaccount.abstrct.IExtractOfAccountService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExtractOfAccountService implements IExtractOfAccountService<ExtractOfAccount> {
    private final ExtractOfAccountRepository extractOfAccountRepository;

    public ExtractOfAccountService(ExtractOfAccountRepository extractOfAccountRepository) {
        this.extractOfAccountRepository = extractOfAccountRepository;
    }

    @Override
    public ExtractOfAccount create(ExtractOfAccount extractOfAccount) {
        return extractOfAccountRepository.save(extractOfAccount);
    }

    @Override
    public Optional<ExtractOfAccount> get(int extractNo) {
        return extractOfAccountRepository.findById(extractNo);
    }

    @Override
    public ExtractOfAccount update(ExtractOfAccount extractOfAccount) {
        return extractOfAccountRepository.save(extractOfAccount);
    }
}
