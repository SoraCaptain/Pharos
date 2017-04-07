package com.iems5722.group1.pharos.fragment.subfragment.person;

import java.util.List;

/**
 * Created by Sora on 16/2/17.
 */
public interface AsyncResponse {
    void onDataReceivedSuccess(String result);
    void onDataReceivedFailed();
}
