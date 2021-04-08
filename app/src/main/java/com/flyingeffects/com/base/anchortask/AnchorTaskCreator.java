package com.flyingeffects.com.base.anchortask;

import com.xj.anchortask.library.AnchorTask;
import com.xj.anchortask.library.IAnchorTaskCreator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnchorTaskCreator implements IAnchorTaskCreator {
    @Override
    public @Nullable AnchorTask createTask(@NotNull String s) {
        if (TaskNameConstants.INIT_YOU_MENG.equals(s)) {
            return new YouMengAnchor();
        } else if (TaskNameConstants.MULTI_DEX.equals(s)) {
            return new MultiAnchor();
        } else if (TaskNameConstants.INIT_LAN_SONG.equals(s)) {
            return new LanSongAnchor();
        } else if (TaskNameConstants.INIT_HAWK.equals(s)) {
            return new HawkAnchor();
        } else if (TaskNameConstants.INIT_VE.equals(s)) {
            return new VeAnchor();
        } else if (TaskNameConstants.INIT_JPUSH.equals(s)) {
            return new JPushAnchor();
        } else if (TaskNameConstants.INIT_ZT.equals(s)) {
            return new ZtAnchor();
        } else if (TaskNameConstants.INIT_SHAN_YAN.equals(s)) {
            return new ShanYanAnchor();
        } else if (TaskNameConstants.INIT_BYTE_DANCE_SHARE.equals(s)) {
            return new ByteDanceShareAnchor();
        } else if (TaskNameConstants.INIT_AD_SDK.equals(s)) {
            return new NtAdAnchor();
        } else if (TaskNameConstants.INIT_TTAD.equals(s)) {
            return new TtAdAnchor();
        } else if (TaskNameConstants.INIT_EMOJI.equals(s)) {
            return new EmojiAnchor();
        } else if (TaskNameConstants.INIT_ALBUM.equals(s)) {
            return new AlbumAnchor();
        } else if (TaskNameConstants.INIT_FU.equals(s)) {
            return new FuAnchor();
        }
        return null;
    }
}
