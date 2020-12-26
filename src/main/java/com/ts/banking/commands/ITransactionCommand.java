package com.ts.banking.commands;

public interface ITransactionCommand<I extends BaseReq, O extends BaseResp> {

    O execute(I req);

    Class<O> getResponseType();
}
