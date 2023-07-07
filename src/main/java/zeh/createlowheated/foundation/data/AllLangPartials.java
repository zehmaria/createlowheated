package zeh.createlowheated.foundation.data;

import com.google.common.base.Supplier;
import com.google.gson.JsonElement;
import com.simibubi.create.foundation.data.LangPartial;
import com.simibubi.create.foundation.utility.Lang;
import zeh.createlowheated.CreateLowHeated;

public enum AllLangPartials implements LangPartial {

    INTERFACE("UI & Messages"),
    TOOLTIPS("Item Descriptions"),

    ;

    private final String displayName;
    private final Supplier<JsonElement> provider;

    private AllLangPartials(String displayName) {
        this.displayName = displayName;
        String fileName = Lang.asId(name());
        this.provider = () -> LangPartial.fromResource(CreateLowHeated.ID, fileName);
    }

    private AllLangPartials(String displayName, Supplier<JsonElement> provider) {
        this.displayName = displayName;
        this.provider = provider;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public JsonElement provide() {
        return provider.get();
    }

}