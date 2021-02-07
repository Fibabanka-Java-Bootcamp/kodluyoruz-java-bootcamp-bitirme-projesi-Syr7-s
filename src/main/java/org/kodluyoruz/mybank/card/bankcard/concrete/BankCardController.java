package org.kodluyoruz.mybank.card.bankcard.concrete;

import org.kodluyoruz.mybank.card.bankcard.abstrct.IBankCardService;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotDeletedException;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bankcard")
public class BankCardController {
    private final IBankCardService<BankCard> bankCardService;


    public BankCardController(IBankCardService<BankCard> bankCardService) {
        this.bankCardService = bankCardService;

    }

    @PostMapping("/{customerTC}")
    @ResponseStatus(HttpStatus.CREATED)
    public BankCardDto create(@PathVariable("customerTC") long customerTC, @RequestBody BankCardDto bankCardDto) {

        return bankCardService.create(customerTC, bankCardDto).toBankCardDto();
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

    @DeleteMapping("/{bankCardNO}/process")
    public void bankCardDelete(@PathVariable("bankCardNO") long bankCardNO) {
        try {
            bankCardService.delete(bankCardNO);
        } catch (BankCardNotDeletedException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BankCard is not deleted");
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error");
        }
    }
}
