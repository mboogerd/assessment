package intergamma.stock.api;

import javassist.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Optional;

/**
 * Enables returning a 404 error response when `Optional` returning methods return empty.
 *
 * Adapted from http://dev-maziarz.blogspot.com/2018/02/spring-mvc-redirect-404-when.html
 */
@ControllerAdvice
public class OptionalResponseControllerAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getParameterType().equals(Optional.class);
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (returnType.getParameterType().equals(Optional.class)) {
            return ((Optional<?>) body).orElseThrow(() -> new NotFoundException(request.getURI().toString()));
        }
        return body;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handle(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}