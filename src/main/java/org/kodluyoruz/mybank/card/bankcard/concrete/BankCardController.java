package org.kodluyoruz.mybank.card.bankcard.concrete;

import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.card.bankcard.abstrct.BankCardService;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotDeletedException;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotFoundException;
import org.kodluyoruz.mybank.utilities.enums.messages.Messages;
import org.kodluyoruz.mybank.utilities.messages.ErrorMessages;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bankcard")
public class BankCardController {
    private final BankCardService<BankCard> bankCardService;
    private static final Logger log = Logger.getLogger(BankCardController.class);

    public BankCardController(BankCardService<BankCard> bankCardService) {
        this.bankCardService = bankCardService;

    }

    @PostMapping("/{customerTC}")
    @ResponseStatus(HttpStatus.CREATED)
    public BankCardDto create(@PathVariable("customerTC") long customerTC, @RequestBody BankCardDto bankCardDto) {
        log.info("Bank card will create.");
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
            log.info(bankCardNO + " bank card will get.");
            return ResponseEntity.ok(bankCardService.findBankCard(bankCardNO).toBankCardDto());
        } catch (BankCardNotFoundException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception exception) {
            log.error(Messages.Error.SERVER_ERROR.message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{bankCardNO}/process")
    public String bankCardDelete(@PathVariable("bankCardNO") long bankCardNO) {
        try {
            log.info("Bank card will delete.");
            return bankCardService.delete(bankCardNO);
        } catch (BankCardNotDeletedException exception) {
            log.error(Messages.Error.CARD_COULD_NOT_DELETED.message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Messages.Error.CARD_COULD_NOT_DELETED.message);
        } catch (RuntimeException exception) {
            log.error(Messages.Error.SERVER_ERROR.message);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.SERVER_ERROR.message);
        }
    }
}
