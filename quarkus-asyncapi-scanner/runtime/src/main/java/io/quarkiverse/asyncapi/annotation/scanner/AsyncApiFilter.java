package io.quarkiverse.asyncapi.annotation.scanner;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;

/**
 * AsyncpiFilter
 *
 * @author christiant
 */
public interface AsyncApiFilter {

    default AsyncAPI filterAsyncAPI(AsyncAPI aAsyncAPI) {
        return aAsyncAPI;
    }

    default Channel filterChannel(String aName, Channel aChannel) {
        return aChannel;
    }
}
