package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.Aggregation;
import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZunionstoreOperation;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;
import com.zibobo.yedis.util.DoubleToBytesUtils;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class ZunionstoreOperationImpl extends LongReplyOperationImpl implements
        ZunionstoreOperation {

    private static final byte[] WEIGHTS = toBytes("WEIGHTS");
    private static final byte[] AGGREGATE = toBytes("AGGREGATE");
    private final static VarArgsCommand CMD = new VarArgsCommand("ZUNIONSTORE");
    private final byte[] destination;
    private final byte[][] keys;
    private final double[] weights;
    private final Aggregation aggregate;

    public ZunionstoreOperationImpl(byte[] destination, byte[][] keys,
            double[] weights, Aggregation aggregate, LongReplyCallback cb) {
        super(cb);
        this.destination = destination;
        this.keys = keys;
        this.weights = weights;
        this.aggregate = aggregate;
    }

    @Override
    public void initialize() {
        int count = keys.length + 2; // destination and numkeys + keys
        if (weights != null) {
            count +=  weights.length + 1; // WEIGHTS + weights
        }
        if (aggregate != null) {
            count += 2; // AGGREGATE + aggregate
        }
        byte[][] varargs = new byte[count][];
        int index = 0;
        varargs[index++] = destination;
        varargs[index++] = IntegerToBytesUtils.toBytes(keys.length);
        System.arraycopy(keys, 0, varargs, index, keys.length);
        index += keys.length;

        if (weights != null) {
            varargs[index++] = WEIGHTS;
            for (double weight : weights) {
                varargs[index++] = DoubleToBytesUtils.toBytes(weight);
            }
        }

        if (aggregate != null) {
            varargs[index++] = AGGREGATE;
            varargs[index++] = aggregate.value;
        }
        setVargsArguments(CMD, varargs);
    }

}
