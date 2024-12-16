package com.example.deploy.global.validation.type;


import com.example.deploy.global.validation.AllOrNoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {AllOrNoneValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AllOrNones.class)
public @interface AllOrNone {

    String message() default "Address, AddressDetail, PostalCode 의 값은 3개가 다 존재하거나 3개가 다 없어야됩니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields();
}
