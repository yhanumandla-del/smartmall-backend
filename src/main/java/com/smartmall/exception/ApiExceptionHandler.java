package com.smartmall.exception;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.Map;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<?> bad(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("timestamp",Instant.now(),"message",e.getMessage()));}
 @ExceptionHandler(IllegalStateException.class) ResponseEntity<?> state(IllegalStateException e){return ResponseEntity.unprocessableEntity().body(Map.of("timestamp",Instant.now(),"message",e.getMessage()));}
 @ExceptionHandler(Exception.class) ResponseEntity<?> any(Exception e){return ResponseEntity.status(500).body(Map.of("timestamp",Instant.now(),"message","Unexpected server error."));}
}
