package com.example.steff.sharedeffort;

import org.json.JSONObject;

public interface IEventNotifier {
    void RequestComplete(JSONObject jsonObject);
}
