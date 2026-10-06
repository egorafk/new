package app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdvanceController {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.13");

    public record CalcRequest(BigDecimal salary, BigDecimal monthDays, BigDecimal workedDays) {}

    public record CalcResponse(BigDecimal gross, BigDecimal tax, BigDecimal net) {}

    /**
     * (оклад / рабочих дней в месяце) * мои рабочие дни, затем минус 13%.
     */
    @PostMapping("/calculate")
    public CalcResponse calculate(@RequestBody CalcRequest req) {
        if (req.salary() == null || req.monthDays() == null || req.workedDays() == null) {
            throw new IllegalArgumentException("Заполните все поля");
        }
        if (req.salary().signum() <= 0 || req.monthDays().signum() <= 0 || req.workedDays().signum() <= 0) {
            throw new IllegalArgumentException("Все значения должны быть больше нуля");
        }
        if (req.workedDays().compareTo(req.monthDays()) > 0) {
            throw new IllegalArgumentException("Отработанных дней не может быть больше, чем в месяце");
        }

        BigDecimal perDay = req.salary().divide(req.monthDays(), 10, RoundingMode.HALF_UP);
        BigDecimal gross = perDay.multiply(req.workedDays()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = gross.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal net = gross.subtract(tax);

        return new CalcResponse(gross, tax, net);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadable() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Заполните все поля числами"));
    }
}
