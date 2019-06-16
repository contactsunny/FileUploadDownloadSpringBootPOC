package com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.helpers;

import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.dtos.ResponseDto;
import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.exceptions.FileNotFoundException;
import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.exceptions.FileStorageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This is a global exception class for all RESTful controllers, which resturn ResponseEntity
 * objects. Any exception thrown from methods in such classes can be handled here.
 *
 * This will ensure that for the same kind of exception thrown from any controller class,
 * the HTTP status, and the response schema remains the same for the client.
 *
 * Any new custom exception added as a response to an API, make sure to handle that here with
 * proper HTTP status codes.
 *
 * ==============================================================================================
 * NOTE: Anything other than a zero (0) in the "status" field in the response
 * means there's some error. This is so that if in future we decide to have custom
 * error codes in the response, we can use non-zero integers for different error codes.
 * ==============================================================================================
 */
@ControllerAdvice
public class ControllerExceptionsHelper extends ResponseEntityExceptionHandler {

    /**
     * This exception will be thrown from an API when we are trying to create an object
     * which already exists in a database.
     *
     * For example, while tyring to create a group for a customer, if another group
     * already exists with the same name for the same customer, this exception will be thrown.
     *
     * @param exception the exception object.
     * @return ResponseEntity object.
     */
    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public ResponseDto<Object> processEntityAlreadyExistsException(Exception exception) {

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(1);
        responseDto.setError(exception.getMessage());

        return responseDto;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseDto<Object> processException(Exception exception) {

        exception.printStackTrace();

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(1);
        responseDto.setError(exception.getMessage());

        return responseDto;
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseDto<Object> processFileNotFoundException(Exception exception) {

        exception.printStackTrace();

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(1);
        responseDto.setError(exception.getMessage());

        return responseDto;
    }
}
