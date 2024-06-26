package uk.co.cloudmatica.proxyapi.handler.exceptions;

public class CompanyNotFoundException extends Exception {

    public CompanyNotFoundException(final String message) {
        super(message);
    }
}
