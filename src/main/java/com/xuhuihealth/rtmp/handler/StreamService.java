package com.xuhuihealth.rtmp.handler;

import java.io.IOException;

//@Service
public class StreamService {

    //@Async
    public void play(String streamUrl,Long i) {
        try {
            new ConvertVideoPakcet().from(streamUrl)
                    .to("rtmp://localhost:1935/live/"+i)
                    .go();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
