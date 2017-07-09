package com.pedro.builder.rtmp;

import android.media.MediaCodec;
import android.util.Log;
import android.view.SurfaceView;
import com.pedro.builder.base.BuilderBase;
import java.nio.ByteBuffer;
import net.ossrs.rtmp.ConnectCheckerRtmp;
import net.ossrs.rtmp.SrsFlvMuxer;

/**
 * Created by pedro on 25/01/17.
 */

public class RtmpBuilder extends BuilderBase {

  private SrsFlvMuxer srsFlvMuxer;

  public RtmpBuilder(SurfaceView surfaceView, ConnectCheckerRtmp connectChecker) {
    super(surfaceView);
    srsFlvMuxer = new SrsFlvMuxer(connectChecker);
  }

  @Override
  public void setAuthorization(String user, String password) {
    srsFlvMuxer.setAuthorization(user, password);
  }

  @Override
  protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
    srsFlvMuxer.setIsStereo(isStereo);
    srsFlvMuxer.setSampleRate(sampleRate);
  }

  @Override
  public boolean prepareAudio() {
    microphoneManager.createMicrophone();
    return audioEncoder.prepareAudioEncoder();
  }

  @Override
  protected void startStreamRtp(String url) {
    srsFlvMuxer.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
    srsFlvMuxer.start(url);
  }

  @Override
  protected void stopStreamRtp() {
    srsFlvMuxer.stop();
  }

  @Override
  protected void getAccDataRtp(ByteBuffer accBuffer, MediaCodec.BufferInfo info) {
    srsFlvMuxer.sendAudio(accBuffer, info);
  }

  @Override
  protected void onSPSandPPSRtp(ByteBuffer sps, ByteBuffer pps) {
    srsFlvMuxer.setSpsPPs(sps, pps);
  }

  @Override
  protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
    Log.i("Builder", "send h264 size:" + h264Buffer.remaining());
    srsFlvMuxer.sendVideo(h264Buffer, info);
  }
}
