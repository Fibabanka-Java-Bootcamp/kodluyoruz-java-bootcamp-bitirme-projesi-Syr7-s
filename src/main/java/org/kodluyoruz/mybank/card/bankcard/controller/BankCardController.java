package org.kodluyoruz.mybank.card.bankcard.controller;

import org.kodluyoruz.mybank.card.bankcard.dto.BankCardDto;
import org.kodluyoruz.mybank.card.bankcard.entity.BankCard;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotFoundException;
import org.kodluyoruz.mybank.card.bankcard.service.BankCardService;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.service.CustomerService;
import org.kodluyoruz.mybank.generate.accountgenerate.AccountGenerate;
import org.kodluyoruz.mybank.generate.securitycodegenerate.SecurityCodeGenerate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bankcard")
public class BankCardController {
    private final BankCardService bankCardService;
    private final CustomerService customerService;

    public BankCardController(BankCardService bankCardService, CustomerService customerService) {
        this.bankCardService = bankCardService;
        this.customerService = customerService;
    }

    @PostMapping("/{customerID}")
    @ResponseStatus(HttpStatus.CREATED)
    public BankCardDto create(@PathVariable("customerID") long customerID, @RequestBody BankCardDto bankCardDto) {
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        bankCardDto.setBankCardAccountNumber(Long.parseLong(AccountGenerate.accountGenerate.get()));
        bankCardDto.setBankCardNameSurname(customerDto.getCustomerName() + " " + customerDto.getCustomerLastname());
        bankCardDto.setSecurityCode(SecurityCodeGenerate.securityCode.get());
        return bankCardService.create(bankCardDto.toBankCard()).toBankCardDto();
    }

    @GetMapping(value = "/cards", params = {"page", "size"})
    public List<BankCardDto> getAllBankCard(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size) {
        return bankCardService.bankCardPage(PageRequest.of(page, size)).stream()
                .map(BankCard::toBankCardDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{bankCardNO}/card")
    public ResponseEntity<BankCardDto> getBankCard(@PathVariable("bankCardNO") long bankCardNO) {
        try {
            return ResponseEntity.ok(bankCardService.findBankCard(bankCardNO).toBankCardDto());
        } catch (BankCardNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
