package meepo.transform.source.rdb;

import meepo.transform.channel.RingbufferChannel;
import meepo.transform.config.TaskContext;
import meepo.util.Util;
import meepo.util.dao.BasicDao;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by peiliping on 17-3-7.
 */
public class DBSyncByIdSource extends DBByIdSource {

    protected Pair<Long, Long> startEnd;

    protected String rollingSql;

    public DBSyncByIdSource(String name, int index, int totalNum, TaskContext context, RingbufferChannel rb) {
        super(name, index, totalNum, context, rb);
        Validate.isTrue(totalNum == 1);
        super.end = Long.MAX_VALUE;
        this.rollingSql = BasicDao.buildAutoGetStartEndSql(super.tableName, super.primaryKeyName);
        this.startEnd = BasicDao.autoGetStartEndPoint(super.dataSource, super.tableName, super.primaryKeyName, this.rollingSql);
        super.start = context.getLong("start", this.startEnd.getRight());
        super.currentPos = super.start;
    }

    @Override public void work() {
        this.startEnd = BasicDao.autoGetStartEndPoint(super.dataSource, super.tableName, super.primaryKeyName, this.rollingSql);
        super.tmpEnd = (this.startEnd.getRight() - super.currentPos >= super.stepSize) ? super.currentPos + super.stepSize : this.startEnd.getRight();
        if (executeQuery()) {
            super.currentPos = super.tmpEnd;
        } else {
            Util.sleep(1);
        }
    }
}
