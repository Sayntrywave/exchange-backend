package com.korotkov.exchange.model;

public enum TradeStatus {

    PENDING("ожидает ответа"),
    REJECTED("отклонен"),
    COMPLETED("совершена");

    private final String label;

    TradeStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}