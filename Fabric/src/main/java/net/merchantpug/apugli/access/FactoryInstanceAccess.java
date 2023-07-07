package net.merchantpug.apugli.access;

import com.google.gson.JsonElement;

public interface FactoryInstanceAccess {
    JsonElement getJson();
    void setJson(JsonElement json);
}
