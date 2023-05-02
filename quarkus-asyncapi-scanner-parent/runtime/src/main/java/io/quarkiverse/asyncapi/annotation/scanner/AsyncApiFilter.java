package io.quarkiverse.asyncapi.annotation.scanner;

import com.asyncapi.v2.model.AsyncAPI;
import com.asyncapi.v2.model.channel.ChannelItem;

/**
 * AsyncpiFilter
 *
 * @author christiant
 */
public interface AsyncApiFilter {

    default AsyncAPI filterAsyncAPI(AsyncAPI aAsyncAPI) {
        return aAsyncAPI;
    }

    default ChannelItem filterChannelItem(String aChannel, ChannelItem aChannelItem) {
        return aChannelItem;
    }
}
