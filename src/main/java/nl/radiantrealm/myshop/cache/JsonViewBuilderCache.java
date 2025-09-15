package nl.radiantrealm.myshop.cache;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.myshop.Main;
import nl.radiantrealm.myshop.record.ShopProduct;

import java.time.Duration;

public class JsonViewBuilderCache {
    public static final ProductOverview productOverview = new ProductOverview();

    private JsonViewBuilderCache() {}

    public static class ProductOverview extends CacheRegistry<String, JsonObject> {

        public ProductOverview() {
            super(Duration.ofMinutes(5));
        }

        @Override
        protected JsonObject load(String key) throws Exception {
            ShopProduct product = Main.shopProductCache.get(key);

            JsonObject object = new JsonObject();
            object.addProperty("product_key", key);
            object.addProperty("product_name", product.productName());
            object.addProperty("product_icon", product.productIcon().name());
            object.addProperty("product_price", Main.productPriceCache.get(key));
            object.addProperty("product_stock", Main.productStockCache.get(key));
            return object;
        }
    }

    public static class CategoryOverview {
    }
}
