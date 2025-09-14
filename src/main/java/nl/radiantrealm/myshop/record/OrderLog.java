package nl.radiantrealm.myshop.record;

import nl.radiantrealm.myshop.enumerator.OrderType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record OrderLog(int logID, long timestamp, OrderType orderType, BigDecimal orderPrice, UUID playerUUID, Map<String, Integer> materials) {
}
