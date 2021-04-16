package com.flyingeffects.com.base.anchortask;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.LogUtil;
import com.orhanobut.hawk.Hawk;
import com.shixing.sxvideoengine.License;
import com.shixing.sxvideoengine.SXLog;
import com.xj.anchortask.library.AnchorTask;

public class VeAnchor extends AnchorTask {

    public VeAnchor() {
        super(TaskNameConstants.INIT_VE);
    }

    @Override
    public void run() {
        String licenseID = "UJ03ctDfZ1ZTWzTF2uC2dmWnOeyD0dk/UhyEu+npLrXEgeMlo2PBMaoHwffFV7bS6O48q0I/8qI4epo2acEbZyiXD1Im4oUNERrPhVtu2nNSnXyjUGr9dLmrYazM4YmNE/A9T6ir5gt3XEs7IjfWftmuZrzgoiAdnGqYyVx8g8ESwumuw/+R8EoMVJ+nfFGI5U4d+RCQqnL58sngu6/6rxxKBh93PYsreRVUfMMndJQGSV0uh0EZrupM9xLlPNMFkZkP9oaTUYeIZLZnu5mNWWDRlgyg80Os1BRSzkp9TG7sb7QJUzFdLvo2cpfhnFyBfRBvoykvllQZaPmbC73J+G+UG2BAZqXtuZB9IGjV3Yga13djLjcvViMCKyu+bftun0lgUaI3Fh2LVRZTvADbVmuMJ/blzjYVVGDT7RNuE55Lo8+uwrTZ/6jkdwh+sKHvMpzHLsIpV55SsyU/XABQ8/36srJP1Ar7GBYSjh7e87KSy4/5Lm5VEiaoL+u9/2bzpw/fOwEoUF1r+18Fy9c3CA2icOcpicntjds0+xd2/fDiJ7YSbs6Iys9slxgT5ukh25B53++VJ60nwD20IKlEtg==";
        License l = License.init(licenseID);
        boolean isValid = l.isValid();
        SXLog.showInLogcat();
        LogUtil.d("OOM", "isValid=" + isValid);
    }
}
