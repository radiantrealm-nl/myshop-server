package nl.radiantrealm.myshop.cache;

import nl.radiantrealm.myshop.Main;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CheckoutSessionCache {
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final ProductStockCache productStockCache = Main.productStockCache;

    private static final Map<UUID, Map<String, Integer>> SESSIONPRODUCTSMAP = new HashMap<>();
    private static final Map<UUID, Long> SESSIONEXPIRYMAP = new HashMap<>();

    static {
        executorService.scheduleAtFixedRate(CheckoutSessionCache::cleanKeys, 0, 10, TimeUnit.SECONDS);
    }

    private CheckoutSessionCache() {}

    private static void cleanKeys() {
        long timestamp = System.currentTimeMillis();

        SESSIONEXPIRYMAP.forEach((key, value) -> {
            if (timestamp > value) {
                destroySessionUUID(key);
            }
        });
    }

    public static UUID createSessionUUID(UUID playerUUID, Map<String, Integer> map) {
        if (playerUUID == null) return null;

        UUID sessionUUID = UUID.randomUUID();
        long expiry = System.currentTimeMillis() + Duration.ofMinutes(5).toMillis();

        SESSIONPRODUCTSMAP.put(playerUUID, map);
        SESSIONEXPIRYMAP.put(playerUUID, expiry);
        return sessionUUID;
    }

    public static Map<String, Integer> getProducts(UUID playerUUID) {
        return SESSIONPRODUCTSMAP.get(playerUUID);
    }

    public static boolean hasCheckoutSession(UUID playerUUID) {
        return SESSIONPRODUCTSMAP.containsKey(playerUUID);
    }

    public static void destroySessionUUID(UUID playerUUID) {
        productStockCache.releaseProducts(SESSIONPRODUCTSMAP.remove(playerUUID));
        SESSIONEXPIRYMAP.remove(playerUUID);
    }
}
