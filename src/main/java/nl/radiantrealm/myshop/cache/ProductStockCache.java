package nl.radiantrealm.myshop.cache;

import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.myshop.Main;
import nl.radiantrealm.myshop.record.ShopProduct;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductStockCache extends CacheRegistry<String, Integer> {
    private final ShopProductCache shopProductCache = Main.shopProductCache;
    private final Map<String, AtomicInteger> bookedProducts = new ConcurrentHashMap<>();

    public ProductStockCache() {
        super(Duration.ofMinutes(5));
    }

    public int bookProduct(String productKey, int quantity) throws Exception {
        int available = get(productKey);

        if (available > 0) {
            int booked = Math.min(available, quantity);
            bookedProducts.computeIfAbsent(productKey, k -> new AtomicInteger()).addAndGet(booked);
            return booked;
        }

        return 0;
    }

    public void releaseProducts(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            AtomicInteger current = bookedProducts.get(entry.getKey());

            if (current == null) {
                continue;
            }

            current.updateAndGet(v -> {
                int next = v - entry.getValue();
                return Math.max(next, 0);
            });

            if (current.get() == 0) {
                bookedProducts.remove(entry.getKey());
            }
        }
    }

    public int getBooked(String key) {
        AtomicInteger integer = bookedProducts.get(key);
        return integer == null ? 0 : integer.get();
    }

    public int available(int stock, int booked) {
        int remaining = stock - booked;
        return Math.max(remaining, 0);
    }

    @Override
    public Integer get(String key) throws Exception {
        Integer stock = super.get(key);
        int booked = getBooked(key);
        return available(stock, booked);
    }

    @Override
    public Map<String, Integer> get(List<String> keys) throws Exception {
        Map<String, Integer> stock = super.get(keys);
        Map<String, Integer> result = new HashMap<>(stock.size());

        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            int booked = getBooked(entry.getKey());
            result.put(entry.getKey(), available(entry.getValue(), booked));
        }

        return result;
    }

    @Override
    protected Integer load(String string) throws Exception {
        ShopProduct shopProduct = shopProductCache.get(string);
        return shopProduct.availability();
    }

    @Override
    protected Map<String, Integer> load(List<String> keys) throws Exception {
        Map<String, ShopProduct> map = shopProductCache.load(keys);
        Map<String, Integer> result = new HashMap<>(map.size());

        for (ShopProduct shopProduct : map.values()) {
            result.put(shopProduct.productKey(), shopProduct.availability());
        }

        return result;
    }

    @Override
    public void remove(String key, Integer value) {
        super.remove(key, value);
        int booked = bookedProducts.get(key).get();
        if (booked < 1) {
            bookedProducts.remove(key);
        }
    }
}
