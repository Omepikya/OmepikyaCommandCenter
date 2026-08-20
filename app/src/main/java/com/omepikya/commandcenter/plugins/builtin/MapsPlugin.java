package com.omepikya.commandcenter.plugins.builtin;

import android.content.Context;
import android.net.Uri;

import com.omepikya.commandcenter.core.CommandContext;

public final class MapsPlugin
        extends UriPlugin {

    public MapsPlugin(
            Context c
    ) {

        super(c);
    }

    @Override
    public String getId() {
        return "maps";
    }

    @Override
    public String getName() {
        return "Maps";
    }

    @Override
    protected String[] keywords() {

        return new String[]{
                "maps",
                "navigate",
                "directions",
                "map"
        };
    }

    @Override
    protected String uri() {

        return "geo:0,0?q=";
    }

    @Override
    protected String uriForCommand(
            CommandContext c
    ) {

        String query =
                extractQuery(c);

        if (query.isEmpty()) {
            return uri();
        }

        return "geo:0,0?q="
                + Uri.encode(query);
    }
}