package com.ts.banking.controllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper {

    @Builder.Default
    private Date createdDate = Calendar.getInstance().getTime();
    private Object data;

}
