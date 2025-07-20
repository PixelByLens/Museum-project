package video.transformer.backend.utils;

import java.text.DecimalFormat;

public class NumberUtil {

    public static String fix2Point(double v) {
        DecimalFormat format = new DecimalFormat("#0.00");
        return format.format(v);
    }
}
