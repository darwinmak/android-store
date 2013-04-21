package com.soomla.store.domain.virtualGoods;

import com.soomla.store.StoreUtils;
import com.soomla.store.data.JSONConsts;
import com.soomla.store.data.StorageManager;
import com.soomla.store.data.StoreInfo;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.soomla.store.purchaseTypes.PurchaseType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * SingleUsePacks are just bundles of SingleUse virtual goods.
 * This kind of virtual good can be used to let your users buy more than one SingleUseVG at once.
 *
 * The SingleUsePackVG's characteristics are:
 *  1. Can be purchased unlimited number of times.
 *  2. Doesn't Have a balance in the database. The SingleUseVG there's associated with this pack has its own balance. When
 *      your users buy a SingleUsePackVG, the balance of the associated SingleUseVG goes up in the amount that this pack
 *      represents (mGoodAmount).
 *
 *  - Usage Examples: 'Box Of Chocolates', '10 Swords'
 *
 * This VirtualItem is purchasable.
 * In case you purchase this item in Google Play (PurchaseWithMarket), You need to define the google item in Google
 * Play Developer Console. (https://play.google.com/apps/publish)
 */
public class SingleUsePackVG extends VirtualGood {

    /** Constructor
     *
     * @param good is the SingleUseVG associated with this pack.
     * @param amount is the number of SingleUseVG in the pack.
     * @param name see parent
     * @param description see parent
     * @param itemId see parent
     * @param purchaseType see parent
     */
    public SingleUsePackVG(SingleUseVG good, int amount,
                           String name, String description,
                           String itemId, PurchaseType purchaseType) {
        super(name, description, itemId, purchaseType);

        mGood = good;
        mGoodAmount = amount;
    }

    /** Constructor
     *
     * see parent
     */
    public SingleUsePackVG(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        String goodItemId = jsonObject.getString(JSONConsts.VGP_GOOD_ITEMID);
        mGoodAmount = jsonObject.getInt(JSONConsts.VGP_GOOD_AMOUNT);

        try {
            mGood = (SingleUseVG) StoreInfo.getVirtualItem(goodItemId);
        } catch (VirtualItemNotFoundException e) {
            StoreUtils.LogError(TAG, "Tried to fetch virtual item with itemId '" + goodItemId + "' but it didn't exist.");
        }
    }

    /**
     * see parent
     */
    @Override
    public JSONObject toJSONObject() {
        JSONObject parentJsonObject = super.toJSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            Iterator<?> keys = parentJsonObject.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                jsonObject.put(key, parentJsonObject.get(key));
            }

            jsonObject.put(JSONConsts.VGP_GOOD_ITEMID, mGood.getItemId());
            jsonObject.put(JSONConsts.VGP_GOOD_AMOUNT, mGoodAmount);
        } catch (JSONException e) {
            StoreUtils.LogError(TAG, "An error occurred while generating JSON object.");
        }

        return jsonObject;
    }

    /**
     * see parent
     * @param amount the amount of the specific item to be given.
     */
    @Override
    public void give(int amount) {
        StorageManager.getVirtualGoodsStorage().add(mGood, mGoodAmount*amount);
    }

    /**
     * see parent
     * @param amount the amount of the specific item to be taken.
     */
    @Override
    public void take(int amount) {
        StorageManager.getVirtualGoodsStorage().remove(mGood, mGoodAmount*amount);
    }

    /**
     * see parent
     */
    @Override
    protected boolean canBuy() {
        return true;
    }

    private static final String TAG = "SOOMLA SingleUsePackVG";

    private SingleUseVG mGood;
    private int         mGoodAmount;
}