package nl.radiantrealm.myshop.cache;

import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.myshop.Main;
import nl.radiantrealm.myshop.record.ShopProduct;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductPriceCache extends CacheRegistry<String, BigDecimal> {
    private final ShopProductCache shopProductCache = Main.shopProductCache;

    public ProductPriceCache() {
        super(Duration.ofMinutes(5));
    }

    @Override
    protected BigDecimal load(String string) throws Exception {
        ShopProduct shopProduct = shopProductCache.get(string);
        return shopProduct.price();
    }

    @Override
    protected Map<String, BigDecimal> load(List<String> keys) throws Exception {
        Map<String, ShopProduct> map = shopProductCache.load(keys);
        Map<String, BigDecimal> result = new HashMap<>(map.size());

        for (ShopProduct shopProduct : map.values()) {
            result.put(shopProduct.productKey(), shopProduct.price());
        }

        return result;
    }
}
