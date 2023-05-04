package io.quarkiverse.asyncapi.annotation.scanner;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonView;

@JsonView(TransferRelevant.class)
public class TransferWorkorderMessage extends TransferWorkorderMessageBase {

    private OffsetDateTime bookDate;

    public TransferWorkorderMessage() {
        //for Jackson only
    }

    public TransferWorkorderMessage(Company aCompany, Part aPart,
            OffsetDateTime aBookDate) {
        super(aCompany, aPart);
        bookDate = aBookDate;
    }

    public OffsetDateTime getBookDate() {
        return bookDate;
    }

    @Override
    public String toString() {
        return "TransferWorkorderMessage{"
                + super.toString()
                + "bookDate=" + bookDate
                + '}';
    }

}
