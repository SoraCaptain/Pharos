package com.iems5722.group1.pharos.module.chat.video_sdk;

// AnyChat Core SDK事件通知接口
public interface AnyChatCoreSDKEvent {
    public void OnAnyChatCoreSDKEvent(int dwEventType, String szJsonStr);
}