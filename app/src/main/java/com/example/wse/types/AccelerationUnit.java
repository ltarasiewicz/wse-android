package com.example.wse.types;

public enum AccelerationUnit {
    METER_PER_SECOND(1f),
    KILOMETER_PER_HOUR(3.6f);

    private final Float value;

    AccelerationUnit(Float value) {
        this.value = value;
    }

    public static AccelerationUnit fromValue(Float sUnit) {
        for (AccelerationUnit unit : AccelerationUnit.values()) {
            if (unit.getValue().equals(sUnit)) {
                return unit;
            }
        }
        return null;
    }

    public Float getValue() {
        return value;
    }
}
