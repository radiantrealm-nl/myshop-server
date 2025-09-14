package nl.radiantrealm.myshop.record;

import java.util.Map;
import java.util.UUID;

public record OrderPickup(UUID playerUUID, Map<String, Integer> materials) {
}
