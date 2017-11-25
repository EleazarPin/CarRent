package com.example.carrent.carrent.dummy;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.EventoItem;
import com.example.carrent.carrent.com.example.carrent.carrent.commons.TrackingItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<TrackingItem> ITEMS_TRACKING = new ArrayList<TrackingItem>();
    public static final List<EventoItem> ITEMS_EVENTO = new ArrayList<EventoItem>();

    static {
        // Add some sample items.
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));
        addT(TrackingItem.Parse("#-34.67020,-58.56442#22/11/2017#0:54:27.0#64.70m |267.82g |9S#0kmph"));

        addE(EventoItem.Parse("#PrenderLuces#22/11/2017#1:26:19.0"));
        addE(EventoItem.Parse("#ExcesoVelocidad#22/11/2017#1:30:52.0#7S#50kmph"));
        addE(EventoItem.Parse("#Humo#22/11/2017#1:30:52.0"));
    }

    private static void addT(TrackingItem item) {
        ITEMS_TRACKING.add(item);
    }

    private static void addE(EventoItem item) {
        ITEMS_EVENTO.add(item);
    }

}
