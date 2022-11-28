package com.johanwedin.urlshortener.controller;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import com.johanwedin.urlshortener.models.sentinels.BadRequestException;

public class UrlValidator {

    public static Validator<String> validator = ValidatorBuilder.<String>of()
            .constraint(String::toString, "url", u -> u.notNull().notEmpty().notBlank().lessThan(256))
            .build();

    public static void validateUrlOrThrow(String candidate, String helpfulParameterName) {
        try {
            ConstraintViolations violations = validator.validate(candidate);
            if (!violations.isValid()) {
                throw new BadRequestException(String.format("Failed to validate %s, %s", helpfulParameterName, violations.get(0).message()));
            }
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(String.format("Failed to validate %s, %s", helpfulParameterName, ex.getMessage()));
        }
    }
}
