package org.LunaTelecom.dto.common;

import io.javalin.http.Context;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.LunaTelecom.http.validator.ValidationException;
import org.LunaTelecom.http.validator.ValidatorUtils;

public class PagerRequest {
    @Positive
    public Integer page;
    @Positive
    public Integer size;
    public PagerRequest(Context ctx) throws ValidationException {
        this.page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        this.size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(10);
        ValidatorUtils.validate(this);
    }
    public long getOffset() {
        return (long) (page - 1) * size;
    }
}
