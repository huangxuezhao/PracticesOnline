package net.lzzy.practicesonline.activities.practicesonline.constants;

import net.lzzy.practicesonline.activities.practicesonline.utils.AppUtils;

/**
 * Created by lzzy_gxy on 2019/4/15.
 * Description:
 */
public class ApiConstants {
    private static final String IP= AppUtils.loadServerSetting(AppUtils.getContext()).first;
    private static final String PORT=AppUtils.loadServerSetting(AppUtils.getContext()).second;
    private static final String PROTCOL="http://";
    public static final String URL_API=PROTCOL.concat(IP).concat(":").concat(PORT);

}
