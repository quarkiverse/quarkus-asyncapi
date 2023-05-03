package io.quarkiverse.asyncapi.annotation.scanner;

public abstract class TransferWorkorderMessageBase {

    private Company company;
    private Part part;

    public TransferWorkorderMessageBase() {
        //for Jackson only
    }

    public TransferWorkorderMessageBase(Company aCompany, Part aPart) {
        company = aCompany;
        part = aPart;
    }

    public Company getCompany() {
        return company;
    }

    public Part getPart() {
        return part;
    }

}
