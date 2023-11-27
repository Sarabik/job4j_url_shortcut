package ru.job4j.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(value = {NullPointerException.class})
    public void handleNullPointerException(Exception e,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", "Some of required fields are empty");
            put("details", e.getMessage());
        }}));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handle(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(
                e.getFieldErrors().stream()
                        .map(f -> Map.of(
                                f.getField(),
                                Objects.requireNonNull(f.getDefaultMessage())
                        ))
                        .collect(Collectors.toList())
        );
    }
}
