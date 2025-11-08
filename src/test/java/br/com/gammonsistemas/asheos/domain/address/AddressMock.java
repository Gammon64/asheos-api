package br.com.gammonsistemas.asheos.domain.address;

import br.com.gammonsistemas.asheos.domain.user.UserMock;

public class AddressMock {
    public static final String ADDRESS_STREET = "Rua Teste";
    public static final String ADDRESS_CITY = "Cidade Teste";
    public static final String ADDRESS_STATE = "ET";
    public static final String ADDRESS_ZIP_CODE = "12345-678";

    public static Address ADDRESS_JOHN_DOE() {
        Address address = new Address();
        address.setStreet(ADDRESS_STREET);
        address.setCity(ADDRESS_CITY);
        address.setState(ADDRESS_STATE);
        address.setZipCode(ADDRESS_ZIP_CODE);
        address.setUser(UserMock.USER_JOHN_DOE());
        return address;
    }

}
