package meepo.transform.sink.redis;

import com.google.common.collect.Lists;
import meepo.transform.channel.DataEvent;
import org.redisson.api.RBatch;

/**
 * Created by peiliping on 17-5-18.
 */
public interface IBuilder {

    public void build(RBatch batch, DataEvent event);

    /*==================================================*/

    public static IBuilder COUPLE = (batch, event) -> batch.getBucket(String.valueOf(event.getTarget()[0])).setAsync(event.getTarget()[1]);

    public static IBuilder ARRAY = (batch, event) -> batch.getList(String.valueOf(event.getTarget()[0])).addAllAsync(0, Lists.newArrayList(event.getTarget()));

}
