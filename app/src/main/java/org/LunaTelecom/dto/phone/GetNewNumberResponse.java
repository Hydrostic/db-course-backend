package org.LunaTelecom.dto.phone;

import java.util.List;

public class GetNewNumberResponse {
    public List<String> phoneNumbers;

    public GetNewNumberResponse(List<String> numbers) {
        this.phoneNumbers = numbers;
    }
}
