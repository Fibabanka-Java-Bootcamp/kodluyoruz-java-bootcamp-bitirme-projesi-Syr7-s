package org.kodluyoruz.mybank.extractofaccount.concrete;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountRepository;
import org.kodluyoruz.mybank.extractofaccount.exception.ExtractOfAccountNotFound;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExtractOfAccountServiceImplTest {

    @Mock
    ExtractOfAccountRepository extractOfAccountRepository;

    @InjectMocks
    ExtractOfAccountServiceImpl extractOfAccountService;

    static ExtractOfAccount extractOfAccount = new ExtractOfAccount();

    @BeforeAll
    static void init() {
        extractOfAccount.setId(2);
        extractOfAccount.setTermDebt(1500);
        extractOfAccount.setOldDebt(2000);
        extractOfAccount.setMinimumPaymentAmount(500);
        extractOfAccount.setOldMinimumPaymentAmount(0);
        extractOfAccount.setShoppingInterestRate(1.79);
        extractOfAccount.setLateInterestRate(2.09);
        extractOfAccount.setShoppingInterestAmount(12.45);
        extractOfAccount.setShoppingInterestAmountNext(0);
        extractOfAccount.setLateInterestAmount(15.889);
        extractOfAccount.setTotalInterestAmount(28.339);
        extractOfAccount.setAccountCutOffTime(LocalDate.of(2021, 2, 18));
        extractOfAccount.setPaymentDueTo(LocalDate.of(2021, 2, 28));
        extractOfAccount.setBankRate(2.12);
        extractOfAccount.setCreditCard(null);
    }

    @BeforeEach
    void setMockOutput() {
        extractOfAccountService = new ExtractOfAccountServiceImpl(extractOfAccountRepository);
        extractOfAccountRepository.save(extractOfAccount);

        Mockito.when(extractOfAccountRepository.findById(extractOfAccount.getId())).thenReturn(java.util.Optional.ofNullable(extractOfAccount));
        assert extractOfAccount != null;
    }

    @Test()
    @DisplayName("ExtractOfAccount info will get with extractID and some of test will control.")
    void get() {
        assertTrue(extractOfAccountService.get(extractOfAccount.getId())
                .orElseThrow(() -> new ExtractOfAccountNotFound("Extract Of Account not found."))
                .getMinimumPaymentAmount() > 0);
    }

    @Test
    void creditCardIsNull() {
        assertNull(extractOfAccountService.get(extractOfAccount.getId())
                .orElseThrow(() -> new ExtractOfAccountNotFound("Extract Of Account not found."))
                .getCreditCard());
    }
}