package com.hercat.mevur.vrcity.service;

public interface RequestListener {
    void success(String response, int responseCode, String identity);
    void error(String error, int responseCode, String identity);
}
