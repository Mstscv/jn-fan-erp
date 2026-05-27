package com.jn.erp.warehouse.transfer.service;

public interface IJnTransferService {

    void transfer(Long fromWhId, Long toWhId, Long materialId, String materialCode, Integer qty, String user);
}
