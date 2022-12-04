package com.shareexpenses.server.discovery.wise_entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class BalanceStatement implements Serializable {
    public List<Transaction> transactions;
}
